package herbivore.game;
import herbivore.arch.Updatable;
import herbivore.game.entity.Entity;
import herbivore.game.entity.EntityActor;
import herbivore.res.Resource;
import herbivore.script.ScriptFile;
import org.ini4j.Ini;

/**
 * a class to hold a custom operation binding loaded from a .op archive
 * @see herbivore.game.Operation
 * @see herbivore.game.entity.EntityMisc#operationBinding
 * @see herbivore.game.entity.EntityItemMisc#operationBinding
 * @author herbivore
 */
public class OperationBinding
    implements Updatable, Collidable {

    /**
     * creates a new operation binding from the specified archive
     * @param resource the .op archive to use
     */
    public OperationBinding(Resource resource){
        data = resource.getSubResource("data.ini").loadAsIni();
        scriptFile = resource.getSubResource("script.js").loadAsScriptFile();
        name = data.get("operation", "name", String.class);
        controlDescription = data.get("operation", "controlDescription", String.class); 
        cooldown = data.get("operation", "cooldown", int.class);
    }
        
    /**
     * @see herbivore.arch.Updatable#update(int)
     */
    @Override
    public void update(int delta){
        if (counter > 0){
            counter -= delta;
        }
    }
    
    /**
     * updates this binding when it belongs to an item and
     * is equipped by the items holder
     * @param actor the owner of this operations parent item
     */
    public void updateEquiped(EntityActor actor){
        actor.addOperation(get(owner, actor));
    }
    
    /**
     * @see herbivore.game.Collidable#collide(herbivore.game.entity.Entity)
     */
    @Override
    public void collide(Entity entity){
        if (entity instanceof EntityActor){
            EntityActor actor = (EntityActor)entity;
            actor.addOperation(get(owner, actor));
        }
    }

    /**
     * creates a new operation as determined by this binding
     * @param owner the owner of the operation
     * @param actor the actor that is receiving this operation
     * @return the created operation
     */
    protected Operation get(final Entity owner, final EntityActor actor){
        return new Operation(name, controlDescription, actor){
            @Override
            public void perform(){
                counter = cooldown;
                scriptFile.invoke("perform", owner, actor);
            }
            @Override
            public boolean enabled(){
                if (times > 0){
                    times--;
                    return flag;
                }
                times = 20;
                flag = counter <= 0 && (Boolean)scriptFile.invoke("enabled", owner, actor);
                return flag;
            }
            private boolean flag;
            private int times;
        };
    }
    
    public void setOwner(Entity owner){this.owner = owner;}
    
    private ScriptFile scriptFile;
    private String name, controlDescription;
    private Entity owner;
    private Ini data;
    private int cooldown, counter;
}
