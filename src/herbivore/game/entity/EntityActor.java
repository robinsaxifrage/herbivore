package herbivore.game.entity;
import herbivore.config.BuildInfo;
import herbivore.game.ActorStats;
import herbivore.game.EntityFactory;
import herbivore.game.IngameUIConversation;
import herbivore.game.Inventory;
import herbivore.game.Operation;
import herbivore.geom.Area;
import herbivore.geom.Bounds;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.sound.Sound;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.newdawn.slick.Image;

/**
 * an actor entity without type specific implementations.
 * this class is technically ready to be added to a space
 * without further implementation
 * @author herbivore
 */
public abstract class EntityActor
    extends Entity {
        
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityActor(Resource baseResource){
        super(baseResource);
        getBehavior().addUpdates(true).addRenders(true).addCollides(true).addCanFall(true);
        operations = new ArrayList();
        hostilities = new ArrayList();
        hostileToMe = new ArrayList();
        canFaceFront = true;
        killable = true;
        random = new Random();
    }
    
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
        super.init();
        ethnicity = getData().get("actor", "ethnicity", String.class);
        deathSfx = getResourceOrFallback("death.wav", Resource.getCustomizableResource("sound/actorDeath.wav")).loadAsSound();
        deathAnimation = getBaseResource().getSubResource("deathAnimation.entity");
        bloodAnimation = getResourceOrFallback("bloodSplat.entity", Resource.getCustomizableResource("prefab/animation/bloodSplat.entity"));
        blinkImage = getBaseResource().getSubResource("blink.png").loadAsImage();
        blinkArea = new Area(blinkImage.getWidth()*BuildInfo.getTextureResizeRatio(), blinkImage.getHeight()*BuildInfo.getTextureResizeRatio());
        arms = new EntityItemActorArms(getBaseResource().getSubResource("arms.entity"), this);
        arms.doInit();
        stats = new ActorStats(getData());
        stats.setOn(this);
        stamina = staminaMax;
        inventory = new Inventory(this, inventorySize);
    }
    
    /**
     * @see herbivore.game.entity.Entity#update(int)
     */
    @Override
    public void update(int delta){
        super.update(delta);
        float newX = -1;
        if (getBounds().x < 0){
            newX = 0;
        }
        if (getBounds().x + getBounds().width > getSpace().getWidth()){
            newX = getSpace().getWidth() - getBounds().width;
        }
        editBounds(newX, -1, -1, -1);
        if (health < 0){
            doDestroy();
        }
        if (currentConversation != null){
            EntityActor other = currentConversation.getOther(this);
            float distance = getBounds().x - other.getBounds().x;
            if (distance > 400 || distance < -400){
                currentConversation.end();
            }
            if (currentConversation.isEnded()){
                currentConversation = null;
            }
        }
        update_updateSpriteIndex(delta);
        update_updateJump(delta);
        update_updateSprint(delta);
        operations.clear();
        if (inventory.current() != null){
            inventory.current().updateEquipped(delta);
        }
        arms.updateEquipped(delta);
        if (blinkCounter > -500){
            blinkCounter -= delta;
        }
        else {
            blinkCounter = random.nextInt(3000);
        }
    }
    
    /**
     * updates this entity's sprite index
     * @param delta the delta time, in milliseconds. used for time normalization
     */
    private void update_updateSpriteIndex(int delta){
        int frameDelta = delta;
        if (sprinting){
            frameDelta *= sprintSpeedMod;
        }
        frameUpdateCounter += frameDelta;
        if (frameUpdateCounter > 120){
            frameUpdateCounter = 0;
            if (getMoveLeft() || getMoveRight() && !(getMoveLeft() && getMoveRight())){
                if (stepPlayCounter > 0){
                    stepPlayCounter --;
                }
                else {
                    if (getGround() != null){
                        getGround().getStepSound().play(this);
                    }
                    stepPlayCounter = 2;
                }
                if (getSpriteIndex() < getSprites().length - 1){
                    setSpriteIndex(getSpriteIndex() + 1);
                }
                else {
                    setSpriteIndex(1);
                }
            }
            stopped = getMoveLeft() == getMoveRight();
            if (canFaceFront){
                if (stopped){
                    setSpriteIndex(0);
                }
            }
            else {
                if (stopped){
                    setSpriteIndex(1);
                }
                stopped = false;
            }
        }
    }
    
    /**
     * updates this entity's jump arc
     * @param delta the delta time, in milliseconds. used for time normalization
     */
    private void update_updateJump(int delta){
        if (jumpAscending){
            if (jump < 40f){
                jump += 0.6f*delta;
            }
            else {
                jumpAscending = false;
            }
        }
        else if (jump > 0){
            jump -= 0.4f*delta;
        }
    }
    
    /**
     * updates this entity's sprint state and stamina
     * @param delta the delta time, in milliseconds. used for time normalization
     */
    private void update_updateSprint(int delta){
        if (sprinting){
            if (stamina > 0 && (getMoveLeft() || getMoveRight())){
                stamina -= delta/10;
            }
            else {
                sprinting = false;
            }
        }
        else {
            if (stamina < staminaMax){
                int add = delta/20;
                if (add == 0){
                    add = 1;
                }
                stamina += add;
            }
        }
    }    
    
    /**
     * @see herbivore.arch.Renderable#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        super.render(renderer);
        if (blinkCounter < 0 && getSpriteIndex() == 0){
            Bounds blinkBounds = new Bounds(blinkArea.width, blinkArea.height);
            blinkBounds.setLocation(getBounds().getLocation());
            renderer.drawImage(blinkImage, blinkBounds);
        }
        if (inventory.current() != null && inventory.current().hasEquippedSprites()){
            inventory.current().renderEquipped(renderer);
        }
        else {
            arms.renderEquipped(renderer);
        }
    }
    
    /**
     * initiates this entity's jump, unless it is already in progress
     */
    public void jump(){
        if (jump <= 0f){
            jumpAscending = true;
        }
    }
    
    /**
     * kills this entity
     */
    public void kill(){
        health = 0 - 1;
    }
        
    /**
     * adds an operation to this entity
     * @see herbivore.game.Operation
     * @param operation the operation to add
     */
    public void addOperation(Operation operation){
        operations.add(operation);
    }
    
    /**
     * adds a hostility to this entity
     * @param actor the new hostility
     */
    public void addHostility(EntityActor actor){
        hostilities.add(actor);
        actor.hostileToMe.add(this);
    }
    
    /**
     * removes a hostility from this entity
     * @param actor the hostility to remove
     */
    public void removeHostility(EntityActor actor){
        hostilities.remove(actor);
        actor.hostileToMe.remove(this);
    }
    
    /**
     * determines whether or not this entity is hostile
     * to the specified other
     * @param actor the entity to check for hostility
     * @return whether or not the hostility exists
     */
    public boolean isHostileTo(EntityActor actor){
        return hostilities.contains(actor);
    }
    
    /**
     * damages this entity the specified amount, from the specified entity
     * @param damage the amount of damage
     * @param perpetrator the perpetrator of the damage
     */
    protected void damage(int damage, EntityActor perpetrator){
        if (damage > 0 && perpetrator != this){
            damage += ((int)(Math.random()*2));
            addHostility(perpetrator);
        }
        if (killable || damage < 0){
            health -= damage;
        }
    }
    
    /**
     * @see herbivore.game.entity.Entity#destroy()
     */
    @Override
    protected void destroy(){
        super.destroy();
        for (EntityItem item : inventory){
            if (item != null){
                inventory.drop(item);
            }
        }
        for (int index = 0; index < hostileToMe.size(); index++){
            hostileToMe.get(index).removeHostility(this);
        }
        getSpace().add(EntityFactory.loadEntity(deathAnimation, getSpace(), this), 0, 0);
        deathSfx.play(this);
        //credit perp or dont
        super.destroy();
    }

    /**
     * @return this entity's bounds, with jump height factored in
     */
    @Override
    public Bounds getBounds(){
        Bounds withJump = new Bounds(super.getBounds());
        if (jump > 0f){
            withJump.y -= jump;
        }
        return withJump;
    }
    
    /**
     * @return this entity's current speed
     */
    @Override
    public float getSpeed(){
        if (sprinting){
            return super.getSpeed()*sprintSpeedMod;
        }
        return super.getSpeed();
    }

    protected void setCurrentConversation(IngameUIConversation currentConversation){this.currentConversation = currentConversation;}
    protected void setCanFaceFront(boolean canFaceFront){this.canFaceFront = canFaceFront;}
    protected void setStopped(boolean stopped){this.stopped = stopped;}
    protected void setStamina(int stamina){this.stamina = stamina;}
    public void setSprinting(boolean sprinting){this.sprinting = sprinting;}
    public void setKillable(boolean killable){this.killable = killable;}
    public void setSprintSpeedMod(float sprintSpeedMod){this.sprintSpeedMod = sprintSpeedMod;}
    public void setInventorySize(int inventorySize){this.inventorySize = inventorySize;}
    public void setPerception(int perception){this.perception = perception;}
    public void setStaminaMax(int staminaMax){this.staminaMax = staminaMax;}
    public void setHealth(int health){this.health = health;}
    
    protected ActorStats getStats(){return stats;}
    protected Resource getBloodAnimation(){return bloodAnimation;}
    protected boolean isStopped(){return stopped;}
    
    public IngameUIConversation getCurrentConversation(){return currentConversation;}
    public List<EntityActor> getHostilities(){return hostilities;}
    public List<Operation> getOperations(){return operations;}
    public Inventory getInventory(){return inventory;}
    public String getEthnicity(){return ethnicity;}
    public int getPerception(){return perception;}
    public int getStaminaMax(){return staminaMax;}
    public int getStamina(){return stamina;}    
    public int getHealth(){return health;}
    
    private IngameUIConversation currentConversation;
    private List<EntityActor> hostilities, hostileToMe;
    private EntityItemActorArms arms;
    private List<Operation> operations;
    private ActorStats stats;
    private Inventory inventory;
    private Resource deathAnimation, bloodAnimation;
    private String ethnicity;
    private Sound deathSfx;
    private Random random;
    private Image blinkImage;
    private Area blinkArea;
    private boolean stopped, canFaceFront, killable;
    private boolean jumpAscending;
    private float sprintSpeedMod;
    private boolean sprinting;
    private float jump;
    private int frameUpdateCounter, perception, stepPlayCounter, blinkCounter, staminaMax, stamina, health, inventorySize;
}