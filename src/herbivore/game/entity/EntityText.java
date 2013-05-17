package herbivore.game.entity;
import herbivore.game.RenderPassEnum;
import herbivore.geom.Location;
import herbivore.render.Font;
import herbivore.render.Renderer;
import herbivore.ui.UIUtils;

/**
 * an entity that renders as a string
 * @author herbivore
 */
public class EntityText
    extends Entity {
    
    /**
     * creates a new text entity with the specified
     * text and lifespan
     * @param text the text to use
     * @param life the life of this entity, in milliseconds
     */
    public EntityText(String text, int life){
        this.text = text;
        this.life = life;
        getBehavior().addRenders(true).addUpdates(true);
        font = UIUtils.getFont("entityTextFontSize", "entityTextFontColor");
        editBounds(-1, -1, font.getWidth(text), font.getHeight(text));
        setRenderPass(RenderPassEnum.foreground_pass);
    }
    
    /**
     * @see herbivore.game.entity.Entity#update(int)
     */
    @Override
    public void update(int delta){
        getBounds().y -= 0.03f*delta;
        life -= delta;
        if (life < 0){
            destroy();
        }
        else if (life < 1000){
            alpha -= 0.001f*delta;
        }
        else if (alpha < 1f){
            alpha += 0.003f*delta;
        }
    }
    
    /**
     * @see herbivore.game.entity.Entity#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        renderer.setAlpha(alpha);
        Location location = getBounds().getLocation();
        renderer.drawString(text, font, location.x, location.y);
        renderer.setAlpha(1f);
    }

    private String text;
    private Font font;
    private float alpha;
    private int life;
}
