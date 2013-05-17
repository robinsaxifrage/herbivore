package herbivore.arch;
import java.util.List;

/**
 * an interface for implementation by classes that have clickable elements
 * @author herbivore
 */
public interface ClickableHolder {

    /**
     * a function to populate the specified list of clickable elements
     * @see herbivore.arch.Clickable
     * @param clickables the list of clickable elements to populate
     */
    public void getClickables(List<Clickable> clickables);
    
}
