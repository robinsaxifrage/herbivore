package herbivore.game;

/**
 * a class containing the basic physical and logical 
 * properties of an entities type
 * @author herbivore
 */
public class EntityBehavior {

    /**
     * adds a rendering flag to this behavior
     * @see herbivore.game.entity.Entity#render(herbivore.render.Renderer)
     * @param renders the rendering flag
     * @return this entity behavior
     */
    public EntityBehavior addRenders(boolean renders){
        this.renders = renders;
        return this;
    }
    
    /**
     * adds a logic update flag to this behavior
     * @see herbivore.game.entity.Entity#update(int)
     * @param updates the logic update flag
     * @return this entity behavior
     */
    public EntityBehavior addUpdates(boolean updates){
        this.updates = updates;
        return this;
    }
    
    /**
     * adds a collision flag to this behavior
     * @see herbivore.game.entity.Entity#collide(herbivore.game.entity.Entity)
     * @param collides the collision flag
     * @return this entity behavior
     */
    public EntityBehavior addCollides(boolean collides){
        this.collides = collides;
        return this;
    }
    
    /**
     * adds a collision checking to this behavior
     * @see herbivore.game.entity.Entity#checkCollisions(java.util.List, int)
     * @param checksCollisions the collision checking flag
     * @return this entity behavior
     */
    public EntityBehavior addChecksCollisions(boolean checksCollisions){
        this.checksCollisions = checksCollisions;
        return this;
    }
    
    /**
     * adds a falling flag to this behavior
     * @param canFall the falling flag
     * @return this entity behavior;
     */
    public EntityBehavior addCanFall(boolean canFall){
        this.canFall = canFall;
        return this;
    }
    
    public boolean getRenders(){return renders;}
    public boolean getUpdates(){return updates;}
    public boolean getCollides(){return collides;}
    public boolean getChecksCollisions(){return checksCollisions;}
    public boolean getCanFall(){return canFall;}
    
    private boolean renders, updates, collides, checksCollisions, canFall;
}
