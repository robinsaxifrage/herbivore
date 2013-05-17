package herbivore.ui;
import herbivore.render.Font;
import herbivore.render.Renderer;
import java.awt.Rectangle;

/**
 * a ui element representing a piece of static text
 * @author herbivore
 */
public class TextElement
    extends Element {
    
    /**
     * creates a new text element with the specified text and font
     * @param text the text to use
     * @param fontName the name of the font to load from the configuration file
     */
    public TextElement(String text, String fontName){
        this(text, fontName, "basicFontColor");
    }
    
    /**
     * creates a new text element with the specified text and font
     * @param text the text to use
     * @param fontName the name of the font to load from the configuration file
     * @param fontType the type of the font to load from the configuration file
     */
    public TextElement(String text, String fontName, String fontType){
        font = UIUtils.getFont(fontName, fontType);
        setText(text);
        setBounds(new Rectangle(font.getWidth(text), font.getHeight("x")));
    }
    
    /**
     * @see herbivore.ui.Element#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        renderer.drawString(getText(), font, getBounds().x, getBounds().y);
    }

    private Font font;
}
