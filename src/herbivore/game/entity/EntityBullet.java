package herbivore.game.entity;
import herbivore.game.EntityFactory;
import herbivore.geom.Bounds;
import herbivore.res.Resource;
import herbivore.sound.Sound;
import java.util.ArrayList;
import java.util.List;

/**
 * an entity representing the projectile fired from all gun items
 * @author herbivore
 */
public class EntityBullet 
    extends Entity {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityBullet(Resource baseResource){
        super(baseResource);
        getBehavior().addRenders(true).addUpdates(true).addCollides(true).addChecksCollisions(true);
        setSpeed(0.74f);
        collisions = new ArrayList();
    }
    
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
        super.init();
        damage = getData().get("bullet", "damage", int.class);
        lifespan = getData().get("bullet", "lifespan", int.class);
        penetrations = getData().get("bullet", "penetrations", int.class);
        hasLifespan = !(lifespan == -1);
        setMoveLeft(owner.isFlipped());
        setMoveRight(!owner.isFlipped());
        impactSfx = getResourceOrFallback("sound/impact.wav", Resource.getCustomizableResource("sound/fleshImpact.wav")).loadAsSound();
    }
    
    @Override
    public void update(int delta){
        if (hasLifespan && lifespan > 0){
            lifespan -= delta;
        }
        else if (hasLifespan) {
            doDestroy();
        }
        super.update(delta);
    }
    
    /**
     * @see herbivore.game.entity.Entity#collide(herbivore.game.entity.Entity)
     */
    @Override
    public void collide(Entity entity){
        if (entity instanceof EntityActor){
            EntityActor actor = (EntityActor)entity;
            if (!collisions.contains(actor)){
                actor.damage(damage, owner);
                Bounds myBounds = getBounds();
                getSpace().add(EntityFactory.loadEntity(actor.getBloodAnimation(), getSpace()), myBounds.x, myBounds.y);
                impactSfx.play(this);
                penetrations--;
                collisions.add(actor);
            }
            if (penetrations == 0){
                doDestroy();
            }
        }
    }
    
    protected void setOwner(EntityActor owner){this.owner = owner;}
    
    private List<EntityActor> collisions;
    private EntityActor owner;
    private Sound impactSfx;
    private boolean hasLifespan;
    private int damage, penetrations, lifespan;
}
