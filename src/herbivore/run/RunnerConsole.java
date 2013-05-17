package herbivore.run;
import herbivore.arch.Clickable;
import herbivore.config.ConfigUtils;
import herbivore.game.Level;
import herbivore.geom.Bounds;
import herbivore.geom.Location;
import herbivore.misc.InputList;
import herbivore.render.Font;
import herbivore.render.Java2DUtils;
import herbivore.render.Renderer;
import herbivore.script.ScriptUtils;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.input.Keyboard.*;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/**
 * a runner that implements the logging and scripting functionality
 * of this program into a console
 * @author herbivore
 */
public class RunnerConsole
    extends Runner {
    
    /**
     * @see herbivore.run.Runner#init()
     */
    @Override
    protected void init(){
        fontSize = ConfigUtils.get("console", "fontSize", int.class);
        suspendParent = ConfigUtils.get("console", "suspendParent", boolean.class);
        opacity = ConfigUtils.get("console", "opacity", float.class);
        black = Java2DUtils.convertToTexture(Java2DUtils.generateFill(Color.black, opacity));
        whiteFont = new Font(fontSize, Color.white);
        yellowFont = new Font(fontSize, Color.yellow);
        redFont = new Font(fontSize, Color.red);
        grayFont = new Font(fontSize, Color.lightGray);
        selectedClickables = new ArrayList();
        clickableFeed = new ArrayList();
        current = new ArrayList();
        current.add("");
    }
    
    /**
     * @see herbivore.run.Runner#update(int)
     */
    @Override
    public void update(int delta){
        clickableFeed.clear();
        getParent().getClickables(clickableFeed);
        if (!suspendParent){
            getParent().update(delta);
        }
        if (blink > -500){
            blink -= delta;
        }
        else {
            blink = 500;
        }
    }
    
    /**
     * @see herbivore.run.Runner#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        getParent().render(renderer);
        renderer.setTranslation(new Location(0f, renderer.getWindowHeight()/4f));
        int windowWidth = renderer.getWindowWidth();
        int windowHeight = renderer.getWindowHeight();
        renderer.drawImage(black, new Bounds(windowWidth, windowHeight));
        List<String> console = Static.console;
        int height = whiteFont.getHeight("x");
        int y = 0;
        for (String string : current){
            y += height;
            renderer.drawString(string + ((string.equals(current.get(current.size() - 1)) && blink > 0)? "_" : ""), whiteFont, 10, y);
        }
        for (int index = console.size() - 1; index >= 0; index--){
            String string = console.get(index);
            Font font = whiteFont;
            if (string.startsWith("<yellow>")){
                font = yellowFont;
                string = string.substring(8);
            }
            else if (string.startsWith("<red>")){
                font = redFont;
                string = string.substring(5);
            }
            else if (string.startsWith("<gray>")){
                font = grayFont;
                string = string.substring(6);
            }
            y += height;
            renderer.drawString(string, font, 10, y);
        }
        if (Mouse.isButtonDown(1)){
            for (Clickable check : clickableFeed){
                Rectangle rect = check.getClickBounds();
                renderer.drawLine(rect.x, rect.y, rect.x + rect.width, rect.y, 2, Color.yellow);
                renderer.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height, 2, Color.yellow);
                renderer.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height, 2, Color.yellow);
                renderer.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height, 2, Color.yellow);
            }
        }
        String text = "";
        if (selectedClickables.size() > 0){
            text += "[";
            for (Clickable clickable : selectedClickables){
                text += clickable.getClickedName() + ", ";
            }
            text = text.substring(0, text.length() - 2) + "]";
        }
        else {
            text = "empty";
        }
        renderer.drawString(text, whiteFont, windowWidth/2 - yellowFont.getWidth(text)/2, 10);
        renderer.setTranslation(new Location());
    }
    
    /**
     * @see herbivore.run.Runner#parseInput(herbivore.misc.InputList)
     */
    @Override
    public void parseInput(InputList inputList){
        List<String> console = Static.console;
        if (inputList.containsMouseRelease()){
            selectedClickables.clear();
            for (Clickable check : clickableFeed){
                if (check.getClickBounds().contains(inputList.getMouseLocation())){
                    selectedClickables.add(check);
                }
            }
        }
        for (int key : inputList.getPresses()){
            if (key == Keyboard.KEY_BACK){
                if (current.get(current.size() - 1).length() > 0){
                    current.set(current.size() - 1, current.get(current.size() - 1).substring(0, current.get(current.size() - 1).length() - 1));
                }
                else {
                    if (current.size() > 1){
                        current.remove(current.size() - 1);
                    }
                }
            }
            else if (key == Keyboard.KEY_RETURN){
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
                    current.add("");
                }
                else {
                    StringBuilder currentAppender = new StringBuilder();
                    for (String line : current){
                        currentAppender.append(line);
                    }
                    String currentStr = currentAppender.toString();
                    if (!parseInput_checkForCommand(currentStr)){
                        Runner parent = getParent();
                        Clickable[] clickableArray = selectedClickables.toArray(new Clickable[selectedClickables.size()]);
                        if (parent instanceof RunnerGame){
                            Level world = ((RunnerGame)parent).getWorld();
                            ScriptUtils.runConsoleScript("world, player, current", currentStr, world, world.getPlayer(), clickableArray);
                        }
                        else {
                            ScriptUtils.runConsoleScript("current", currentStr, (Object[])clickableArray);
                        }
                    }
                    console.add("<gray>" + currentAppender);
                    current.clear();
                    current.add("");
                }
            }
            else {
                current.set(current.size() - 1, current.get(current.size() - 1).concat(KeyParser.parse(key, Keyboard.isKeyDown(KEY_LSHIFT) || Keyboard.isKeyDown(KEY_RSHIFT))));
            }
        }
    }
    
    /**
     * checks if a command is contained within the specified string
     * @param check the string to check
     * @return whether or not a command was found
     */
    private boolean parseInput_checkForCommand(String check){
        switch (check){
            case "help":
                ScriptUtils.printHelp();
                break;
            case "clear":
                Static.clear();
                break;
            case "fq":
                System.exit(0);
                break;
            default:
                return false;
        }
        return true;
    }
        
    private List<Clickable> clickableFeed, selectedClickables;
    private List<String> current;
    private Image black;
    private Font whiteFont, yellowFont, redFont, grayFont;
    private boolean suspendParent;
    private float opacity;
    private int fontSize, blink;
    
    /**
     * a static class to manage the console entry list
     */
    public static class Static {
        
        /**
         * clears the console
         */
        public static void clear(){
            console.clear();
        }
        
        /**
         * appends a string to the console
         * @param string the string to append
         */
        public static void append(String string){
            if (console == null){
                console = new ArrayList();
            }
            console.add(string);
        }
                
        private static List<String> console;
    }
    
    /**
     * a static class to parse key input
     */
    private static class KeyParser {
        
        /**
         * initializes the key parser
         */
        private static void init(){
            if (initialized){
                return;
            }           
            alphabetChars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
            alphabetKeys = new int[]{KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I, KEY_J, KEY_K, KEY_L, KEY_M, KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R, KEY_S, KEY_T, KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z};
            miscChars = new String[]{",", " ", "0", ";", ".", "'", "\\", "[", "]", "-", "=", "/", "      "};
            miscKeys = new int[]{KEY_COMMA, KEY_SPACE, KEY_0, KEY_SEMICOLON, KEY_PERIOD, KEY_APOSTROPHE, KEY_BACKSLASH, KEY_LBRACKET, KEY_RBRACKET, KEY_MINUS, KEY_EQUALS, KEY_SLASH, KEY_TAB};
            keyConverts = new String[]{"7->&", "9->(", "0->)", ";->:", "'->\"", "\\->|", "[->{", "]->}", "-->_", "=->+", ",-><", ".->>", "1->!", "/->?"};
            initialized = true;
        }
        
        /**
         * @param key the key that was pressed
         * @param caps whether or not caps were on when the key was pressed
         * @return a string representation of the pressed key
         */
        public static String parse(int key, boolean caps){
            init();
            String it = "";
            if (key >= KEY_1 && key <= KEY_9){
                it = (key - 1) + "";
            }
            if (it.equals("")){
                for (int index = 0; index < miscKeys.length; index++){
                    if (key == miscKeys[index]){
                        it = miscChars[index];
                        break;
                    }
                }
            }
            boolean alphabet = false;
            if (it.equals("")){
                for (int index = 0; index < alphabetKeys.length; index++){
                    if (key == alphabetKeys[index]){
                        it = alphabetChars[index];
                        alphabet = true;
                        break;
                    }
                }
            }
            if (caps){
                if (alphabet){
                    it = it.toUpperCase();
                }
                else {
                    for (String string:keyConverts){
                        String[] two = string.split("->");
                        if (two[0].equals(it)){
                            it = two[1];
                        }

                    }
                }
            }
            return it;
        }
        
        private static String[] alphabetChars, miscChars, keyConverts;
        private static boolean initialized;
        private static int[] alphabetKeys, miscKeys;
    }
}
