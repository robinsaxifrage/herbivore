package herbivore.game;
import herbivore.arch.Updatable;
import herbivore.game.entity.Entity;
import herbivore.game.entity.EntityActor;
import herbivore.game.entity.EntityNPC;
import herbivore.geom.Bounds;
import herbivore.geom.Location;
import herbivore.res.Resource;
import herbivore.script.ScriptFile;
import org.ini4j.Ini;

/**
 * the artificial intelligence class that is wrapped around a .ai resource and subsequently a javascript script.
 * this class also contains a few helper methods for the scripts to use
 * @author herbivore
 */
public class AI
    implements Updatable {

    /**
     * creates a new AI wrapper
     * @param owner the non player character that this ai is controlling
     * @param resource the .ai archive to use
     */
    public AI(EntityNPC owner, Resource resource){
        this.owner = owner;
        data = resource.getSubResource("data.ini").loadAsIni();
        nullAi = data.get("data", "nullAi", boolean.class);
        if (!nullAi){
            updateInterval = data.get("data", "updateInterval", int.class);
            scriptFile = resource.getSubResource("script.js").loadAsScriptFile();
        }
    }
    
    /**
     * updates the countdown timer and maybe the ai, unless it was specified as a null ai in its configuration file
     * @param delta the delta in milliseconds
     */
    @Override
    public void update(int delta){
        if (nullAi){
            return;
        }
        if (updateTimer > updateInterval){
            scriptFile.invoke("update", this, owner, delta);
            updateTimer = 0;
        }
        updateTimer += delta;
    }
    
    /**
     * retrieves the nearest entity hostile to the owner inside the distance
     * @param includeDistance the inclusion distance of the search
     * @return the nearest entity, or null if none were found
     */
    public Entity getNearestHostility(float includeDistance){
        return getNearest(new EntityFinder(){
            @Override
            public boolean isValid(Entity entity){
                return entity instanceof EntityActor && owner.isHostileTo((EntityActor)entity);
            }
        }, includeDistance);
    }
    
    /**
     * retrieves the nearest entity of the specified class inside the distance
     * @param className the simple name of the class to locate
     * @param includeDistance the inclusion distance of the search
     * @return the nearest entity, or null if none were found
     */
    public Entity getNearestByClass(final String className, float includeDistance){
        return getNearest(new EntityFinder(){
            @Override
            public boolean isValid(Entity entity){
                return entity.getClass().getSimpleName().equals(className);
            }
        }, includeDistance);
    }
    
    /**
     * retrieves the nearest entity with the specified name inside the distance
     * @param name the name of the entity to locate
     * @param includeDistancethe inclusion distance of the search
     * @return the nearest entity, or null if none were found
     */
    public Entity getNearestByName(final String name, float includeDistance){
        return getNearest(new EntityFinder(){
            @Override
            public boolean isValid(Entity entity){
                return entity.getName().equals(name);
            }
        }, includeDistance);
    }
    
    /**
     * iterates through the entities in the owner's space and finds the nearest one
     * that the finder determines as valid
     * @param finder the entity finder to use
     * @param includeDistance inclusion distance of the search
     * @return the nearest entity, or null if none were found
     */
    private Entity getNearest(EntityFinder finder, float includeDistance){
        Entity find = null;
        Bounds ownerBounds = owner.getBounds();
        for (Entity check : owner.getSpace().getEntities()){
            if (finder.isValid(check)){
                float dist = check.getBounds().x - ownerBounds.x;
                if (dist > includeDistance && dist < -includeDistance){
                    includeDistance = dist;
                    find = check;
                }
            }
        }
        return find; 
    }
    
    /**
     * stops the owner, then moves it relative to a location
     * @param location the location to move relative to
     * @param at the flag of whether the owner is moving towards or away from the point
     * @param rush whether the owner should sprint or not
     */
    public void moveRelativeTo(Location location, boolean at, boolean rush){
        owner.setSprinting(rush);
        owner.setMoveLeft(false);
        owner.setMoveRight(false);
        float distVec = owner.getBounds().x - location.x;
        if (distVec > 25f || distVec < -25f){
            if (distVec > 0 && at){
                owner.setMoveLeft(true);
            }
            else {
                owner.setMoveRight(true);
            }
        }
    }
    
    /**
     * performs an operation from the owner, if the owner currently has it
     * @see herbivore.game.Operation
     * @param name the name of the operation to perform
     * @return whether or not the operation was performed
     */
    public boolean performOperation(String name){
        for (Operation op:owner.getOperations()){
            if (op.getDescription(false).equals(name) && op.enabled()){
                op.perform();
                return true;
            }
        }
        return false;
    }
    
    private ScriptFile scriptFile;
    private EntityNPC owner;
    private Ini data;
    private boolean nullAi;
    private int updateInterval, updateTimer;
    
    /**
     * an interface use in sorting a list of entities based on criteria
     */
    private interface EntityFinder {
        
        /**
         * a function to determine whether or not an entity meets
         * the criteria
         * @param entity the entity to check
         * @return whether or not the entity meets the criteria
         */
        public boolean isValid(Entity entity);
        
    }
}
