package herbivore.game;
import herbivore.run.RunnerGame;

/**
 * an interface for implementation by classes that can generate worlds
 * @author herbivore
 */
public interface WorldGenerator {
    
    /**
     * generates the world for the specified game
     * @param game the game to generate the world for
     * @return the generated world
     */
    public abstract Level generate(RunnerGame game);
    
}
 