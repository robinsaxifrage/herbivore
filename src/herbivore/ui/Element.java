package herbivore.ui;
import herbivore.arch.Clickable;
import herbivore.arch.Renderable;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * a template class for implementation by elements to be
 * used in a gui
 * @author herbivore
 */
public abstract class Element
    implements Renderable, Clickable {
    
    /**
     * sets the location of this ui element
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setLocation(int x, int y){
        bounds.setLocation(x, y);
    }
    
    /**
     * updates the focused state of this element
     * @param mouseLocation the location of the mouse
     * @param mouseDown whether or not the mouse is down
     */
    public void updateFocusState(Point mouseLocation, boolean mouseDown){
    }
    
    protected void setBounds(Rectangle bounds){this.bounds = bounds;}
    protected void setText(String text){this.text = text;}
    
    @Override
    public Rectangle getClickBounds(){return bounds;}
    public Rectangle getBounds(){return bounds;}
    @Override
    public String getClickedName(){return text;}
    public String getText(){return text;}
    public boolean isFocused(){return false;}
        
    private Rectangle bounds;
    private String text;
}
