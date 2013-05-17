package herbivore.misc;
import herbivore.config.ConfigUtils;
import org.lwjgl.opengl.Display;

/**
 * a class responsible for calculating delta times, syncing
 * frame rate and calculating frames per second
 * @author herbivore
 */
public class SpeedTracker {

    /**
     * @return the singleton instance of the speed tracker
     */
    public static SpeedTracker get(){
        if (instance == null){
            instance = new SpeedTracker();
        }
        return instance;
    }
    
    private static SpeedTracker instance;
    
    /**
     * creates a new speed tracker
     */
    private SpeedTracker(){
        deltaSamples = ConfigUtils.get("performance", "deltaSamples", int.class);
        fpsTarget = ConfigUtils.get("performance", "fpsTarget", int.class);
        deltaSampleStore = new int[deltaSamples];
        speedHack = 1;
        calculateDelta();
    }
    
    /**
     * calculates the last delta and stores it
     */
    public void update(){
        delta = calculateDelta();
        deltaSampleStore[deltaSampleIndex] = delta;
        if (deltaSampleIndex < deltaSampleStore.length - 1){
            deltaSampleIndex++;
        }
        else {
            deltaSampleIndex = 0;
        }
    }
    
    /**
     * synchronizes the frame rate to the frames per 
     * second target set in the configuration file
     */
    public void syncFramerate(){
        Display.sync(fpsTarget);
    }
    
    /**
     * calculates and returns the delta time, in milliseconds
     * @return the calculated value
     */
    private int calculateDelta(){
        long time = System.nanoTime()/1000000;
        int theDelta = (int)(time - lastTime);
        lastTime = time;
        return (int)(theDelta*speedHack);
    }
    
    /**
     * calculates and returns the frames per second, 
     * averaged out from the contents of the delta store
     * @return 
     */
    public int getFPS(){
        int total = delta;
        for (int tickDelta : deltaSampleStore){
            total += tickDelta;
        }
        int theDelta = total/(1 + deltaSampleStore.length);
        return 1000/theDelta;
    }
    
    public void setSpeedHack(float speedHack){this.speedHack = speedHack;}
    
    public int getDelta(){return delta;}
    
    private float speedHack;
    private int[] deltaSampleStore;
    private long lastTime;
    private int delta, deltaSamples, deltaSampleIndex, fpsTarget;
}
