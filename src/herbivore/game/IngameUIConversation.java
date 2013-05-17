package herbivore.game;
import herbivore.game.entity.EntityActor;
import herbivore.geom.Bounds;
import herbivore.arch.Clickable;
import herbivore.misc.InputList;
import herbivore.render.Renderer;
import herbivore.ui.ActionListener;
import herbivore.ui.ButtonElement;
import herbivore.ui.ButtonParser;
import herbivore.render.Font;
import herbivore.ui.TextElement;
import herbivore.ui.Element;
import herbivore.ui.UIUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.newdawn.slick.Color;

/**
 * a class for non player character to player conversation wrapping
 * @see herbivore.game.ConversationTree
 * @see herbivore.game.entity.EntityActor#currentConversation
 * @author herbivore
 */
public class IngameUIConversation
    implements IngameUI {

    /**
     * creates a new conversation wrapper with the specified conversation
     * @param conversation the conversation to use
     */
    public IngameUIConversation(ConversationTree conversation){
        this.conversation = conversation;
        conversation.setUI(this);
        conversation.start();
    }
    
    /**
     * the function to load, create and then layout the contents of the gui
     */
    protected void layoutContents(){
        List<String> outputStrings = conversation.getOutput();
        List<String> inputStrings = conversation.getInput();
        Font font = UIUtils.getFont("conversationMenuFontSize", "basicFontColor");
        int lineHeight = font.getHeight("x");
        int height;
        int outputSize = outputStrings.size();
        int inputSize = inputStrings.size();
        if (outputSize > inputSize){
            height = lineHeight*outputSize;
        }
        else {
            height = lineHeight*inputSize;
        }
        height += 20;
        Font titleFont = UIUtils.getFont("conversationMenuTitleFontSize", "basicFontColor");
        int titleLineHeight = titleFont.getHeight("x");
        titleOne = new TextElement(conversation.getOwner().getName(), "conversationMenuTitleFontSize");
        titleTwo = new TextElement(conversation.getOther(conversation.getOwner()).getName(), "conversationMenuTitleFontSize");
        int longestOutput = titleOne.getBounds().width;
        int longestInput = titleTwo.getBounds().width;
        for (String string : outputStrings){
            int width = font.getWidth(string);
            if (width > longestOutput){
                longestOutput = width;
            }
        }
        for (String string : inputStrings){
            int width = font.getWidth(string);
            if (width > longestInput){
                longestInput = width;
            }
        }
        int width = longestOutput + longestInput + 40;
        int leftShift = Renderer.get().getWindowWidth()/2 - width/2;
        int topShift = Renderer.get().getWindowHeight()/2 - height/2;
        background = UIUtils.getPopupBackground(new Bounds(leftShift, topShift, width, height));
        closeButton = new ButtonElement("close", "menuFontSize", new ActionListener(){
            @Override
            public void actionPerformed(){
                end();
            }
        });
        outputs = new Element[outputSize];
        inputs = new ButtonElement[inputSize];
        for (int index = 0; index < outputSize; index++){
            Element element = new TextElement(outputStrings.get(index), "conversationMenuFontSize");
            element.setLocation(leftShift + 10, topShift + 10 + (index*lineHeight));
            outputs[index] = element;
        }
        for (int index = 0; index < inputSize; index++){
            final String string = inputStrings.get(index);
            inputs[index] = new ButtonElement(string, "conversationMenuFontSize", new ActionListener(){
                @Override
                public void actionPerformed(){
                    conversation.processInput(string);
                }
            });
            inputs[index].setLocation(leftShift + longestOutput + 30, topShift + 10 + (index*lineHeight));
        }
        background.setLocation(leftShift, topShift);
        closeButton.setLocation(leftShift + width/2 - font.getWidth(closeButton.getText())/2, topShift + height + 10);
        titleOne.setLocation(leftShift - titleFont.getWidth(titleOne.getText()) - 10, topShift + height/2 - titleLineHeight/2);
        titleTwo.setLocation(leftShift + width + 10, topShift + height/2 - titleLineHeight/2);
        lineX = leftShift + longestOutput + 20;
        lineYTop = topShift;
        lineYBottom = topShift + height;
        List<ButtonElement> buttons = new ArrayList();
        buttons.addAll(Arrays.asList(inputs));
        buttons.add((ButtonElement)closeButton);
        buttonParser = new ButtonParser(buttons);
    }
    
    /**
     * wrapper method
     * @see herbivore.game.ConversationTree#getOther(herbivore.game.entity.EntityActor)
     * @param actor the wrapped methods argument
     * @return the wrapped methods return value
     */
    public EntityActor getOther(EntityActor actor){
        return conversation.getOther(actor);
    }
    
    /**
     * @see herbivore.arch.Renderable#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        background.render(renderer);
        for (Element element : outputs){
            element.render(renderer);
        }
        for (Element element : inputs){
            element.render(renderer);
        }
        titleOne.render(renderer);
        titleTwo.render(renderer);
        closeButton.render(renderer);
        renderer.drawLine(lineX, lineYTop, lineX, lineYBottom, 4, Color.white);
    }
    
    /**
     * @see herbivore.arch.InputParser#parseInput(herbivore.misc.InputList)
     */
    @Override
    public void parseInput(InputList inputList){
        buttonParser.parseInput(inputList);
    }
    
    /**
     * ends the conversation forcibly
     */
    public void end(){
        forcedEnd = true;
    }
    
    /**
     * @see herbivore.game.IngameUI#isEnded()
     */
    @Override
    public boolean isEnded(){
        return forcedEnd || conversation.isEnded();
    }
    
    /**
     * @see herbivore.arch.ClickableHolder#getClickables(java.util.List)
     */
    @Override
    public void getClickables(List<Clickable> clickables){
        clickables.addAll(Arrays.asList(inputs));
        clickables.addAll(Arrays.asList(outputs));
        clickables.add(closeButton);
        clickables.add(background);
        clickables.add(titleOne);
        clickables.add(titleTwo);
    }
    
    private ConversationTree conversation;
    private ButtonElement[] inputs;
    private ButtonParser buttonParser;
    private Element[] outputs;
    private Element background, closeButton, titleOne, titleTwo;
    private boolean forcedEnd;
    private int lineX, lineYTop, lineYBottom;
}
