package herbivore.game;
import herbivore.arch.Renderable;
import herbivore.arch.Updatable;
import herbivore.config.BuildInfo;
import herbivore.config.ConfigUtils;
import herbivore.geom.Bounds;
import herbivore.render.Java2DUtils;
import herbivore.render.Renderer;
import herbivore.render.SpriteSheet;
import herbivore.res.Resource;
import java.util.Random;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/**
 * a class responsible for updating and rendering the sky
 * @author herbivore
 */
public class Sky
    implements Updatable, Renderable {
    
    /**
     * @return the singleton instance of the sky
     */
    public static Sky get(){
        if (instance == null){
            instance = new Sky();
        }
        return instance;
    }
    
    private static Sky instance;
    
    /**
     * creates a new sky
     */
    private Sky(){
        Renderer renderer = Renderer.get();
        renderSize = new Bounds(renderer.getWindowWidth(), renderer.getWindowHeight());
        float myMod = BuildInfo.getTextureResizeRatio() - 1f;
        Random random = new Random();
        SpriteSheet mesh = new SpriteSheet(Resource.getCustomizableResource("art/stars.png"));
        mesh.setDefaultSize(2, 2);
        starTextures = new Image[]{mesh.chop(0, 0), mesh.chop(2, 0), mesh.chop(4, 0), mesh.chop(6, 0)};
        starBounds = new Bounds[20];
        starTextureBindings = new int[starBounds.length];
        for (int index = 0; index < starBounds.length; index++){
            starBounds[index] = new Bounds(random.nextInt((int)renderSize.width), random.nextInt((int)renderSize.height), 2*myMod, 2*myMod);
            starTextureBindings[index] = random.nextInt(starTextures.length);
        }
        night = Java2DUtils.convertToTexture(Java2DUtils.generateFill(Color.black, 1f));
        day = Java2DUtils.convertToTexture(Java2DUtils.generateFill(new Color(170, 230, 250), 1f));
        timeScale = ConfigUtils.get("gameplay", "timeScale", int.class);
        time = 86400000/2;
    }

    /**
     * @see herbivore.arch.Updatable#update(int)
     */
    @Override
    public void update(int delta){
        double theDelta = delta*timeScale;
        for (Bounds star:starBounds){
            star.x += 0.0003f*theDelta/5;
            star.y -= 0.0001f*theDelta/5;
            if (star.x > renderSize.width + star.width){
                star.x = -star.width;
            }
            if (star.y < -star.height){
                star.y = renderSize.height;
            }
        }
        if (dayRising){
            if (time < 86400000){
                 time += theDelta;
            }
            else {
                dayRising = false;
            }
        }
        else {
            if (time > 0){
                time -= theDelta;
            }
            else {
                dayRising = true;
            }
        }
    }
    
    /**
     * @see herbivore.arch.Renderable#render(herbivore.render.Renderer
     */
    @Override
    public void render(Renderer renderer){
        renderer.drawImage(night, renderSize);
        renderer.setAlpha(time/86400000);
        renderer.drawImage(day, renderSize);
        renderer.setAlpha(1f - (time/86400000));
        for (int index = 0; index < starBounds.length; index++){
            renderer.drawImage(starTextures[starTextureBindings[index]], starBounds[index]);
        }
        renderer.setAlpha(1f);
    }
    
    private Bounds[] starBounds;
    private Image[] starTextures;
    private Bounds renderSize;
    private Image night, day;
    private boolean dayRising;
    private float time, timeScale;
    private int[] starTextureBindings;
}
