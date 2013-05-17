package herbivore.game.entity;
import herbivore.game.OperationBinding;
import herbivore.res.Resource;

/**
 * an item entity that can be assigned operation bindings, but is
 * other than that a direct implementation of an item entity
 * @author herbivore
 */
public class EntityItemMisc
    extends EntityItem {

    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityItemMisc(Resource baseResource){
        super(baseResource);
    }
        
    /**
     * @see herbivore.game.entity.EntityItem#updateEquipped(int)
     */
    @Override
    protected void updateEquipped(int delta){
        super.updateEquipped(delta);
        if (operationBinding != null){
            operationBinding.update(delta);
            operationBinding.updateEquiped(getOwner());
        }
    }
    
    /**
     * adds an operation binding to this entity for any actor holding it
     * @param operationBinding the operation binding to add
     */
    public void addOpBinding(OperationBinding operationBinding){
        this.operationBinding = operationBinding;
        operationBinding.setOwner(this);
    }
    
    private OperationBinding operationBinding;
}
