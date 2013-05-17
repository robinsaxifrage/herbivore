package herbivore.game;
import herbivore.config.BuildInfo;
import herbivore.game.entity.EntityActor;
import org.ini4j.Ini;

/**
 * a class to calculate the statistics of an actor
 * @author herbivore
 */
public class ActorStats {
    
    /**
     * creates a new statistics class with values loaded from the specified configuration file
     * @param file the configuration file to use
     */
    public ActorStats(Ini file){
        this(file.get("stats", "strength", int.class), file.get("stats", "agility", int.class), file.get("stats", "intelligence", int.class));
    }
    
    /**
     * creates a new statistics class with specified values
     * @param strength the strength of the actor
     * @param agility the agility of the actor
     * @param intelligence the intelligence
     */
    public ActorStats(int strength, int agility, int intelligence){
        this.strength = strength;
        this.agility = agility;
        this.intelligence = intelligence;
        this.speed = (0.02f + (agility * 0.001f)) * BuildInfo.getTextureResizeRatio();
        this.sprintSpeedMod = 1f + (agility * 0.4f);
        this.inventorySize = 5 + strength;
        this.health = 25 + (5 * strength);
        this.stamina = 50 + (25 * strength);
    }
    
    /**
     * applies the calculated values to a target
     * @param target the actor to set the values on
     */
    public void setOn(EntityActor target){
        target.setSpeed(speed);
        target.setSprintSpeedMod(sprintSpeedMod);
        target.setInventorySize(inventorySize);
        target.setHealth(health);
        target.setStaminaMax(stamina);
        target.setPerception(intelligence);
    }
    
    //provided so scripts can check them for adequacy
    public int getIntelligence(){return intelligence;}
    public int getStrength(){return strength;}
    public int getAgility(){return agility;}
    
    private float speed, sprintSpeedMod;
    protected int health, stamina, inventorySize, strength, agility, intelligence;
}
