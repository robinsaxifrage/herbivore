package herbivore.run;
import herbivore.Herbivore;
import herbivore.config.ConfigUtils;
import herbivore.geom.Bounds;
import herbivore.geom.Location;
import herbivore.misc.InputList;
import herbivore.render.Font;
import herbivore.render.Java2DUtils;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.ui.UIUtils;
import org.newdawn.slick.Image;

/**
 * a runner to wait for input and then draw the splash screen after startup
 * @author herbivore
 */
public class RunnerSplashScreen
    extends Runner {
    
    /**
     * @see herbivore.run.Runner#init()
     */
    @Override
    protected void init(){
        prompt = ConfigUtils.get("splash", "promptText");
        time = (int)(ConfigUtils.get("splash", "seconds", float.class)*1000);
        image = Resource.getResource("res/art/startup.png").loadAsImage();
        promptFont = UIUtils.getFont("splashFontSize", "splashFontColor");
        int windowWidth = Renderer.get().getWindowWidth();
        int windowHeight = Renderer.get().getWindowHeight();
        imageBounds = new Bounds(windowWidth/2 - image.getWidth()/2, windowHeight/2 - image.getHeight()/2, image.getWidth(), image.getHeight());
        promptLocation = new Location(windowWidth/2 - promptFont.getWidth(prompt)/2, windowHeight/2 - promptFont.getHeight(prompt)/2);
        backgroundColor = Java2DUtils.convertToTexture(Java2DUtils.generateFill(UIUtils.getColor("splashColor"), 1f));
        screen = new Bounds(windowWidth, windowHeight);
        mainMenu = new RunnerMainMenu();
        mainMenu.doInit(Renderer.get());
    }

    /**
     * @see herbivore.run.Runner#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        if (started){
            if (time < 1000){
                mainMenu.render(renderer);
                renderer.setAlpha(time/1000f);
            }
            renderer.drawImage(backgroundColor, screen);
            renderer.drawImage(image, imageBounds);
            renderer.setAlpha(1f);
        }
        else {
            renderer.drawImage(backgroundColor, screen);
            renderer.drawString(prompt, promptFont, promptLocation.x, promptLocation.y);
        }
    }
    
    /**
     * @see herbivore.run.Runner#update(int)
     */
    @Override
    public void update(int delta){
        if (started){
            if (time < 0){
                Herbivore.get().pushRunner(mainMenu);
            }
            mainMenu.update(delta);
            time -= delta;
        }
        else {

        }
    }
    
    /**
     * @see herbivore.run.Runner#parseInput(herbivore.misc.InputList)
     */
    @Override
    public void parseInput(InputList inputList){
        if (inputList.getReleases().size() > 0){
            if (!started){
                started = true;
            }
            else if (time > 1000){
                time = 1000;
            }
        }
    }

    private Location promptLocation;
    private Bounds imageBounds, screen;
    private Runner mainMenu;
    private String prompt;
    private Image image, backgroundColor;
    private Font promptFont;
    private boolean started;
    private int time;
}
