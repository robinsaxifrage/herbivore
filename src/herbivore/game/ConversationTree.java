package herbivore.game;
import herbivore.game.entity.EntityActor;
import herbivore.misc.Logger;
import herbivore.res.Resource;
import herbivore.script.ScriptFile;
import java.util.ArrayList;
import java.util.List;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * a class for conversation navigation and loading. conversations are managed
 * through a list of "lines" loaded from xml. lines are pairs of inputs and outputs
 * @author herbivore
 */
public class ConversationTree {
    
    /**
     * creates a new conversation tree with the specified .conv archive 
     * @param resource the .conv archive
     * @param owner the host of the conversation
     * @param other the initiator of the conversation
     */
    public ConversationTree(Resource resource, EntityActor owner, EntityActor other){
        this.owner = owner;
        this.other = other;
        document = resource.getSubResource("conversation.xml").loadAsXml();
        scriptFile = resource.getSubResource("script.js").loadAsScriptFile();
    }
    
    /**
     * returns the xml element for the requited line
     * @param name the name of the desired line
     * @return the line, or null if it doesn't exist
     */
    private Element getLine(String name){
        Elements elements = document.getRootElement().getChildElements();
        for (int index = 0; index < elements.size(); index++){
            Element check = elements.get(index);
            if (check.getLocalName().equals("line") && check.getAttribute("name").getValue().equals(name)){
                return check;
            }
        }
        return null;
    }
    
    /**
     * processes the selected input and changes lines accordingly
     * @param input the selected input
     */
    protected void processInput(String input){
        Elements elements = currentElement.getFirstChildElement("input").getChildElements();
        for (int index = 0; index < elements.size(); index++){
            Element check = elements.get(index);
            if (check.getLocalName().equals("option") && check.getValue().equals(input)){
                String[] action = check.getAttribute("action").getValue().split(":");
                switch (action[0]){
                    case "script":
                        scriptFile.invoke(action[1], this, owner, other);
                        break;
                    case "end":
                        end();
                        switch (action[1]){
                            case "none":
                                break;
                        }
                        break;
                    case "goto":
                        goTo(action[1]);
                        break;
                }
            }
        }
    }
    
    /**
     * returns the opposite actor than specified
     * @param actor the know actor
     * @return the other actor in the conversation
     */
    public EntityActor getOther(EntityActor actor){
        if (actor.equals(other)){
            return other;
        }
        return other;
    }
    
    /**
     * ends the conversation
     */
    public void end(){
        ended = true;
    }
    
    /**
     * goes to the selected line
     * @param name the name of the line
     */
    public void goTo(String name){
        currentElement = getLine(name);
        ui.layoutContents();
    }
    
    /**
     * initiates the conversation
     */
    public void start(){
        goTo("start");
    }
    
    /**
     * @return the current lines output
     */
    public List<String> getOutput(){
        List<String> outputs = new ArrayList();
        Elements elements = currentElement.getFirstChildElement("output").getChildElements();
        for (int index = 0; index < elements.size(); index++){
            outputs.add(elements.get(index).getValue());
        }
        return outputs;
    }
     
    /**
     * @return the current lines inputs
     */
    public List<String> getInput(){
        List<String> inputs = new ArrayList();
        Elements elements = currentElement.getFirstChildElement("input").getChildElements();
        for (int index = 0; index < elements.size(); index++){
            Element element = elements.get(index);
            String perceptionThreshold = element.getAttributeValue("perception_threshold");
            if (perceptionThreshold == null || Integer.parseInt(perceptionThreshold) > other.getPerception()){
                inputs.add(element.getValue());
            }
        }
        return inputs;
    }
    
    protected void setUI(IngameUIConversation ui){this.ui = ui;}
   
    protected EntityActor getOwner(){return owner;}
    public boolean isEnded(){return ended;}

    private IngameUIConversation ui;
    private EntityActor owner, other;
    private ScriptFile scriptFile;
    private Document document;
    private Element currentElement;
    private boolean ended;
}
