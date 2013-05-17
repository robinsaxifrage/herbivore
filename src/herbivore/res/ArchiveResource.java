package herbivore.res;
import herbivore.misc.Logger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;

/**
 * a class representing a resource which is inside an archive
 * @author herbivore
 */
public class ArchiveResource
    extends Resource {

    /**
     * creates a new archive resource
     * @param archive the parent archive
     * @param entry the entry in the archive that represents this resource
     * @param exists whether or not this resource exists
     */
    protected ArchiveResource(ArchiveFile archive, JarEntry entry, boolean exists){
        this.archive = archive;
        this.entry = entry;
        this.exists = exists;
    }
    
    /**
     * @see herbivore.res.Resource#getSubResourceForLoad(java.lang.String)
     */
    @Override
    protected Resource getSubResourceForLoad(String location){
        if (exists()){
            return null;
        }
        JarEntry newEntry = archive.getEntry(location);
        return Resource.checkForArchive(new ArchiveResource(archive, newEntry, newEntry != null), location);
    }
    
    /**
     * @see herbivore.res.Resource#exists()
     */
    @Override
    public boolean exists(){
        return exists;
    }
    
    /**
     * @see herbivore.res.Resource#getResourcePath()
     */
    @Override
    public String getResourcePath(){
        return exists? archive.getPath() + "->" + entry.getName() : archive.getPath() + " base resource";
    }
    
    /**
     * @see herbivore.res.Resource#getResourceFile()
     */
    @Override
    protected File getResourceFile(){
        return null;
    }
    
    /**
     * @see herbivore.res.Resource#getResourceInputStream()
     */
    @Override
    protected InputStream getResourceInputStream(){
        try {
            return archive.getArchive().getInputStream(entry);
        }
        catch (IOException exception){
            Logger.error(exception, "failed to load archive entry " + entry.getName());
        }
        return null;
    }
    
    /**
     * @see herbivore.res.Resource#loadAsArchiveFile()
     */
    @Override
    public Resource loadAsArchiveFile(){
        return exists? archive.extractArchiveToTemp(entry) : this;
    }
  
    private ArchiveFile archive;
    private JarEntry entry;
    private boolean exists;
}
