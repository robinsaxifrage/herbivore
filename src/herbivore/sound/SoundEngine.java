package herbivore.sound;
import herbivore.arch.Updatable;
import herbivore.config.ConfigUtils;
import herbivore.game.entity.Entity;
import herbivore.misc.Logger;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;

/**
 * a class representing the sound engine, used to initialize
 * openal and manage sources
 * @author herbivore
 */
public class SoundEngine
    implements Updatable {
    
    /**
     * @return the singleton instance of this sound engine
     */
    public static SoundEngine get(){
        if (instance == null){
            instance = new SoundEngine();
        }
        return instance;
    }
    
    private static SoundEngine instance;
    
    /**
     * creates a new sound system
     */
    private SoundEngine(){
        sourceCountCap = ConfigUtils.get("sound", "sourceCap", int.class);
        soundVolume = ConfigUtils.get("sound", "soundVolume", float.class);
        musicVolume = ConfigUtils.get("sound", "musicVolume", float.class);
        try {
            AL.create();
        }
        catch (LWJGLException exception){
            Logger.error(exception, "audio library creation failed");
        }
        sounds = new ArrayList();
        unreleasable = new ArrayList();
        listener = null;
        init_initializeOpenAL();
    }
    
    /**
     * initializes openal and generates the sources. the number
     * of sources is raised until it either hits the system max
     * or the amount specified in the configuration file
     */
    private void init_initializeOpenAL(){
        sourceCount = 0;
        sources = BufferUtils.createIntBuffer(sourceCountCap);
	while (AL10.alGetError() == AL10.AL_NO_ERROR){
            IntBuffer temp = BufferUtils.createIntBuffer(1);
            try {
                AL10.alGenSources(temp);
		if (AL10.alGetError() == AL10.AL_NO_ERROR) {
                    sourceCount++;
                    sources.put(temp.get(0));
                    if (sourceCount > sourceCountCap - 1) {
                        break;
                    }
                } 
            }
            catch (OpenALException e){
                break;
            }
	}
        Logger.info(sourceCount + " audio sources generated");
        FloatBuffer orientation = BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
	FloatBuffer velocity = BufferUtils.createFloatBuffer(3).put(new float[3]);
	FloatBuffer position = BufferUtils.createFloatBuffer(3).put(new float[] {0f, 0f, 0f});
	position.flip();
	velocity.flip();
	orientation.flip();
	AL10.alListener(AL10.AL_POSITION, position);
	AL10.alListener(AL10.AL_VELOCITY, velocity);
	AL10.alListener(AL10.AL_ORIENTATION, orientation);
    }
    
    /**
     * searches for and finds the a free source reference
     * @return the free source reference, or -1 if all the sources are in use
     */
    protected int findFreeSource(){
        for (int index = 0; index < sourceCount - 1; index++){
            int source = sources.get(index);
            if (!unreleasable.contains(source) && AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING){
                return source;
            }
        }
        return -1;
    }
    
    /**
     * @see herbivore.arch.Updatable#update(int)
     */
    @Override
    public void update(int delta){
        for (int index = 0; index < sounds.size(); index++){
            Sound sound = sounds.get(index);
            sound.update(delta);
            if (!sound.isPlaying() && !unreleasable.contains(sound.getSource())){
                sounds.remove(sound);
            }
        }
    }
    
    /**
     * adds a sound for this sound engine to manage
     * @param sound the sound to add
     */
    protected void addSound(Sound sound){
        sounds.add(sound);
    }
        
    /**
     * adds a source to the list of sources not to be released
     * @param source 
     */
    protected void addUnreleasable(Integer source){
        unreleasable.add(source);
    }
    
    /**
     * sets the sound volume
     * @param soundVolume the new sound volume
     */
    public void setSoundVolume(float soundVolume){
        this.soundVolume = soundVolume;
        ConfigUtils.put("sound", "soundVolume", soundVolume);
        for (Sound sound : sounds){
            if (!sound.isLooping()){
                sound.setInternalVolume(soundVolume);
            }
        }
    }
    
    /**
     * sets the sound volume
     * @param musicVolume the new sound volume
     */
    public void setMusicVolume(float musicVolume){
        this.musicVolume = musicVolume;
        ConfigUtils.put("sound", "musicVolume", musicVolume);
        for (Sound sound : sounds){
            if (sound.isLooping()){
                sound.setInternalVolume(musicVolume);
            }
        }
    }
    
    public void setListener(Entity listener){this.listener = listener;}
    
    protected Entity getListener(){return listener;}
    protected float getSoundVolume(){return soundVolume;}
    protected float getMusicVolume(){return musicVolume;}
    
    private List<Integer> unreleasable;
    private List<Sound> sounds;
    private IntBuffer sources;
    private Entity listener;
    private float soundVolume, musicVolume;
    private int sourceCount, sourceCountCap;
}