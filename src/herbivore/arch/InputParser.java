package herbivore.arch;
import herbivore.misc.InputList;

/**
 * an interface for implementation by classes that parse input
 * @author herbivore
 */
public interface InputParser {

    /**
     * a function to parse the input contained in the specified input list
     * @see herbivore.misc.InputList
     * @param inputList the input list to use
     */
    public void parseInput(InputList inputList);
    
}
