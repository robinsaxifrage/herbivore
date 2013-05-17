package herbivore.ui;
import herbivore.render.Font;
import herbivore.config.ConfigUtils;
import herbivore.geom.Bounds;
import herbivore.misc.Logger;
import herbivore.render.Java2DUtils;
import herbivore.res.Resource;
import herbivore.sound.Sound;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.newdawn.slick.Color;

/**
 * a static class containing utilities to help with
 * gui creation and management
 * @author herbivore
 */
public class UIUtils {
    
    /**
     * initializes the utility
     */
    private static void init(){
        if (initialized){
            return;
        }
        popupColor = getColor("popupColor");
        popupOpacity = ConfigUtils.get("layout", "popupOpacity", float.class);
        buttonFocusedSfx = Resource.getResource("res/sound/menu/buttonHover.wav").loadAsSound();
        buttonClickedSfx = Resource.getResource("res/sound/menu/buttonClick.wav").loadAsSound();
        buttonFocusedSfx.flagUnreleasable();
        buttonClickedSfx.flagUnreleasable();
        fonts = new HashMap();
        initialized = true;
    }
    
    /**
     * loads a slickutil color through reflect
     * @param name the name of the color to load
     * @return the loaded color
     */
    public static Color getColor(String name){
        name = ConfigUtils.get("layout", name, String.class);
        boolean darker = false;
        boolean brighter = false;
        if (name.contains("->")){
            String[] nameData = name.split("->");
            name = nameData[0];
            switch (nameData[1]) {
                case "darker":
                    darker = true;
                    break;
                case "brighter":
                    brighter = true;
                    break;
            }
        }
        Field[] fields = Color.class.getFields();
        Color color = null;
        for (Field field : fields){
            if (Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)){
                try {
                    color = (Color)field.get(null);
                }
                catch (IllegalAccessException exception){
                    Logger.error(exception, "failed to load color " + name + " through reflect");
                }
            }
        }
        if (darker){
            color = color.darker();
        }
        else if (brighter){
            color = color.brighter();
        }
        return color;
    }

    /**
     * loads a font with the specified values loaded from the configuration file
     * @param name the name of the font
     * @param type the type of the font
     * @return the loaded font
     */
    public static Font getFont(String name, String type){
        init();
        String tag = name + type;
        Font font = fonts.get(tag);
        if (font == null){
            int size = ConfigUtils.get("layout", name, int.class);
            Color color = getColor(type);
            font = new Font(size, color);
            fonts.put(tag, font);
        }
        return font;
    }
    
    /**
     * @return the button focusing sound effect
     */
    protected static Sound getButtonFocusedSound(){
        init();
        return buttonFocusedSfx;
    }
    
    /**
     * @return the button click sound effect
     */
    public static Sound getButtonClickedSound(){
        init();
        return buttonClickedSfx;
    }
    
    /**
     * generates the background for a pop-up menu
     * @param bounds the bounds of the pop-up menu
     * @return an image element representing the background
     */
    public static ImageElement getPopupBackground(Bounds bounds){
        init();
        return new ImageElement(Java2DUtils.convertToTexture(Java2DUtils.generateFill(popupColor, popupOpacity)), "background", (int)bounds.width, (int)bounds.height, (int)bounds.x, (int)bounds.y);
    }
    
    private static Map<String, Font> fonts;
    private static Sound buttonFocusedSfx, buttonClickedSfx;
    private static Color popupColor;
    private static boolean initialized;
    private static float popupOpacity;    
}
