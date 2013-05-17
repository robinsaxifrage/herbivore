package herbivore.game;
import herbivore.arch.Clickable;
import herbivore.config.BuildInfo;
import herbivore.misc.InputList;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.ui.ActionListener;
import herbivore.ui.ButtonElement;
import herbivore.ui.ButtonParser;
import herbivore.render.Font;
import herbivore.ui.ImageElement;
import herbivore.ui.TextElement;
import herbivore.ui.Element;
import herbivore.ui.UIUtils;
import java.util.Arrays;
import java.util.List;

/**
 * a class for viewing of note entity contents
 * @see herbivore.game.entity.EntityItemNote
 * @author herbivore
 */
public class IngameUINote
    implements IngameUI {

    /**
     * creates a new gui displaying the specified .note archive
     * @param resource the .note archive
     */
    public IngameUINote(Resource resource){
        String[] contents = resource.getSubResource("contents.txt").loadAsText().split("\n");
        Font font = UIUtils.getFont("noteTextFontSize", "noteTextFontColor");
        elements = new Element[contents.length];
        for (int index = 0; index < elements.length; index++){
            elements[index] = new TextElement(contents[index], "noteTextFontSize", "noteTextFontColor");
        }
        closeButton = new ButtonElement("close", "menuFontSize", new ActionListener(){
            @Override
            public void actionPerformed(){
                ended = true;
            }
        });
        background = new ImageElement(resource.getSubResource("background.png"), BuildInfo.getTextureResizeRatio()*5);
        int halfBackgroundWidth = background.getBounds().width/2;
        int halfBackgroundHeight = background.getBounds().height/2;
        int leftShift = Renderer.get().getWindowWidth()/2 - halfBackgroundWidth;
        int topShift = Renderer.get().getWindowHeight()/2 - halfBackgroundHeight;
        int lineHeight = font.getHeight("x");
        int textHeight = lineHeight*(elements.length);
        int startY = halfBackgroundHeight - textHeight/2;
        for (int index = 0; index < elements.length; index++){
            Element next = elements[index];
            next.setLocation(leftShift + halfBackgroundWidth - font.getWidth(next.getText())/2, topShift + startY + (lineHeight*index));
        }
        closeButton.setLocation(leftShift + halfBackgroundWidth - font.getWidth(closeButton.getText())/2, topShift + halfBackgroundHeight*2 + lineHeight/2);
        background.setLocation(leftShift, topShift);
        buttonParser = new ButtonParser((ButtonElement)closeButton);
    }
    
    /**
     * @see herbivore.arch.Renderable#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        background.render(renderer);
        for (Element element : elements){
            element.render(renderer);
        }
        closeButton.render(renderer);
    }
    
    /**
     * @see herbivore.arch.InputParser#parseInput(herbivore.misc.InputList)
     */
    @Override
    public void parseInput(InputList inputList){
        buttonParser.parseInput(inputList);
    }
    
    /**
     * @see herbivore.game.IngameUI#isEnded()
     */
    @Override
    public boolean isEnded(){
        return ended;
    }
    
    /**
     * @see herbivore.arch.ClickableHolder#getClickables(java.util.List)
     */
    @Override
    public void getClickables(List<Clickable> clickables){
        clickables.addAll(Arrays.asList(elements));
        clickables.add(background);
        clickables.add(closeButton);
    }

    private Element[] elements;
    private ButtonParser buttonParser;
    private Element background, closeButton;
    private boolean ended;
}
