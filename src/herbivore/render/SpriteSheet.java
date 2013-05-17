package herbivore.render;
import herbivore.res.Resource;
import java.awt.image.BufferedImage;
import org.newdawn.slick.Image;

/**
 * a class representing a sprite sheet that can be
 * chopped up into sub-images, or sprites
 * @author herbivore
 */
public class SpriteSheet {

    /**
     * creates a new sprite sheet from an image resource
     * @param resource the image resource
     */
    public SpriteSheet(Resource resource){
        this(resource.loadAsBufferedImage());
    }
    
    /**
     * creates a new sprite sheet with a base image
     * @param base the base image to use
     */
    public SpriteSheet(BufferedImage base){
        this.base = base;
    }
    
    /**
     * sets the default width and height of sub-images chopped
     * from this sprite sheet
     * @param defaultWidth the default width to use
     * @param defaultHeight the default height to use
     */
    public void setDefaultSize(int defaultWidth, int defaultHeight){
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
    }
    
    /**
     * chops the specified section as a buffered image
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width dimension
     * @param height the height dimension
     * @return the chopped image
     */
    public BufferedImage chopAsBuffered(int x, int y, int width, int height){
        return base.getSubimage(x, y, width, height);
    }
    
    /**
     * chops the specified section as a buffered image with the default
     * width and height
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the chopped image
     */
    public BufferedImage chopAsBuffered(int x, int y){
        return base.getSubimage(x, y, defaultWidth, defaultHeight);
    }
    
    /**
     * chops the specified section as a slickutil image
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width dimension
     * @param height the height dimension
     * @return the chopped image
     */
    public Image chop(int x, int y, int width, int height){
        return Java2DUtils.convertToTexture(base.getSubimage(x, y, width, height));
    }
    
    /**
     * chops the specified section as a slickutil image with the default
     * width and height
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the chopped image
     */
    public Image chop(int x, int y){
        return chop(x, y, defaultWidth, defaultHeight);
    }
    
    public int getWidth(){return base.getWidth();}
    public int getHeight(){return base.getHeight();}
    
    private BufferedImage base;
    private int defaultWidth, defaultHeight;
}