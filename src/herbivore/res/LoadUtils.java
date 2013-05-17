package herbivore.res;
import herbivore.misc.Logger;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.ini4j.Ini;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.WaveData;

/**
 * a static class to help with the loading of resources as
 * different types of media
 * @author herbivore
 */
public class LoadUtils {
    
    /**
     * initializes the loaded object storage and xml builder
     */
    private static void init(){
        if (initialized){
            return;
        }
        storage = new HashMap();
        xmlBuilder = new Builder();
        initialized = true;
    }
    
    /**
     * asserts the current custom search directory used to look for
     * customizable resources
     * @param directory the directory to use
     */
    public static void assertCustomSearchLocation(String directory){
        customSearchDirectory = directory + "/";
        Logger.info("current custom resource directory is now: " + customSearchDirectory);
    }
    
    /**
     * loads a .png resource as a slickutil image
     * @param resource the resource to load
     * @return the loaded image
     */
    protected static Image loadImage(final Resource resource){
        return grabOrLoad(resource, "image", Image.class, new LoadCallback(){
            @Override
            public Object load(InputStream resourceStream) throws SlickException {
                return new Image(resourceStream, resource.getResourcePath(), false);
            }
        });
    }
    
    /**
     * loads a .png resource as a buffered image
     * @param resource the resource to load
     * @return the loaded image
     */
    protected static BufferedImage loadBufferedImage(Resource resource){
        return grabOrLoad(resource, "buffered image", BufferedImage.class, new LoadCallback(){
            @Override
            public Object load(InputStream resourceStream) throws IOException {
                return ImageIO.read(resourceStream);
            }
        });
    }
    
    /**
     * loads a .wav resource as a sound
     * @param resource the resource to load
     * @return the loaded sounds buffer reference
     */
    protected static int loadSound(final Resource resource){
        return grabOrLoad(resource, "sound", Integer.class, new LoadCallback(){
            @Override
            public Object load(InputStream resourceStream) throws IOException {
                WaveData wavData = WaveData.create(new BufferedInputStream(resourceStream));
                IntBuffer buffer = BufferUtils.createIntBuffer(1);
                AL10.alGenBuffers(buffer);
                int bufferId = buffer.get(0);
                AL10.alBufferData(bufferId, wavData.format, wavData.data, wavData.samplerate);
                wavData.dispose();
                return new Integer(bufferId);
            }
        });
    }
    
    /**
     * loads a .* resource as text
     * @param resource the resource to load
     * @return the loaded files text
     */
    protected static String loadText(Resource resource){
        return grabOrLoad(resource, "text", String.class, new LoadCallback(){
            @Override
            public Object load(InputStream resourceStream) throws IOException {
                byte[] dataBuffer = new byte[resourceStream.available()];
                resourceStream.read(dataBuffer);
                return new String(dataBuffer);
            }
        });
    }
    
    /**
     * loads a resource as a configuration file
     * @param resource the resource to load
     * @return the loaded configuration file
     */
    protected static Ini loadIni(Resource resource){
        return grabOrLoad(resource, "ini", Ini.class, new LoadCallback(){
            @Override
            public Object load(InputStream resourceStream) throws IOException {
                return new Ini(resourceStream);
            }
        });
    }
    
    /**
     * loads a resource as an xml document
     * @param resource the resource to load
     * @return the loaded document
     */
    protected static Document loadXml(Resource resource){
        return grabOrLoad(resource, "xml", Document.class, new LoadCallback(){
            @Override
            public Object load(InputStream resourceStream) throws ParsingException, IOException {
                return xmlBuilder.build(resourceStream);
            }
        });
    }
    
    /**
     * checks the object store for a resource, and if it does not exist, loads
     * it with the specified load callback
     * @param resource the resource to load
     * @param loadType the name of the type of resource being loaded
     * @param clazz the class type to be returned
     * @param callback the callback to load the file
     * @return the loaded object
     */
    private static <K extends Object> K grabOrLoad(Resource resource, String loadType, Class<K> clazz, LoadCallback callback){
        init();
        String resourceName = resource.getResourcePath();
        String resourceId = loadType + "!" + resourceName;
        Object object = storage.get(resourceId);
        if (object == null){
            try (InputStream resourceStream = resource.getResourceInputStream()) {
                object = callback.load(resourceStream);
            }
            catch (Exception exception){
                Logger.error(exception, "failed to load " + resourceName + " as " + loadType);
            }
            storage.put(resourceId, object);
        }
        return (K)object;
    }
    
    /**
     * returns a file with the appropriate system directory separators
     * @param location the location of the file, with "/" representing directories
     * @return 
     */
    public static File getFile(String location){
        init();
        return new File("..\\", location.replace("/", "\\"));
    }
    
    protected static String getCustomSearchDirectory(){return customSearchDirectory;}
   
    private static Map<String, Object> storage;
    private static Builder xmlBuilder;
    private static String customSearchDirectory;
    private static boolean initialized;
    
    private static interface LoadCallback {
        public Object load(InputStream resourceStream) throws Exception;
    }
}
