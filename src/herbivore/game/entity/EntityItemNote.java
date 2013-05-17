package herbivore.game.entity;
import herbivore.game.IngameUINote;
import herbivore.game.Operation;
import herbivore.res.Resource;

/**
 * an item entity representing a readable note
 * @author herbivore
 */
public class EntityItemNote
    extends EntityItem {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityItemNote(Resource baseResource){
        super(baseResource);
    }
    
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
       super.init();
       note = getBaseResource().getSubResource("note.note");
    }
    
    /**
     * @see herbivore.game.entity.EntityItem#updateEquipped(int)
     */
    @Override
    protected void updateEquipped(int delta){
        super.updateEquipped(delta);
        final EntityActor owner = getOwner();
        owner.addOperation(new Operation("read", "primaryAction", this){
            @Override
            public void perform(){
                popup = new IngameUINote(note);
                getSpace().getLevel().getGame().setCurrentUI(popup);
            }
            @Override
            public boolean enabled(){
                return popup == null || popup.isEnded();
            }
        });
    }

    private IngameUINote popup;
    private Resource note;
}
