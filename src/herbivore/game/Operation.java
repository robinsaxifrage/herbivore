package herbivore.game;
import herbivore.game.entity.Entity;

/**
 * a class representing an action an actor can take on objects in the world and its inventory
 * @see herbivore.game.entity.EntityActor#operations
 * @author herbivore
 */
public abstract class Operation {

    /**
     * creates a new operation with the specified description, control and owner
     * @param description the description of the operation
     * @param controlName the configuration files name for the desired control
     * @param owner the entity that this operation belongs to
     */
    public Operation(String description, String controlName, Entity owner){
        this.description = description;
        this.controlName = controlName;
        this.owner = owner;
    }
    
    /**
     * a function to perform the operation
     */
    public abstract void perform();
    
    /**
     * @return whether or not the operation is enabled
     */
    public boolean enabled(){
        return true;
    }
    
    /**
     * @param verbose whether or not a verbose name is requested
     * @return the description of this operation
     */
    public String getDescription(boolean verbose){
        return verbose? description + " " + owner.getName() : description;
    }
    
    public String getControlName(){return controlName;}
    
    private Entity owner;
    private String description, controlName;
}
