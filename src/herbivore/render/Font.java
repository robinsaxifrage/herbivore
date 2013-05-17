package herbivore.render;
import herbivore.misc.Logger;
import herbivore.res.LoadUtils;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

/**
 * a wrapper class for slick unicode fonts
 * @author herbivore
 */
public class Font {
        
    /**
     * creates a new font with the specified color and size
     * @param size the size to use
     * @param color the color to use
     */
    public Font(int size, Color color){
        try {
            font = new UnicodeFont(LoadUtils.getFile("res/font.ttf").getPath(), size, false, false);
            font.addAsciiGlyphs();
            font.getEffects().add(new ColorEffect(new java.awt.Color(color.r, color.g, color.b)));
            font.loadGlyphs();
        }
        catch (SlickException exception){
            Logger.error(exception, "failed to create font");
        }
    }
    
    /**
     * @param text the text to check
     * @return the width of the specified text as rendered by this font
     */
    public int getWidth(String text){
        return font.getWidth(text);
    }
    
    /**
     * @param text the text to check
     * @return the height of the specified text as rendered by this font
     */
    public int getHeight(String text){
        return font.getHeight(text);
    }
    
    public UnicodeFont getFont(){return font;}
    
    private UnicodeFont font;
}
