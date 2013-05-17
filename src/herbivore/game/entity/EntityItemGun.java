package herbivore.game.entity;
import herbivore.config.BuildInfo;
import herbivore.game.Operation;
import herbivore.geom.Bounds;
import herbivore.geom.Location;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.sound.Sound;
import java.awt.Point;

/**
 * an item entity that can be used by actors to spawn bullets (no shit!)
 * @author herbivore
 */
public class EntityItemGun 
    extends EntityItem {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityItemGun(Resource baseResource){
        super(baseResource);
    }
    
    /**
     * @see herbivore.game.entity.Entity#update(int)
     */
    @Override
    protected void init(){
        super.init();
        reloadSpriteCount = getData().get("sprites", "reloadSpriteCount", int.class);
        blowbackPerShot = getData().get("gun", "blowbackPerShot", int.class);
        reloadTime = getData().get("gun", "reloadTime", int.class);
        fireLocations = new Point[]{
            new Point(getData().get("gun", "fireLoc1x", int.class), getData().get("gun", "fireLoc1y", int.class)),
            new Point(getData().get("gun", "fireLoc2x", int.class), getData().get("gun", "fireLoc2y", int.class))
        };
        fireSfx = getResourceOrFallback("fire.wav", Resource.getCustomizableResource("sound/gunFire.wav")).loadAsSound();
        reloadSfx = getResourceOrFallback("reload.wav", Resource.getCustomizableResource("sound/gunReload.wav")).loadAsSound();
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
        if (reloading && currentMagFeed != null){
            getOwner().getInventory().add(currentMagFeed);
            currentMagFeed = null;
            reloadCounter = 0;
            reloadSfx.stop();
        }
    }
    
    /**
     * @see herbivore.game.entity.EntityItem#updateEquipped(int)
     */
    @Override
    protected void updateEquipped(int delta){
        super.updateEquipped(delta);
        final EntityActor owner = getOwner();
        if (blowback > 0){
            blowback -= delta;
        }
        if (reloadCounter > 0){
            owner.setMoveLeft(false);
            owner.setMoveRight(false);
            owner.setStopped(true);
            owner.setSpriteIndex(0);
            reloadCounter -= delta;
        }
        else if (reloading){
            reloading = false;
            if (currentMagFeed != null){
                currentMag = currentMagFeed;
                getOwner().getInventory().remove(currentMag);
                owner.notifyFrom(currentMag.getRemaining() + " rounds loaded");
            }
        }
        if (currentMag == null || currentMag.getRemaining() == 0){
            owner.addOperation(new Operation("reload", "primaryAction", this){
                @Override
                public void perform(){
                    reloadCounter = reloadTime;
                    reloading = true;
                    currentMagFeed = getNextMag();
                    getOwner().getInventory().remove(currentMagFeed);
                    reloadSfx.play(owner);
                    exicuteScript("reload");
                }
                @Override
                public boolean enabled(){
                    return getNextMag() != null && reloadCounter <= 0;
                }
            });
        }
        else {
            owner.addOperation(new Operation("fire", "primaryAction", this){
                @Override
                public void perform(){
                    EntityBullet bullet = currentMag.getBullet();
                    bullet.setOwner(getOwner());
                    Location playerLoc = owner.getBounds().getLocation();
                    Location fireLoc = getFireLocation();
                    owner.getSpace().add(bullet, playerLoc.x + fireLoc.x, playerLoc.y + fireLoc.y);
                    blowback = blowbackPerShot;
                    fireSfx.play(owner);
                    exicuteScript("fire");
                }
                @Override
                public boolean enabled(){
                    return blowback <= 0;
                }
            });
        }
    }
    
    /**
     * @see herbivore.game.entity.EntityItem#updateEquipped_calculateSpriteIndex()
     */
    @Override
    protected int updateEquipped_calculateSpriteIndex(){
        if (reloading){
            return 3 + reloadSpriteCount - (reloadCounter/(reloadTime/reloadSpriteCount));
        }
        //what the fuck is this bullshit
        else if (blowback <= blowbackPerShot/2){
            if (getOwner().isFlipped()){
                return 1;
            }
            else {
                return 0;
            }
        }
        else {
            if (getOwner().isFlipped()){
                return 3;
            }
            else {
                return 2;
            }

        }
    }
    
    /**
     * @see herbivore.game.entity.EntityItem#renderEquipped(herbivore.render.Renderer)
     */
    @Override
    protected void renderEquipped(Renderer renderer){
        if (reloading){
            Entity owner = getOwner();
            Bounds renderBounds = new Bounds(getEquippedDrawArea().width, getEquippedDrawArea().height);
            renderBounds.setLocation(new Location(owner.getBounds().x - owner.getBounds().width/2, owner.getBounds().y));
            renderer.drawImage(getCurrentEquippedSprite(), renderBounds);
        }
        else {
            super.renderEquipped(renderer);
        }
    }
     
    /**
     * @return the location of the projectile to spawn
     */
    protected Location getFireLocation(){
        Point location = getOwner().isFlipped()? fireLocations[1] : fireLocations[0];
        return new Location(location.x * BuildInfo.getTextureResizeRatio(), location.y * BuildInfo.getTextureResizeRatio());
    }
    
     /**
     * @return the next appropriate magazine entity from this entity's owner's inventory
     */
    private EntityItemGunMag getNextMag(){
        for (EntityItem item:getOwner().getInventory()){
            if (item != null && item instanceof EntityItemGunMag && ((EntityItemGunMag)item).getTargetGun().equals(getName())){
                return (EntityItemGunMag)item;
            }
        }
        return null;
    }
    
    public EntityItemGunMag getCurrentMag(){return currentMag;}
   
    private EntityItemGunMag currentMag, currentMagFeed;
    private Point[] fireLocations;
    private Sound reloadSfx, fireSfx;
    private boolean reloading;
    private int blowbackPerShot, reloadTime, reloadCounter, blowback, reloadSpriteCount;
}
