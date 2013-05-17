package herbivore.ui;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import java.awt.Rectangle;
import org.newdawn.slick.Image;

/**
 * a ui element representing an image
 * @author herbivore
 */
public class ImageElement
    extends Element {

    /**
     * creates a new image element with the specified image and resize ratio
     * @param resource the image resource
     * @param resize the resize ratio
     */
    public ImageElement(Resource resource, int resize){
        this(resource, 0, 0, resize);
    }
    
    /**
     * creates a new image element with the specified image and resize ratio at the specified coordinates
     * @param resource the image resource
     * @param x the x coordinate
     * @param y the y coordinate
     * @param resize the resize ratio
     */
    public ImageElement(Resource resource, int x, int y, int resize){
        this(resource.loadAsImage(), resource.getResourcePath(), x, y, resize);
    }
    
    /**
     * creates a new image element with the specified image and resize ratio at the specified coordinates
     * @param image the image
     * @param name the name
     * @param x the x coordinate
     * @param y the y coordinate
     * @param resize the resize ratio
     */
    public ImageElement(Image image, String name, int x, int y, int resize){
        this(image, name, image.getWidth()*resize, image.getHeight()*resize, x, y);
    }
    
    /**
     * creates a new image element with the specified image and dimensions at the specified coordinates
     * @param image the image
     * @param name the name
     * @param width the width dimensions
     * @param height the height dimensions
     * @param x the x coordinates
     * @param y the y coordinates
     */
    public ImageElement(Image image, String name, int width, int height, int x, int y){
        this.image = image;
        setText(name);
        setBounds(new Rectangle(x, y, width, height));
    }
    
    /**
     * @see herbivore.ui.Element#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        renderer.drawImage(image, getBounds());
    }
    
    private Image image;
}
