package herbivore.game.entity;
import herbivore.res.Resource;

/**
 * an animation type of entity, simple iterates through its frames and then
 * either loops or self destroys
 * @author herbivore
 */
public class EntityAnimation
    extends Entity {

    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityAnimation(Resource baseResource){
        super(baseResource);
        getBehavior().addRenders(true).addUpdates(true);
    }
    
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
        super.init();
        timePerFrame = getData().get("animation", "timePerFrame", int.class);
        looping = getData().get("animation", "looping", boolean.class);
    }
    
    /**
     * @see herbivore.game.entity.Entity#update(int)
     */
    @Override
    public void update(int delta){
        if (counter < timePerFrame){
            counter += delta;
        }
        else {
            counter = 0;
            if (getSpriteIndex() < getSprites().length - 1){
                setSpriteIndex(getSpriteIndex() + 1);
            }
            else {
                if (looping){
                    setSpriteIndex(0);
                }
                else {
                    doDestroy();
                }
            }
        }
    }
    
    private boolean looping;
    private int timePerFrame, counter;
}
