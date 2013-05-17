package herbivore.game.entity;
import herbivore.render.Java2DUtils;
import herbivore.res.Resource;
import herbivore.sound.Sound;
import java.awt.image.BufferedImage;
import java.util.Random;
import org.newdawn.slick.Image;

/**
 * an entity representing the physical ground. 
 * also loads the appropriate footstep sound effects
 * @author herbivore
 */
public class EntityGround
    extends Entity {

    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityGround(Resource baseResource){
        super(baseResource);
        getBehavior().addCanFall(false).addCollides(true).addRenders(true);
        setSuppressInitScript(true);
    }
    
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
        if (!subpiece){
            float width = getBounds().width;
            adjustTextureToBounds(width);
            super.init();
            editBounds(-1, -1, width, -1);
            soundCount = getData().get("ground", "soundCount", int.class);
            footstepSfx = new Sound[soundCount];
            for (int index = 0; index < soundCount; index++){
                footstepSfx[index] = getBaseResource().getSubResource((1 + index) + ".wav").loadAsSound();
            }
            exicuteScript("init");
        }
        random = new Random();
    }
    
    /**
     * tiles the image until it fits this entity's bounds
     * @param width the width of this entity
     */
    private void adjustTextureToBounds(float width){
        BufferedImage image = Java2DUtils.createTiledImage(getBaseResource().getSubResource("sprites.png").loadAsBufferedImage(), width);
        setSprites(new Image[]{Java2DUtils.convertToTexture(image)});
        editBounds(-1, -1, width, -1);
    }
    
    /**
     * generates and returns a piece of ground replicating this entity 
     * with the specified dimensions. generating a sub-piece makes this
     * entity excluded from ground searches
     * @param width the width of the new ground
     * @param height the height of the new ground
     * @return the generated ground entity
     */
    public EntityGround.Subpiece getSubpiece(float width, float height){
        hasSubpiece = true;
        return new Subpiece(this, width, height);
    }
    
    /**
     * @return a random footstep sound effect for this ground type
     */
    protected Sound getStepSound(){
        return footstepSfx[random.nextInt(soundCount)];
    }
    
    protected void setFootstepSfx(Sound[] footstepSfx){this.footstepSfx = footstepSfx;}
    protected void setSubpiece(boolean subpiece){this.subpiece = subpiece;}
    protected void setSoundCount(int soundCount){this.soundCount = soundCount;}
    
    public boolean getHasSubpiece(){return hasSubpiece;}
    
    private Sound[] footstepSfx;
    private Random random;
    private boolean subpiece, hasSubpiece;
    private int soundCount;
    
    /**
     * a class representing a small piece of this entity to be placed
     */
    public static class Subpiece
        extends EntityGround {
        
        /**
         * creates a new sub-piece
         * @param owner the owner of the sub-piece
         * @param width the width to use
         * @param height the height to use
         */
        private Subpiece(EntityGround owner, float width, float height){
            super(null);
            getBehavior().addRenders(false);
            setName(owner.getName() + " sub");
            setSubpiece(true);
            setFootstepSfx(owner.footstepSfx);
            setSoundCount(owner.soundCount);
            editBounds(-1, -1, width, height);
        }
        
    }
}
