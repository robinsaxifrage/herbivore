package herbivore.game;
import herbivore.arch.ClickableHolder;
import herbivore.arch.InputParser;
import herbivore.arch.Renderable;

/**
 * an interface for implementation by in game gui's
 * @author herbivore
 */
public interface IngameUI
    extends Renderable, InputParser, ClickableHolder{
    
    /**
     * @return whether or not the gui is finished executing
     */
    public boolean isEnded();
    
}
