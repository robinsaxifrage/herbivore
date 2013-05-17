package herbivore.game.entity;
import herbivore.res.Resource;

/**
 * an item entity to represent the magazine for a specific gun entity
 * @author herbivore
 */
public class EntityItemGunMag
    extends EntityItem {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityItemGunMag(Resource baseResource){
        super(baseResource);
    }
    
    /**
     * @see herbivore.game.entity.Entity#init() 
     */
    @Override
    protected void init(){
        super.init();
        targetGun = getData().get("magazine", "targetGun", String.class);
        capacity = getData().get("magazine", "capacity", int.class);
        bulletResource = getBaseResource().getSubResource("bullet.entity");
        remaining = capacity;
    }
    
    /**
     * removes a bullet from this entity and returns it
     * @return the bullet
     */
    protected EntityBullet getBullet(){
        remaining--;
        return new EntityBullet(bulletResource);
    }
    
    protected String getTargetGun(){return targetGun;}
    protected int getRemaining(){return remaining;}
    
    private Resource bulletResource;
    private String targetGun;
    private int capacity, remaining;
}
