package herbivore.run;
import herbivore.arch.Clickable;
import herbivore.arch.ClickableHolder;
import herbivore.arch.InputParser;
import herbivore.arch.Renderable;
import herbivore.arch.Updatable;
import herbivore.misc.Logger;
import herbivore.render.Renderer;
import java.util.List;

/**
 * a template class for implementation by runners. runners 2nd
 * tier of program flow control, and are pushed on and off as
 * the program runs
 * @author herbivore
 */
public abstract class Runner
    implements Updatable, Renderable, InputParser, ClickableHolder {
    
    /**
     * protected because all runners need to be in this package
     * to work properly
     */
    protected Runner(){
    }
    
    /**
     * does the initialization of this runner, and draws a loading screen
     * if nessesary. NOTE: no initialization beyond definition of the runner
     * itself should occur in the constructor
     * @param renderer the renderer to draw the loading screen with
     */
    public void doInit(Renderer renderer){
        if (initialized){
            return;
        }
        Logger.info("initializing runner " + getClass().getSimpleName());
        if (usesLoadingScreen){
            renderer.drawLoadingFrame();
        }
        init();
        initialized = true;
    }
    
    /**
     * destroys this runner
     */
    public void doDestroy(){
        Logger.debug("destroying runner " + getClass().getSimpleName());
        destroy();
    }
    
    /**
     * a method to initialize all nessesary components
     */
    protected abstract void init();

    /**
     * a method to destroy all nessesary components
     */
    protected void destroy(){}
    
    /**
     * called when the runner is brought onto the stack
     */
    public void resume(){}
    
    /**
     * @see herbivore.arch.ClickableHolder#getClickables(java.util.List)
     */
    @Override
    public void getClickables(List<Clickable> clickables){}
    
    /**
     * terminates this runner. a terminated runner will be removed from
     * the stack as soon as the tick ends
     */
    public void terminate(){
        setRunning(false);
        terminated = true;
    }
    
    protected void setClearsStack(boolean clearsStack){this.clearsStack = clearsStack;}
    protected void setGrabsMouse(boolean grabsMouse){this.grabsMouse = grabsMouse;}
    protected void setUsesLoadingScreen(boolean usesLoadingScreen){this.usesLoadingScreen = usesLoadingScreen;}
    
    public void setParent(Runner parent){this.parent = parent;}
    public void setRunning(boolean running){this.running = running;}

    public Runner getParent(){return parent;}
    public boolean isRunning(){return running;}
    public boolean getClearsStack(){return clearsStack;}
    public boolean getGrabsMouse(){return grabsMouse;}
    public boolean wasTerminated(){return terminated;}
    
    private Runner parent;
    private boolean clearsStack, terminated, initialized, usesLoadingScreen, running, grabsMouse;
}