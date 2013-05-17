package herbivore.game.entity;
import herbivore.game.AI;
import herbivore.game.ConversationTree;
import herbivore.game.IngameUIConversation;
import herbivore.game.InteractionTypeEnum;
import herbivore.game.Operation;
import herbivore.res.Resource;

/**
 * an actor entity implemented to be used as a non player character
 * @author herbivore
 */
public class EntityNPC
    extends EntityActor {
    
    /**
     * @see herbivore.game.entity.Entity#Entity(herbivore.res.Resource)
     */
    public EntityNPC(Resource baseResource){
        super(baseResource);
        getBehavior().addChecksCollisions(true);
    }
    
    /**
     * @see herbivore.game.entity.Entity#init()
     */
    @Override
    protected void init(){
        super.init();
        interactionType = InteractionTypeEnum.valueOf(getData().get("npc", "interactionType"));
        if (interactionType != InteractionTypeEnum.always){
            interactionValue = getData().get("npc", "interactionValue", String.class);
        }
        if (interactionType != InteractionTypeEnum.none){
            conversation = getBaseResource().getSubResource("conversation.conv");
        }
        ai = new AI(this, getBaseResource().getSubResource("ai.ai"));
    }
    
    /**
     * @see herbivore.game.entity.Entity#update(int)
     */
    @Override
    public void update(int delta){
        if (getSpace().getLevel().getGame().getAiProcessing() && ai != null){
            ai.update(delta);
        }
        if (cooldown > 0){
            cooldown -= delta;
        }
        super.update(delta);
    }

    /**
     * @see herbivore.game.entity.Entity#collide(herbivore.game.entity.Entity)
     */
    @Override
    public void collide(Entity entity){
        if (entity instanceof EntityActor){
            final EntityActor actor = (EntityActor)entity;
            final EntityActor me = this;
            ((EntityActor)entity).addOperation(new Operation("talk to", "talkTo", this){
                @Override
                public void perform(){
                    switch (interactionType){
                        case once:
                            if (onceHappened){
                                interactionType = InteractionTypeEnum.none;
                                perform();
                                break;
                            }
                            onceHappened = true;
                        case always:
                            IngameUIConversation theConversation = new IngameUIConversation(new ConversationTree(conversation, me, actor));
                            me.setCurrentConversation(theConversation);
                            actor.setCurrentConversation(theConversation);
                            me.getSpace().getLevel().getGame().setCurrentUI(theConversation);
                            break;
                        case none:
                            notifyFrom(interactionValue);
                            cooldown = 1000;
                            break;
                        
                    }
                }
                @Override
                public boolean enabled(){
                    return cooldown <= 0 && me.getCurrentConversation() == null && actor.getCurrentConversation() == null;
                }
            });
        }
    }
    
    private InteractionTypeEnum interactionType;
    private Resource conversation;
    private String interactionValue;
    private AI ai;
    private boolean onceHappened;
    private int cooldown;
}
