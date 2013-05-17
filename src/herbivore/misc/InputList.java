package herbivore.misc;
import herbivore.config.ConfigUtils;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * a class representing a list of input commands, such as key presses and releases
 * @author herbivore
 */
public class InputList {

    /**
     * creates a new, empty, input list 
     */
    public InputList(){
        presses = new ArrayList();
        releases = new ArrayList();
    }
    
    /**
     * empties this input list
     */
    public void clear(){
        presses.clear();
        releases.clear();
    }
    
    /**
     * populates this input list from the lwjgl keyboard and mouse
     * @param windowHeight the height of the window, for reversing mouse y coordinates
     */
    public void populate(int windowHeight){
        while (Keyboard.next()){
            boolean keyState = Keyboard.getEventKeyState();
            int keyCode = Keyboard.getEventKey();
            if (keyState){
                presses.add(keyCode);
            }
            else {
                releases.add(keyCode);
            }
        }
        mousePress = false;
        mouseRelease = false;
        while (Mouse.next()){
            boolean clickState = Mouse.getEventButtonState();
            if (Mouse.getEventButton() == 0){
                if (clickState){
                    mousePress = true;
                }
                else {
                    mouseRelease = true;
                }
            }
        }
        mouseLocation = new Point(Mouse.getX(), windowHeight - Mouse.getY());
    }
    
    /**
     * @param control the name of the control in the configuration file
     * @return whether this input list contains a press of the specified 
     * controls key in the configuration file
     */
    public boolean containsPress(String control){
        return listContains(presses, control);
    }
    
    /**
     * @param control the name of the control in the configuration file
     * @return whether this input list contains a release of the specified 
     * controls key in the configuration file
     */
    public boolean containsRelease(String control){
        return listContains(releases, control);
    }
    
    /**
     * gets a controls key from the configuration file and checks for a match
     * @param list the list of keys to check
     * @param control the name of the control in the configuration file
     * @return whether or not the specified list contains the control
     */
    private boolean listContains(List<Integer> list, String control){
        control = ConfigUtils.get("controls", control, String.class).toUpperCase();
        if (control.contains("!")){
            String[] split = control.split("!");
            int prim = Keyboard.getKeyIndex(split[0]);
            int held = Keyboard.getKeyIndex(split[1]);
            return !Keyboard.isKeyDown(held) && list.contains(prim);
        }
        if (control.contains("&")){
            String[] split = control.split("&");
            int prim = Keyboard.getKeyIndex(split[0]);
            int held = Keyboard.getKeyIndex(split[1]);
            return Keyboard.isKeyDown(held) && list.contains(prim);
        }
        return list.contains(Keyboard.getKeyIndex(control));
    }
    
    public List<Integer> getPresses(){return presses;}
    public List<Integer> getReleases(){return releases;}
    public Point getMouseLocation(){return mouseLocation;}
    public boolean containsMousePress(){return mousePress;}
    public boolean containsMouseRelease(){return mouseRelease;}
    
    private List<Integer> presses, releases;
    private Point mouseLocation;
    private boolean mousePress, mouseRelease;
}
