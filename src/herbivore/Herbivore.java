package herbivore;
import herbivore.config.BuildInfo;
import herbivore.config.ConfigUtils;
import herbivore.misc.InputList;
import herbivore.misc.Logger;
import herbivore.misc.ShutdownHook;
import herbivore.misc.SpeedTracker;
import herbivore.misc.UncaughtExceptionHandler;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.run.PopupRunner;
import herbivore.run.Runner;
import herbivore.run.RunnerConsole;
import herbivore.run.RunnerSplashScreen;
import herbivore.sound.SoundEngine;
import java.util.Stack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

/**
 * the primary flow control implementation class
 * @author herbivore
 */
public class Herbivore
    implements Runnable {
    
    /**
     * @return the singleton instance of the program
     */
    public static Herbivore get(){
        if (instance == null){
            instance = new Herbivore();
        }
        return instance;
    }
    
    private static Herbivore instance;
    
    /**
     * prevent outside creation of another instance
     */
    private Herbivore(){
    }
    
    /**
     * executes the program and manages flow control
     */
    @Override
    public void run(){
        run_init();
        //loop is broken with jvm exit
        while (true){
            run_loadCurrentRunner();
            while (currentRunner.isRunning()){
                if (Display.isCloseRequested()){
                    if (ConfigUtils.get("runtime", "windowCloseIsFq", boolean.class)){
                        Logger.debug("closing window is force ");
                        System.exit(0);
                    }
                    else {
                        pushRunner(new PopupRunner(Resource.getResource("res/menu/exitConfirm.menu")));
                    }
                }
                run_checkForNewRunner();
                run_update();
                run_render();
                run_parseInput();
                speedTracker.syncFramerate();
            }
            run_retireCurrentRunner();
        }
    }
    
    /**
     * initializes the program
     */
    private void run_init(){
        Logger.info(BuildInfo.getBuildTitle() + " initializing");
        //silence slick util logging
        Log.setVerbose(false);
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook()));
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        speedTracker = SpeedTracker.get();
        renderer = Renderer.get();
        soundEngine = SoundEngine.get();
        inputList = new InputList();
        runnerStack = new Stack();
        Runner first = new RunnerSplashScreen();
        pushRunner(first);
    }
    
    /**
     * loads and initializes the runner currently on top of the stack
     */
    private void run_loadCurrentRunner(){
        currentRunner = runnerStack.peek();
        if (currentRunner.getClearsStack()){
            for (int index = 0; index < runnerStack.size(); index++){
                Runner runner = runnerStack.get(index);
                if (!runner.equals(currentRunner)){
                    runner.doDestroy();
                }
            }
            runnerStack.clear();
            runnerStack.add(currentRunner);
        }
        currentRunner.doInit(renderer);
        Mouse.setGrabbed(currentRunner.getGrabsMouse());
        currentRunner.resume();
        currentRunner.setRunning(true);
    }
    
    /**
     * determines if the currently executing runner is still top of the stack.
     * if it is not, it is ended
     */
    private void run_checkForNewRunner(){
        if (!currentRunner.equals(runnerStack.peek())){
            currentRunner.setRunning(false);
        }
    }
    
    /**
     * wrapper for the current runners update method
     * calculates delta and then updates the current runner and sound engine
     */
    private void run_update(){
        speedTracker.update();
        int delta = speedTracker.getDelta();
        currentRunner.update(delta);
        soundEngine.update(delta);
    }
    
    /**
     * wrapper for the current runners render method.
     * prepares and disposes the renderer and swaps the buffers
     */
    private void run_render(){
        renderer.beginRender();
        currentRunner.render(renderer);
        renderer.endRender();
        Display.update();
    }
    
    /**
     * wrapper for the current runners input parsing method.
     * populates the input list and then checks to see if any universal events are contained
     */
    private void run_parseInput(){
        inputList.clear();
        inputList.populate(renderer.getWindowHeight());
        if (inputList.containsRelease("consoleToggle")){
            if (currentRunner instanceof RunnerConsole){
                currentRunner.terminate();
            }
            else {
                pushRunner(new RunnerConsole());
            }
        }
        if (inputList.containsRelease("screenshot")){
            renderer.captureScreenShot();
        }
        currentRunner.parseInput(inputList);
    }
        
    /**
     * retires the current runner and destroys it, if it was terminated
     */
    private void run_retireCurrentRunner(){
        if (currentRunner.wasTerminated()){
            currentRunner.doDestroy();
            runnerStack.remove(currentRunner);
        }
        currentRunner.setRunning(false);
    }

    /**
     * pushes a new runner onto the stack
     * @param runner the runner to push onto the stack
     */
    public void pushRunner(Runner runner){
        Logger.debug(runner.getClass().getName() + " pushed to stack");
        if (currentRunner instanceof RunnerConsole){
            runner.setParent(currentRunner.getParent());
        }
        else {
            runner.setParent(currentRunner);
        }
        runnerStack.push(runner);
    }
    
    private Stack<Runner> runnerStack;
    private SpeedTracker speedTracker;
    private SoundEngine soundEngine;
    private InputList inputList;
    private Renderer renderer;
    private Runner currentRunner;
}