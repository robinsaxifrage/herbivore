package herbivore.run;
import herbivore.arch.Clickable;
import herbivore.geom.Bounds;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.ui.Element;
import herbivore.ui.TextElement;
import herbivore.ui.UIUtils;
import java.awt.Point;
import java.util.List;

/**
 * a menu runner representing menus that appear to be
 * pop-ups, drawn on top of their parent and only occupying
 * part of the screen
 * @author herbivore
 */
public class PopupRunner 
    extends RunnerMenu {
    
    /**
     * creates a new pop-up runner with the specified .menu resource
     * @param resource the .menu resource to load
     */
    public PopupRunner(Resource resource){
        super(resource, "menuFontSize");
        setUsesLoadingScreen(resource.getSubResource("useLoadingText").exists());
    }
    
    /**
     * @see herbivore.run.RunnerMenu#loadElements(nu.xom.Element)
     */
    @Override
    protected void loadElements(nu.xom.Element rootElement){
        title = new TextElement(rootElement.getAttributeValue("title"), "menuTitleFontSize");
        super.loadElements(rootElement);
    }
    
    /**
     * @see herbivore.run.RunnerMenu#layout(herbivore.ui.Element[])
     */
    @Override
    protected void layout(Element[] elements){
        int height = 10;
        int width = title.getBounds().width;
        for (Element element : elements){
            if (element.getBounds().width > width){
                width = element.getBounds().width;
            }
            element.setLocation(0, height);
            height += element.getBounds().getBounds().height;
        }
        width += 20;
        height += 10;
        int leftShift = Renderer.get().getWindowWidth()/2 - width/2;
        int topShift = Renderer.get().getWindowHeight()/2 - height/2;
        title.setLocation(leftShift, topShift - title.getBounds().height);
        for (Element element : elements){
            Point elementLoc = element.getBounds().getLocation();
            element.setLocation(elementLoc.x + leftShift + 10, elementLoc.y + topShift);
        }
        bounds = new Bounds(leftShift, topShift, width, height);
        background = UIUtils.getPopupBackground(bounds);
    }

    /**
     * @see herbivore.run.Runner#update(int)
     */
    @Override
    public void update(int delta){
        getParent().update(delta);
    }
    
    /**
     * @see herbivore.run.RunnerMenu#render(herbivore.render.Renderer, herbivore.ui.Element[])
     */
    @Override
    public void render(Renderer renderer, Element[] buttons){
        Runner runner = getParent();
        while (runner instanceof PopupRunner){
            runner = runner.getParent();
        }
        runner.render(renderer);
        background.render(renderer);
        title.render(renderer);
        for (Element button : buttons){
            button.render(renderer);
        }
    }
    
    /**
     * @see herbivore.run.Runner#getClickables(java.util.List)
     */
    @Override
    public void getClickables(List<Clickable> clickables){
        super.getClickables(clickables);
        clickables.add(title);
        clickables.add(background);
    }
    
    private Element title, background;
    private Bounds bounds;
}