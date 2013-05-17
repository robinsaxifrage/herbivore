package herbivore.game;
import herbivore.game.entity.Entity;

/**
 * an interface for implementation by classes that can collide with entities
 * @author herbivore
 */
public interface Collidable {

    /**
     * a function to perform logic after a collision has been found
     * @param entity the colliding entity
     */
    public void collide(Entity entity);
    
}
