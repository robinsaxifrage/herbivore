package herbivore.arch;
import java.awt.Rectangle;

/**
 * an interface for implementation by classes that are "clickable". 
 * clickable classes selectable by the console, as long as they are
 * sent to it
 * @see herbivore.run.RunnerConsole#selectedClickables
 * @author herbivore
 */
public interface Clickable {
    /**
     * a function to get the bounds on the screen that can be clicked to select
     * this element
     * @return a rectangle representing the clickable area
     */
    public Rectangle getClickBounds();
    
    /**
     * @return the name to display in the console when this element is clicked
     */
    public String getClickedName();
    
}
