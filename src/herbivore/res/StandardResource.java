package herbivore.res;
import java.io.File;

/**
 * a class representing a basic resource
 * @author herbivore
 */
public class StandardResource
    extends Resource {

    /**
     * creates a new resource
     * @param location the location of the resource, relative to the games base directory
     */
    protected StandardResource(String location){
        this.location = location;
        file = LoadUtils.getFile(location);
    }
    
    /**
     * @see herbivore.res.Resource#getSubResourceForLoad(java.lang.String)
     */
    @Override
    protected Resource getSubResourceForLoad(String location){
        return Resource.getResource(this.location + "/" + location);
    }
    
    /**
     * @see herbivore.res.Resource#getResourceFile()
     */
    @Override
    public File getResourceFile(){
        if (file.exists()){
            return file;
        }
        return null;
    }
        
    private String location;
    private File file;
}
