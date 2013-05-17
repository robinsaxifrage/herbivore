package herbivore.ui;
import herbivore.arch.InputParser;
import herbivore.misc.InputList;
import java.util.Collection;
import org.lwjgl.input.Mouse;

/**
 * a class for use by any gui that has buttons to parse,
 * prevents repetition of functionality.
 * @author herbivore
 */
public class ButtonParser
    implements InputParser {

    /**
     * creates a new button parser with specified the button elements
     * @param elements the collection of button elements to use
     */
    public ButtonParser(Collection<? extends ButtonElement> elements){
        this(elements.toArray(new ButtonElement[elements.size()]));
    }
    
    /**
     * creates a new button parser with specified the button elements
     * @param elements the varargs list of button elements to use
     */
    public ButtonParser(ButtonElement... elements){
        this.elements = elements;
    }
    
    /**
     * @see herbivore.arch.InputParser#parseInput(herbivore.misc.InputList) 
     */
    @Override
    public void parseInput(InputList inputList){
        selection = -1;
        for (int index = 0; index < elements.length; index++){
            elements[index].updateFocusState(inputList.getMouseLocation(), Mouse.isButtonDown(0));
            if (elements[index].isFocused()){
                selection = index;
            }
        }
        if (inputList.containsMouseRelease()){
            if (selection < elements.length && selection >= 0){
                elements[selection].click();
            }
        }
    }
    
    private ButtonElement[] elements;
    private int selection;
}
