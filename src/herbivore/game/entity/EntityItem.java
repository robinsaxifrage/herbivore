package herbivore.game.entity;
import herbivore.config.BuildInfo;
import herbivore.game.Operation;
import herbivore.geom.Area;
import herbivore.geom.Bounds;
import herbivore.geom.Location;
import herbivore.render.Renderer;
import herbivore.render.SpriteSheet;
import herbivore.res.Resource;
import herbivore.sound.Sound;
import org.newdawn.slick.Image;

/**
 * an item entity that can be picked up and
 * interacted with by actor entities
 * @author herbivore
 */
public abstract class EntityItem 
    extends Entity {

    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityItem(Resource baseResource){
        super(baseResource);
        getBehavior().addRenders(true).addUpdates(true).addCollides(true).addChecksCollisions(true).addCanFall(true);
        dropable = true;
    }
        
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
        super.init();
        dropSfx = getResourceOrFallback("drop.wav", Resource.getCustomizableResource("sound/itemDrop.wav")).loadAsSound();
        pickupSfx = getResourceOrFallback("pickup.wav", Resource.getCustomizableResource("sound/itemPickup.wav")).loadAsSound();
    }

    /**
     * called when this entity is picked up
     * @param ethnicity the ethnicity of the owner
     */
    public void pickup(String ethnicity){
        generateTextureAndBoundsEquipped(ethnicity);
        exicuteScript("pickup");
    }
    
    /**
     * called when this entity is dropped
     */
    public void drop(){
        exicuteScript("drop");
    }
        
    /**
     * called when this entity is equipped
     */
    public void equip(){
        exicuteScript("equip");
    }
    
    /**
     * called when this entity is dequipped
     */
    public void dequip(){
        exicuteScript("dequip");
    }
    
    /**
     * the update method for when this entity is equipped
     * @param delta 
     */
    protected void updateEquipped(int delta){
        if (dropable){
            final EntityItem item = this;
            final EntityActor myOwner = this.owner;
            myOwner.addOperation(new Operation("drop", "dropItem", this){
                @Override
                public void perform(){
                    myOwner.getInventory().drop(item);
                    dropSfx.play(myOwner);
                }
            });
        }
        equippedSpriteIndex = updateEquipped_calculateSpriteIndex();
    }
    
    /**
     * @return the appropriate sprite index
     */
    protected int updateEquipped_calculateSpriteIndex(){
        if (owner.getSpriteIndex() > 0){
            if (owner.isFlipped()){
                return 1;
            }
            else {
                return 0;
            }
        }
        else {
            return 2;
        }
    }
    
    /**
     * @see herbivore.game.entity.Entity#update(int)
     */
    @Override
    public void update(int delta){
        super.update(delta);
        if (rising){
            if (bob < 20f){
                bob += 0.009f*delta;
            }
            else {
                rising = false;
            }
        }
        else {
            if (bob > 0f){
                bob -= 0.009f*delta;
            }
            else {
                rising = true;
            }
        }
        
    }
    
    /**
     * @see herbivore.game.entity.Entity#collide(herbivore.game.entity.Entity)
     */
    @Override
    public void collide(Entity entity){
        if (entity instanceof EntityActor){
            final EntityActor actor = (EntityActor)entity;
            final EntityItem item = this;
            actor.addOperation(new Operation("take", "pickupItem", this){
                @Override
                public void perform(){
                    actor.getInventory().add(item);
                    pickupSfx.play(actor);
                }
                @Override
                public boolean enabled(){
                    return !actor.getInventory().full();
                }
            });
        }
    }
    
    /**
     * loads and then generates the equipped texture and bounds
     * with the specified ethnicity
     * @param ethnicity the ethnicity of the owner
     */
    private void generateTextureAndBoundsEquipped(String ethnicity){
        Resource spriteSheetResource = getBaseResource().getSubResource("equippedSprites(" + ethnicity + ").png");
        if (spriteSheetResource.exists()){
            SpriteSheet spriteSheet = new SpriteSheet(spriteSheetResource);
            int spriteCount = getData().get("sprites", "equippedSpriteCount", int.class);
            int spriteWidth = spriteSheet.getWidth()/spriteCount;
            int spriteHeight = spriteSheet.getHeight();
            spriteSheet.setDefaultSize(spriteWidth, spriteHeight);
            equippedSprites = new Image[spriteCount];
            for (int index = 0; index < spriteCount; index++){
                equippedSprites[index] = spriteSheet.chop(spriteWidth*index, 0);
            }
            equippedDrawArea = new Area(spriteWidth*BuildInfo.getTextureResizeRatio(), spriteHeight*BuildInfo.getTextureResizeRatio());
            equippedSpritesFlag = true;
        }
    }
    
    /**
     * renders this entity on the specified owner
     * @param renderer the renderer to use
     */
    protected void renderEquipped(Renderer renderer){
        Bounds renderBounds = new Bounds(equippedDrawArea.width, equippedDrawArea.height);
        renderBounds.setLocation(renderEquipped_getLocation());
        renderer.drawImage(getCurrentEquippedSprite(), renderBounds);
    }
    
    /**
     * calculates the location to render this entity at when it is
     * equipped on its owner
     * @return the location to render this to
     */
    private Location renderEquipped_getLocation(){
        Bounds ownerBounds = owner.getBounds();
        return owner.isStopped()? new Location(ownerBounds.x - ((equippedDrawArea.width - ownerBounds.width)/2), ownerBounds.y) : 
                (owner.isFlipped()? new Location(ownerBounds.x - (equippedDrawArea.width - ownerBounds.width), ownerBounds.y) : ownerBounds.getLocation());
    }
    
    /**
     * renders this entity at the specified location
     * @param renderer the renderer to use
     * @param location the location to render this entity at
     */
    public void renderFloating(Renderer renderer, Location location){
        Bounds drawBounds = new Bounds(getBounds());
        drawBounds.setLocation(location);
        renderer.drawImage(getCurrentSprite(), drawBounds);
    }    
        
    /**
     * @return this entity's bounds, with item bob factored in
     */
    @Override
    public Bounds getBounds(){
        Bounds withBob = new Bounds(super.getBounds());
        withBob.y -= bob;
        return withBob;
    }
    
    /**
     * @return the current equipped sprite
     */
    public Image getCurrentEquippedSprite(){
        return equippedSprites[equippedSpriteIndex];
    }
    
    public void setOwner(EntityActor owner){this.owner = owner;}
    public void setDropable(boolean dropable){this.dropable = dropable;}
    
    public EntityActor getOwner(){return owner;}
    public Area getEquippedDrawArea(){return equippedDrawArea;}
    
    protected boolean hasEquippedSprites(){return equippedSpritesFlag;}
    
    private EntityActor owner;
    private Image[] equippedSprites;
    private Sound dropSfx, pickupSfx;
    private Area equippedDrawArea; 
    private boolean rising, equippedSpritesFlag, dropable;
    private float bob;
    private int equippedSpriteIndex;
}
