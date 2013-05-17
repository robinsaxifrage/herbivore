package herbivore.game.entity;
import herbivore.game.EntityBehavior;
import herbivore.game.EntityFactory;
import herbivore.geom.Bounds;
import herbivore.sound.Sound;
import java.util.ArrayList;
import java.util.List;

/**
 * an entity for inflicting damage on actors in a region
 * @author herbivore
 */
public class EntityHitbox 
    extends Entity {
    
    /**
     * creates a new hitbox with the specified dimensions and properties
     * @param width the width to use
     * @param height the height to use
     * @param damage the damage this hitbox will deal
     * @param life the lifespan of this hitbox, in milliseconds
     * @param owner the actor entity that owns this hitbox
     * @param impactSfx the sound effect to play upon impact
     */
    public EntityHitbox(float width, float height, int damage, int life, EntityActor owner, Sound impactSfx){
        this.owner = owner;
        this.damage = damage;
        this.life = life;
        this.impactSfx = impactSfx;
        getBehavior().addUpdates(true).addCollides(true).addChecksCollisions(true);
        editBounds(-1, -1, width, height);
        setName("hitbox");
        collisions = new ArrayList();
    }
    
    /**
     * @see herbivore.game.entity.Entity#update(int)
     */
    @Override
    public void update(int delta){
        if (life > 0){
            life -= delta;
        }
        else {
            doDestroy();
        }
    }

    /**
     * @see herbivore.game.entity.Entity#collide(herbivore.game.entity.Entity)
     */
    @Override
    public void collide(Entity entity){
        Bounds myBounds = getBounds();
        if (entity instanceof EntityActor){
            EntityActor actor = (EntityActor)entity;
            if (!collisions.contains(actor)){
                getSpace().add(EntityFactory.loadEntity((actor).getBloodAnimation(), getSpace()), myBounds.x, myBounds.y);
                actor.damage(damage, owner);
                collisions.add(actor);
                if (!played){
                    played = true;
                    impactSfx.play(entity);
                }
            }
        }
    }

    private List<EntityActor> collisions;
    private EntityActor owner;
    private Sound impactSfx;
    private boolean played;
    private int damage, life;
}
