package herbivore.res;
import herbivore.config.BuildInfo;
import herbivore.misc.Logger;
import herbivore.script.ScriptFile;
import herbivore.sound.Sound;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import nu.xom.Document;
import org.ini4j.Ini;
import org.newdawn.slick.Image;

/**
 * a template class for implementation by loadable resources, as well
 * as the primary method of resource retrieval
 * @author herbivore
 */
public abstract class Resource {
    
    /**
     * @param location the location of the resource
     * @return the resource reference
     */
    public static Resource getResource(String location){
        Resource resource = new StandardResource(location);
        return checkForArchive(resource, location);
    }
    
    /**
     * @param location the location of the customizable resource, from the <code>res/</code>
     * directory or current level folder
     * @return the resource reference
     */
    public static Resource getCustomizableResource(String location){
        Resource resource = new CustomizableResource(location);
        return checkForArchive(resource, location);
    }
     
    /**
     * checks whether or not a resource is an archive, and loads it as one if it is
     * @param resource the resource to check
     * @param location the location of the resource
     * @return the now valid resource
     */
    protected static Resource checkForArchive(Resource resource, String location){
        if (isLocationArchive(location)){
            resource = resource.loadAsArchiveFile();
        }
        return resource;
    }
    
    /**
     * @param location the location to check
     * @return whether or not the location belongs to an archive recognized by this program
     */
    private static boolean isLocationArchive(String location){
        for (String check : BuildInfo.getSupportedArchives()){
            if (location.endsWith(check)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return the file reference to the resource
     */
    protected abstract File getResourceFile();
    
    /**
     * gets a sub-resource of this resource for loading
     * @param location the relative location of the resource
     * @return the new resource
     */
    protected abstract Resource getSubResourceForLoad(String location);
    
    /**
     * gets a sub-resource of this resource
     * @param location the relative location of the resource
     * @return the new resource
     */
    public Resource getSubResource(String location){
        Resource resource = getSubResourceForLoad(location);
        return checkForArchive(resource, location);
    }
     
    /**
     * @return whether or not this resource exists
     */
    public boolean exists(){
        File file = getResourceFile();
        return file != null && file.exists();
    }
    
    /**
     * @return an input stream of this resource
     */
    protected InputStream getResourceInputStream(){
        File resourceFile = getResourceFile();
        if (resourceFile != null){
            try {
                return new FileInputStream(resourceFile);
            }
            catch (FileNotFoundException exception){
                Logger.error(exception, "error in file system");
            }
        }
        return null;
    }
        
    /**
     * @return the path of this resource
     */
    public String getResourcePath(){
        return getResourceFile().getPath();
    }
    
    /**
     * @return the name of this resource
     */
    public String getResourceName(){
        return getResourceFile().getName();
    }
    
    /**
     * @return this resource loaded as a buffered image
     */
    public BufferedImage loadAsBufferedImage(){
        return LoadUtils.loadBufferedImage(this);
    }
    
    /**
     * @return this resource loaded as a slickutil image
     */
    public Image loadAsImage(){
        return LoadUtils.loadImage(this);
    }
    
    /**
     * @return this resource loaded as a sound
     */
    public Sound loadAsSound(){
        return new Sound(LoadUtils.loadSound(this));
    }
    
    /**
     * @return this resource loaded as an ini configuration file
     */
    public Ini loadAsIni(){
        return LoadUtils.loadIni(this);
    }
    
    /**
     * @return this resource loaded as an xml document
     */
    public Document loadAsXml(){
        return LoadUtils.loadXml(this);
    }
    
    /**
     * @return this resource loaded as a string
     */
    public String loadAsText(){
        return LoadUtils.loadText(this);
    }
    
    /**
     * @return this resource loaded as a script file
     */
    public ScriptFile loadAsScriptFile(){
        return new ScriptFile(loadAsText());
    }

    /**
     * @return this resource loaded as an archive file
     */
    protected Resource loadAsArchiveFile(){
        return new ArchiveFile(this).getBaseResource();
    }
            
}
