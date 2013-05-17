package herbivore.arch;

/**
 * an interface for implementation by classes classes that have logical updates
 * @author herbivore
 */
public interface Updatable {

    /**
     * a function to update logic
     * @param delta the delta time, in milliseconds. used for time normalization
     */
    public void update(int delta);
    
}
