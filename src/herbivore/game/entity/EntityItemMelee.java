package herbivore.game.entity;
import herbivore.config.BuildInfo;
import herbivore.game.Operation;
import herbivore.geom.Bounds;
import herbivore.res.Resource;
import herbivore.sound.Sound;
import java.awt.Dimension;
import java.awt.Point;

/**
 * an item entity used by actors for melee combat. it
 * spawns hitboxs on use
 * @author herbivore
 */
public class EntityItemMelee
    extends EntityItem {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityItemMelee(Resource baseResource){
        super(baseResource);
    }
    
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
        super.init();
        actionDescription = getData().get("melee", "actionDescription", String.class);
        damage = getData().get("melee", "damage", int.class);
        swingTime = getData().get("melee", "swingTime", int.class);
        recoverTime = getData().get("melee", "recoverTime", int.class);
        staminaCost = getData().get("melee", "staminaCost", int.class);
        hitboxSize = new Dimension(getData().get("melee", "hitboxWidth", int.class), getData().get("melee", "hitboxHeight", int.class));
        hitboxLocations = new Point[]{
            new Point(getData().get("melee", "hitbox1x", int.class), getData().get("melee", "hitbox1y", int.class)),
            new Point(getData().get("melee", "hitbox2x", int.class), getData().get("melee", "hitbox2y", int.class))
        };
        Resource swingSfxResource = getResourceOrFallback("swing.wav", null);
        if (swingSfxResource != null){
            swingSfx = swingSfxResource.loadAsSound();
        }
        impactSfx = getResourceOrFallback("impact.wav", Resource.getCustomizableResource("sound/fleshImpact.wav")).loadAsSound();
    }
    
    /**
     * @see herbivore.game.entity.EntityItem#equip() 
     */
    @Override
    public void equip(){
        super.equip();
        getOwner().setCanFaceFront(false);
    }
    
    /**
     * @see herbivore.game.entity.EntityItem#dequip()
     */
    @Override
    public void dequip(){
        super.dequip();
        getOwner().setCanFaceFront(true);
    }
    
    /**
     * @see herbivore.game.entity.EntityItem#updateEquipped(int) 
     */
    @Override
    protected void updateEquipped(int delta){
        super.updateEquipped(delta);
        if (swinging){
            if (swingCounter > 0){
                swingCounter -= delta;
            }
            else {
                swinging = false;
                recovering = true;
            }
        }
        else if (recovering){
            if (swingCounter > -recoverTime){
                swingCounter -= delta;
            }
            else {
                recovering = false;
            }
        }
        final EntityActor owner = getOwner();
        owner.addOperation(new Operation(actionDescription, "primaryAction", this){
            @Override
            public void perform(){
                Bounds playerBounds = owner.getBounds();
                Point hitLocPx = owner.isFlipped()? hitboxLocations[1] : hitboxLocations[0];
                EntityHitbox hitbox = new EntityHitbox(hitboxSize.width * BuildInfo.getTextureResizeRatio(), hitboxSize.height * BuildInfo.getTextureResizeRatio(), damage, 500, owner, impactSfx);
                owner.getSpace().add(hitbox, playerBounds.x + (hitLocPx.x * BuildInfo.getTextureResizeRatio()), playerBounds.y + (hitLocPx.y * BuildInfo.getTextureResizeRatio()));
                owner.setStamina(owner.getStamina() - staminaCost);
                swingCounter = swingTime;
                swinging = true;
                if (swingSfx != null){
                    swingSfx.play(owner);
                }
                exicuteScript("swing");
            }
            @Override
            public boolean enabled(){
                return !swinging && !recovering && owner.getStamina() > staminaCost;
            }
        });
    }
    
    /**
     * @see herbivore.game.entity.EntityItem#updateEquipped_calculateSpriteIndex()
     */
    @Override
    protected int updateEquipped_calculateSpriteIndex(){
        if (swinging){
            if (!getOwner().isFlipped()){
                return 4;
            }
            return 5;
        }
        if (recovering){
            if (!getOwner().isFlipped()){
                return 2;
            }
            return 3;
        }
        if (!getOwner().isFlipped()){
            return 0;
        }
        return 1;
    }
    
    private Dimension hitboxSize;
    private Point[] hitboxLocations;
    private String actionDescription;
    private Sound swingSfx, impactSfx;
    private boolean swinging, recovering;
    private int swingTime, recoverTime, staminaCost, damage, swingCounter;
}
