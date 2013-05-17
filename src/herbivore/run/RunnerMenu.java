package herbivore.run;
import herbivore.Herbivore;
import herbivore.arch.Clickable;
import herbivore.misc.InputList;
import herbivore.misc.Logger;
import herbivore.render.Font;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.script.ScriptFile;
import herbivore.ui.ActionListener;
import herbivore.ui.ButtonElement;
import herbivore.ui.ButtonParser;
import herbivore.ui.Element;
import herbivore.ui.GapElement;
import herbivore.ui.TextElement;
import herbivore.ui.UIUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nu.xom.Elements;
import org.lwjgl.input.Keyboard;

/**
 * a template class for all menu runners that are
 * loaded from a .menu archive
 * @author herbivore
 */
public abstract class RunnerMenu
    extends Runner {

    /**
     * creates a new menu with the specified .menu resource and font type
     * @param resource the .menu resource
     * @param fontType the font type to use
     */
    public RunnerMenu(Resource resource, String fontType){
        this.resource = resource;
        this.fontType = fontType;
    }
        
    /**
     * @see herbivore.run.Runner#init()
     */
    @Override
    protected void init(){
        scriptFile = resource.getSubResource("script.js").loadAsScriptFile();
        loadElements(resource.getSubResource("layout.xml").loadAsXml().getRootElement());
        layout(elements);
        List<ButtonElement> buttons = new ArrayList();
        for (Element element : elements){
            if (element instanceof ButtonElement){
                buttons.add((ButtonElement)element);
            }
        }
        buttonParser = new ButtonParser(buttons);
    }
    
    /**
     * parses the xml of this menu and loads it into ui elements
     * @param rootElement the root element of the xml document to use
     */
    protected void loadElements(nu.xom.Element rootElement){
        Elements children = rootElement.getChildElements();
        inputTarget = rootElement.getAttributeValue("input_target");
        refresh = rootElement.getAttributeValue("refresh");
        List<Element> elementsList = new ArrayList();
        for (int index = 0; index < children.size(); index++){
            nu.xom.Element element = children.get(index);
            Font font = UIUtils.getFont(fontType, "basicFontColor");
            String text = element.getValue();
            String elementValueSource = element.getAttributeValue("source");
            if (elementValueSource != null){
                text = (String)scriptFile.invoke(elementValueSource);
            }
            switch (element.getLocalName()){
                case "text":
                    if (!text.equals("")){
                        elementsList.add(new TextElement(text, fontType));
                    }
                    break;
                case "gap":
                    elementsList.add(new GapElement(1, font.getHeight("x")));
                    break;
                case "button":
                    if (!text.equals("")){
                        ActionListener listener = generateListener(element.getAttributeValue("action").split(":"));
                        elementsList.add(new ButtonElement(text, fontType, listener));
                    }
                    break;
            }
        }
        elements = elementsList.toArray(new Element[elementsList.size()]);
    }
    
    /**
     * generates a listener from the string form data
     * @param listenerData the data of this listener
     * @return the generated listener
     */
    protected ActionListener generateListener(final String[] listenerData){
        final Runner me = this;
        switch (listenerData[0]){
            case "push":
                return new ActionListener(){
                    @Override
                    public void actionPerformed(){
                        Herbivore.get().pushRunner(new PopupRunner(Resource.getResource("res/menu/" + listenerData[1] + ".menu")));
                    }
                };
            case "invoke":
                return new ActionListener(){
                    @Override
                    public void actionPerformed(){
                        scriptFile.invoke(listenerData[1], me);
                    }
                };
            case "close":
                return new ActionListener(){
                    @Override
                    public void actionPerformed(){
                        if (listenerData.length == 2){
                            scriptFile.invoke(listenerData[1], me);
                        }
                        terminate();
                    }
                };
        }
        return null;
    }
        
    /**
     * a method to lay out ui elements
     * @param elements the elements to lay out
     */
    protected abstract void layout(Element[] elements);
    
    /**
     * @see herbivore.run.Runner#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        render(renderer, elements);
    }
    
    /**
     * a method to render the elements of this menu
     * @param renderer the renderer to use
     * @param elements the elements to render
     */
    protected abstract void render(Renderer renderer, Element[] elements);
    
    /**
     * @see herbivore.run.Runner#parseInput(herbivore.misc.InputList)
     */
    @Override
    public void parseInput(InputList inputList){
        buttonParser.parseInput(inputList);
        if (inputTarget != null){
            for (Integer key : inputList.getReleases()){
                try {
                    scriptFile.invoke(inputTarget, Keyboard.getKeyName(key).toLowerCase(), this);
                }
                catch (IllegalArgumentException exception){
                    Logger.error(exception, "the runner is flagged as parsing input, but the script is missing: " + resource.getResourceName());
                }
            }
        }
    }
    
    /**
     * @see herbivore.run.Runner#resume()
     */
    @Override
    public void resume(){
        if (refresh != null && refresh.equals("true")){
            init();
        }
    }
    
    /**
     * @see herbivore.run.Runner#getClickables(java.util.List)
     */
    @Override
    public void getClickables(List<Clickable> clickables){
        clickables.addAll(Arrays.asList(elements));
    }
    
    private ButtonParser buttonParser;
    private Element[] elements;
    private ScriptFile scriptFile;
    private Resource resource;
    private String fontType, inputTarget, refresh;
}
