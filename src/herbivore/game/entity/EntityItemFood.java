package herbivore.game.entity;
import herbivore.game.FoodConsistancyEnum;
import herbivore.game.Operation;
import herbivore.res.Resource;
import herbivore.sound.Sound;

/**
 * an item entity that can be consumed by actors
 * @author herbivore
 */
public class EntityItemFood 
    extends EntityItem {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityItemFood(Resource baseResource){
        super(baseResource);
    }
    
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
        super.init();
        damage = getData().get("food", "damage", int.class);
        consistancy = FoodConsistancyEnum.valueOf(getData().get("food", "consistancy", String.class));
        consumeSfx = getResourceOrFallback("consumed.wav", Resource.getCustomizableResource("sound/" + consistancy.name() + "Food.wav")).loadAsSound();
    }

    /**
     * @see herbivore.game.entity.EntityItem#updateEquipped(int) 
     */
    @Override
    public void updateEquipped(int delta){
        super.updateEquipped(delta);
        final EntityItem food = this;
        final EntityActor owner = getOwner();
        owner.addOperation(new Operation(consistancy == FoodConsistancyEnum.liquid? "drink" : "eat", "primaryAction", this){
            @Override
            public void perform(){
                consumeSfx.play(owner);
                exicuteScript("consumed");
                owner.notifyFrom((damage > 0? "" : "+") + -damage);
                owner.damage(damage, owner);
                getOwner().getInventory().remove(food);
            }
        });
    }
 
    private FoodConsistancyEnum consistancy;
    private Sound consumeSfx;
    private int damage;
}
