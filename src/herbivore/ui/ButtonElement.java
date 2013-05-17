package herbivore.ui;
import herbivore.render.Font;
import herbivore.render.Renderer;
import java.awt.Point;

/**
 * a ui element representing a clickable button
 * @author herbivore
 */
public class ButtonElement
    extends TextElement {
    
    /**
     * creates a new button element with the specified text, font name and action listener
     * @param text the text to use
     * @param fontName the font name to load the font from the configuration file with
     * @param listener the action listener to send the click event to
     */
    public ButtonElement(String text, String fontName, ActionListener listener){
        super(text, fontName);
        focusedFont = UIUtils.getFont(fontName, "focusedFontColor");
        toggledFont = UIUtils.getFont(fontName, "toggledFontColor");
        this.listener = listener;
    }
    
    /**
     * @see herbivore.ui.UIElement#updateFocusState(java.awt.Point, boolean)
     */
    @Override
    public void updateFocusState(Point mouseLocation, boolean mouseDown){
        boolean newHovered = getBounds().contains(mouseLocation);
        if (newHovered && !focused){
            UIUtils.getButtonFocusedSound().play();
        }
        focused = newHovered;
        if (focused){
            this.mouseDown = mouseDown;
        }
        else {
            this.mouseDown = false;
        }
    }
    
    /**
     * @see herbivore.ui.UIElement#render(herbivore.render.Renderer)
     * @param renderer 
     */
    @Override
    public void render(Renderer renderer){
        if (mouseDown){
            renderer.drawString(getText(), toggledFont, getBounds().x, getBounds().y);
        }
        else if (focused){
            renderer.drawString(getText(), focusedFont, getBounds().x, getBounds().y);
        }
        else {
            super.render(renderer);
        }
    }
    
    /**
     * fires the click event on the listener
     */
    public void click(){
        UIUtils.getButtonClickedSound().play();
        listener.actionPerformed();
    }
    
    @Override
    public boolean isFocused(){return focused;}

    private ActionListener listener;
    private Font focusedFont, toggledFont;
    private boolean focused, mouseDown;
}
