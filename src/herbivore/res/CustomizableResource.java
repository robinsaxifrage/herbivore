package herbivore.res;
import java.io.File;

/**
 * a class representing a resource that can exist in
 * two different location. these locations are the current
 * levels base folder and the <code>res/</code> folder
 * @author herbivore
 */
public class CustomizableResource
    extends Resource {
    
    /**
     * creates a new customizable resource
     * @param location the location of this resource
     */
    protected CustomizableResource(String location){
        this.location = location;
        stockResource = LoadUtils.getFile("res/" + location);
        customResource = LoadUtils.getFile(LoadUtils.getCustomSearchDirectory() + location);
    }
    
    /**
     * @see herbivore.res.Resource#getSubResourceForLoad(java.lang.String)
     */
    @Override
    public Resource getSubResourceForLoad(String location){
        return Resource.getCustomizableResource(this.location + "/" + location);
    }
    
    /**
     * @see herbivore.res.Resource#getResourceFile() 
     */
    @Override
    public File getResourceFile(){
        if (customResource.exists()){
            return customResource;
        }
        else if (stockResource.exists()){
            return stockResource;
        }
        return null;
    }
    
    private String location;
    private File stockResource, customResource;
}
