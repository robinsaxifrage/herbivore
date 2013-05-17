package herbivore.sound;
import herbivore.arch.Updatable;

/**
 * a template class for implementation by classes
 * that want to modify a sound over a period of time
 * @see herbivore.sound.Sound#modifier
 * @author herbivore
 */
public abstract class SoundModifier
    implements Updatable {
        
    protected void setSound(Sound sound){this.sound = sound;}
    public void setFinished(boolean finished){this.finished = finished;}
    
    protected Sound getSound(){return sound;}
    protected boolean isFinished(){return finished;}
    
    private Sound sound;
    private boolean finished;
}
