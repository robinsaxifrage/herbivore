package herbivore.game;
import herbivore.Herbivore;
import herbivore.arch.Renderable;
import herbivore.arch.Updatable;
import herbivore.game.entity.Entity;
import herbivore.game.entity.EntityActor;
import herbivore.geom.Location;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.run.PopupRunner;
import herbivore.run.RunnerGame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * a class representing the level, generated from a world generator, which contains the play spaces
 * @author herbivore
 */
public class Level
    implements Iterable<Space>, Updatable, Renderable {
    
    /**
     * creates a new level with no spaces or entities
     */
    public Level(){
        spaces = new ArrayList();
        masterEntityList = new ArrayList();
    }
    
    /**
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator iterator(){
        return new Iterator(){
            @Override
            public boolean hasNext(){
                return index < spaces.size() - 1;
            }
            @Override
            public Space next(){
                index++;
                return spaces.get(index);
            }
            @Override
            public void remove(){}
            private int index;
        };
    }
    
    /**
     * @see herbivore.arch.Updatable#update(int)
     */
    @Override
    public void update(int delta){
        if (won && !wonRegistered){
            Herbivore.get().pushRunner(new PopupRunner(Resource.getResource("res/menu/gameWon.menu")));
            wonRegistered = true;
        }
        for (Space space : spaces){
            space.update(delta);
        }
    }
        
    /**
     * @see herbivore.arch.Renderable#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        player.getSpace().render(renderer);
    }
    
    /**
     * adds a space to the world
     * @param space the space to add
     */
    public void addSpace(Space space){
        spaces.add(space);
    }
    
    /**
     * finds the space with specified name
     * @param name the name to search for
     * @return the space, or null if no space has that name
     */
    public Space getSpace(String name){
        for (Space space:spaces){
            if (space.getName().equals(name)){
                return space;
            }
        }
        return null;
    }
    
    public void setPlayer(EntityActor player){this.player = player;}
    public void setGame(RunnerGame game){this.game = game;}
    public void setScroll(Location scroll){this.scroll = scroll;}
    public void setWon(boolean won){this.won = won;}
    
    public List<Entity> getMasterEntityList(){return masterEntityList;}
    public EntityActor getPlayer(){return player;}
    public RunnerGame getGame(){return game;}
    public Location getScroll(){return scroll;}
    
    private List<Entity> masterEntityList;
    private List<Space> spaces;
    private EntityActor player;
    private RunnerGame game;
    private Location scroll;
    private boolean won, wonRegistered;
}