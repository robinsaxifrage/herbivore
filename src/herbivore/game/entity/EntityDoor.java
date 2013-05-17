package herbivore.game.entity;
import herbivore.game.Operation;
import herbivore.geom.Bounds;
import herbivore.res.Resource;
import herbivore.sound.Sound;

/**
 * an entity for connecting two separate spaces within a world
 * @author herbivore
 */
public class EntityDoor
    extends Entity {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityDoor(Resource baseResource){
        super(baseResource);
        getBehavior().addCollides(true).addRenders(true).addChecksCollisions(true);
    }
    
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
        super.init();
        openSfx = getResourceOrFallback("open.wav", Resource.getCustomizableResource("sound/doorOpen.wav")).loadAsSound();
        closeSfx = getResourceOrFallback("close.wav", Resource.getCustomizableResource("sound/doorClose.wav")).loadAsSound();
    }
    
    /**
     * @see herbivore.game.entity.Entity#collide(herbivore.game.entity.Entity)
     */
    @Override
    public void collide(Entity entity){
        if (entity instanceof EntityActor){
            final EntityActor actor = (EntityActor)entity;
            final Entity me = this;
            actor.addOperation(new Operation("enter", "doorUse", this){
                @Override
                public void perform(){
                    openSfx.play(me);
                    Bounds otherBounds = other.getBounds();
                    actor.moveToSpace(other.getSpace(), otherBounds.x + otherBounds.width/2 - actor.getBounds().width/2, otherBounds.y + otherBounds.height);
                    closeSfx.play(other);
                }
            });
        }
    }
    
    /**
     * links this door with another
     * @param other the door to link with
     */
    public void linkWith(EntityDoor other){
        this.other = other;
    }

    private Entity other;
    private Sound openSfx, closeSfx;
}
