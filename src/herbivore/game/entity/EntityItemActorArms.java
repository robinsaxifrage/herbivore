package herbivore.game.entity;
import herbivore.res.Resource;

/**
 * an ease of use class that contains the default item, arms
 * @author herbivore
 */
public class EntityItemActorArms 
    extends EntityItem {
    
    /**
     * creates a new arms item 
    * @param baseResource the .entity archive
    * @param owner the owner of this entity
     */
    public EntityItemActorArms(Resource baseResource, EntityActor owner){
        super(baseResource);
        setDropable(false);
        setOwner(owner);
    }
    
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
        super.init();
        pickup(getOwner().getEthnicity());
    }
    
}
