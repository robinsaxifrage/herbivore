package herbivore.sound;
import org.lwjgl.openal.AL10;

/**
 * a sound modifier that fades out and then ends
 * a sound
 * @author herbivore
 */
public class FadeOutModifier
    extends SoundModifier {

    /**
     * creates a new fade out modified with the specified run time
     * @param time the run time, in milliseconds
     */
    public FadeOutModifier(int time){
        this.time = time;
        maxTime = time;
    }
    
    /**
     * @see herbivore.sound.SoundModifier#update(int)
     */
    @Override
    public void update(int delta){
        if (time > 0){
            time -= delta;
            getSound().setProperty(AL10.AL_GAIN, (time/(float)maxTime));
        }
        else {
            getSound().stop();
            setFinished(true);
        }
    }    
    
    private int time, maxTime;
}
