package herbivore.game.entity;
import herbivore.res.Resource;

/**
 * an entity representing the most basic possible
 * implementation of an entity. used to add static
 * textures to the world
 * @author herbivore
 */
public class EntityTexture 
    extends Entity {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityTexture(Resource baseResource){
        super(baseResource);
        getBehavior().addRenders(true);
    }
    
}
