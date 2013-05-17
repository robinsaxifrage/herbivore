package herbivore.ui;
import herbivore.render.Renderer;
import java.awt.Rectangle;

/**
 * a ui element representing an empty space
 * @author herbivore
 */
public class GapElement
    extends Element {
        
    /**
     * creates a new gap element with the specified dimensions
     * @param width the width dimension
     * @param height the height dimension
     */
    public GapElement(int width, int height){
        setBounds(new Rectangle(width, height));
    }
    
    /**
     * satisfy the abstract method, or in other words, do jack shit
     */
    @Override
    public void render(Renderer renderer){
    }
    
}
