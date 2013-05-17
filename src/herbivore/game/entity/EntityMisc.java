package herbivore.game.entity;
import herbivore.game.OperationBinding;
import herbivore.res.Resource;

/**
 * an entity that can have an operation binding added to it
 * @author herbivore
 */
public class EntityMisc
    extends Entity {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityMisc(Resource baseResource){
        super(baseResource);
        getBehavior().addRenders(true).addUpdates(true).addCollides(true).addChecksCollisions(true);
    }
    
    /**
     * @see herbivore.game.entity.Entity#update(int)
     */
    @Override
    public void update(int delta){
        super.update(delta);
        if (operationBinding != null){
            operationBinding.update(delta);
        }
    }
    
    /**
     * @see herbivore.game.entity.Entity#collide(herbivore.game.entity.Entity)
     */
    @Override
    public void collide(Entity entity){
        if (operationBinding != null){
            operationBinding.collide(entity);
        }
    }
    
    /**
     * adds an operation binding to this entity
     * @param operationBinding the operation binding to add
     */
    public void addOpBinding(OperationBinding operationBinding){
        this.operationBinding = operationBinding;
        operationBinding.setOwner(this);
    }

    private OperationBinding operationBinding;
}
