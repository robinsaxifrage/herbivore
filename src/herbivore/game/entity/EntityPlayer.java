package herbivore.game.entity;
import herbivore.Herbivore;
import herbivore.res.Resource;
import herbivore.run.PopupRunner;

/**
 * an actor entity that is controlled by the user.
 * adding multiple player entities to a world will
 * cause erratic behavior 
 * @author herbivore
 */
public class EntityPlayer
    extends EntityActor {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityPlayer(Resource baseResource){
        super(baseResource);
    }
    
    /**
     * @see herbivore.game.entity.Entity#destroy()
     */
    @Override
    protected void destroy(){
        super.destroy();
        getSpace().getLevel().getGame().closeHuds();
        Herbivore.get().pushRunner(new PopupRunner(Resource.getResource("res/menu/gameLost.menu")));
    }
}
