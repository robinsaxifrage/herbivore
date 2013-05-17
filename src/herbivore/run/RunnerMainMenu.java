package herbivore.run;
import herbivore.arch.Clickable;
import herbivore.game.Sky;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.sound.FadeOutModifier;
import herbivore.sound.Sound;
import herbivore.ui.Element;
import herbivore.ui.ImageElement;
import java.awt.Rectangle;
import java.util.List;

/**
 * a menu runner that represents the main menu
 * @author herbivore
 */
public class RunnerMainMenu 
    extends RunnerMenu {

    /**
     * creates a new runner main menu with the <code>res/menu/main.menu</code> resource
     */
    public RunnerMainMenu(){
        super(Resource.getResource("res/menu/main.menu"), "mainMenuFontSize");
        setClearsStack(true);
    }
    
    /**
     * @see herbivore.run.Runner#init()
     */
    @Override
    protected void init(){
        title = new ImageElement(Resource.getResource("res/art/title.png"), 5);
        title.setLocation(50, Renderer.get().getWindowHeight()/2 - (title.getBounds().height)/2);
        sky = Sky.get();
        music = Resource.getResource("res/sound/menu/titleMusic.wav").loadAsSound();
        super.init();
    }
    
    /**
     * @see herbivore.run.RunnerMenu#layout(herbivore.ui.Element[])
     */
    @Override
    protected void layout(Element[] elements){
        int textHeight = elements[0].getBounds().height;
        int longest = 0;
        for (Element element : elements){
            int width = element.getBounds().width;
            if (width > longest){
                longest = width;
            }
        }
        for (int index = 0; index < elements.length; index++){
            Rectangle titleBounds = title.getBounds();
            int y = titleBounds.y + titleBounds.height/2 - (elements.length*textHeight)/2 + (index*textHeight) + 20;
            int x = Renderer.get().getWindowWidth() - longest + (longest - elements[index].getBounds().width) - titleBounds.x;
            elements[index].setLocation(x, y);
        }
    }
        
    /**
     * @see herbivore.run.Runner#update(int)
     */
    @Override
    public void update(int delta){
        if (!musicPlaying){
            music.play(true);
            musicPlaying = true;
        }
        sky.update(delta);
        if (titleReturn){
            if (titleRotation < 6f){
                titleRotation += 0.004f*delta;
            }
            else {
                titleReturn = false;
            }
        }
        else {
            if (titleRotation > -6f){
                titleRotation -= 0.004f*delta;
            }
            else {
                titleReturn = true;
            }
        }
    }
    
    /**
     * @see herbivore.run.RunnerMenu#render(herbivore.render.Renderer, herbivore.ui.Element[])
     */
    @Override
    public void render(Renderer renderer, Element[] elements){
        sky.render(renderer);
        renderer.setRotation(titleRotation);
        title.render(renderer);
        renderer.setRotation(0f);
        for (Element element : elements){
            element.render(renderer);
        }
        renderer.setAlpha(1f);
    }
    
    /**
     * @see herbivore.run.Runner#destroy()
     */
    @Override
    protected void destroy(){
        music.setModifier(new FadeOutModifier(3000));
    }
    
    /**
     * @see herbivore.run.Runner#getClickables(java.util.List)
     */
    @Override
    public void getClickables(List<Clickable> clickables){
        super.getClickables(clickables);
        clickables.add(title);
    }
    
    private Element title;
    private Sound music;
    private Sky sky;
    private boolean titleReturn, musicPlaying;
    private float titleRotation;
}