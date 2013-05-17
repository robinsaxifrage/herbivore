package herbivore.sound;
import herbivore.arch.Updatable;
import herbivore.config.BuildInfo;
import herbivore.game.entity.Entity;
import herbivore.geom.Bounds;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

/**
 * a class representing an openal buffer that can play
 * in a variety of different ways
 * @author herbivore
 */
public class Sound
    implements Updatable {
    
    /**
     * creates a new sound with the specified openal buffer;
     * @param buffer the openal buffer reference to use
     */
    public Sound(int buffer){
        this.buffer = buffer;
        internalVolume = 1f;
        source = -1;
        SoundEngine.get().addSound(this);
    }
    
    /**
     * @see herbivore.arch.Updatable#update(int)
     */
    @Override
    public void update(int delta){
        if (isPlaying() && modifier != null){
            modifier.update(delta);
            if (modifier.isFinished()){
                modifier = null;
            }
        }
    }
    
    /**
     * plays this sound once
     */
    public void play(){
        play(null, false);
    }
    
    /**
     * plays this sound at the specified entity
     * @param sourceEntity the source entity of this sound
     */
    public void play(Entity sourceEntity){
        play(sourceEntity, false);
    }
    
    /**
     * plays this with the specified looping flag
     * @param looping the looping flag
     */
    public void play(boolean looping){
        play(null, looping);
    }

    /**
     * plays this sound at the specified entity with the specified looping flag
     * @param sourceEntity the source entity of this sound
     * @param looping the looping flag
     */
    public void play(Entity sourceEntity, boolean looping){
        this.looping = looping;
        SoundEngine soundEngine = SoundEngine.get();
        source = soundEngine.findFreeSource();
        AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
        AL10.alSourcef(source, AL10.AL_PITCH, 1f);
        AL10.alSourcef(source, AL10.AL_GAIN, soundEngine.getSoundVolume()); 
        AL10.alSourcei(source, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
        calculateDistance(sourceEntity, soundEngine.getListener());
        AL10.alSourcePlay(source);
    }
    
    /**
     * calculates the distance between a source and listener entity, packs it
     * and loads it to openal
     * @param sourceEntity the source entity
     * @param listenerEntity the listener entity
     */
    private void calculateDistance(Entity sourceEntity, Entity listenerEntity){
        float xDistance = 0;
        float yDistance = 0;
        if (sourceEntity != null && listenerEntity != null){
            Bounds listenerBounds = listenerEntity.getBounds();
            Bounds sourceBounds = sourceEntity.getBounds();
            xDistance = sourceBounds.x - listenerBounds.x;
            yDistance = sourceBounds.y - listenerBounds.y;
            xDistance /= BuildInfo.getAudioProximityDenominator();
            yDistance /= BuildInfo.getAudioProximityDenominator();
        }
        velocity = BufferUtils.createFloatBuffer(3).put(new float[3]);
        position = BufferUtils.createFloatBuffer(3).put(new float[]{xDistance, yDistance, 1f});
        velocity.flip();
        position.flip();
        AL10.alSource(source, AL10.AL_POSITION, position);
    	AL10.alSource(source, AL10.AL_VELOCITY, velocity);
    }
    
    /**
     * stops this sound
     */
    public void stop(){
        AL10.alSourceStop(source);
    }
    
    /**
     * flags this sound as one that is loaded once and used
     * many times throughout the program. this stops the sound engine
     * from releasing this sounds handle. is used by the button focus and
     * click sound effects, for example
     */
    public void flagUnreleasable(){
        SoundEngine.get().addUnreleasable(source);
    }
     
    /**
     * sets the specified openal source property
     * @param propertyName the name of the property to set
     * @param value the value to set the property to 
     */
    protected void setProperty(int propertyName, float value){
        if (propertyName == AL10.AL_GAIN){
            if (mute){
                value = 0f;
            }
            else {
                value *= getVolume();
            }
        }
        AL10.alSourcef(source, propertyName, value);
    }
    
    /**
     * @return the sound volume that this sound should be played at
     */
    private float getVolume(){
        return SoundEngine.get().getSoundVolume()*internalVolume;
    }
        
    /**
     * @return whether or not this sound is playing
     */
    public boolean isPlaying(){
        return AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }
    
    /**
     * sets the internal volume of this sound. the sounds playback
     * volume is calculated by <code>internalVolume*engineVolume</code>
     * @param internalVolume the internal volume to use
     */
    public void setInternalVolume(float internalVolume){
        this.internalVolume = internalVolume;
        setProperty(AL10.AL_GAIN, 1f);
    }
    
    /**
     * mutes or un-mutes this sound
     * @param mute the mute flag to set
     */
    public void setMute(boolean mute){
        this.mute = mute;
        setProperty(AL10.AL_GAIN, 0f);
    }
    
    /**
     * sets this sounds current sound modifier
     * @param modifier the modifier to use
     */
    public void setModifier(SoundModifier modifier){
        this.modifier = modifier;
        modifier.setSound(this);
    }
    
    protected boolean isLooping(){return looping;}
    protected int getSource(){return source;}

    private SoundModifier modifier;
    private FloatBuffer position, velocity;
    private boolean mute, looping;
    private float internalVolume;
    private int buffer, source;
}