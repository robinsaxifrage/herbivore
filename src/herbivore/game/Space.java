package herbivore.game;
import herbivore.arch.Renderable;
import herbivore.arch.Updatable;
import herbivore.game.entity.Entity;
import herbivore.game.entity.EntityGround;
import herbivore.game.entity.EntityPlayer;
import herbivore.geom.Area;
import herbivore.geom.Bounds;
import herbivore.geom.Location;
import herbivore.misc.Logger;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.sound.Sound;
import herbivore.render.Font;
import herbivore.ui.UIUtils;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * a class representing a space in the worlds level. a space is the
 * primary container for entities inside a world and because of this
 * is responsible for updating and rendering them, as well as checking
 * their collisions
 * @author herbivore
 */
public class Space
    implements Updatable, Renderable {
    
    /**
     * creates a new space with the specified name and width in the specified world
     * @param name the name of the space
     * @param world the world containing this space
     * @param width the width of this space
     * @param hasSky a flag of whether or not the sky should be rendered before this spaces contents
     */
    public Space(String name, Level world, float width, boolean hasSky){
        this.name = name;
        this.level = world;
        this.width = width;
        sky = hasSky? Sky.get() : null;
        renderList = new ArrayList();
        updateList = new ArrayList();
        collisionList = new ArrayList();
        entities = new ArrayList();
        scrollStartLoc = new Location();
        ambiences = new ArrayList();
        titleFont = UIUtils.getFont("hudTitleFontSize", "basicFontColor");
    }
        
    /**
     * @see  herbivore.arch.Updatable#update(int)
     */
    @Override
    public void update(int delta){
        if (sky != null){
            sky.update(delta);
        }
        if (entities.contains(level.getPlayer())){
            if (titleLoc == null){
                titleLoc = new Point(Renderer.get().getWindowWidth()/2 - titleFont.getWidth(name)/2, -titleFont.getHeight(name));
            }
            if (titleLoc.y < 10){
                titleLoc.y += delta/10;
            }
            if (!titlePeaked){
                titleAlpha += 0.0005f*delta;
                if (titleAlpha >= 1f){
                    titlePeaked = true;
                }
            }
            else if (titlePeaked){
                if (titleHold < 1000){
                    titleHold += delta;
                }
                else if (titleAlpha > 0f){
                    titleAlpha -= 0.0005f*delta;
                }
            }
        }
        for (int index = 0; index < updateList.size(); index++){
            updateList.get(index).update(delta);
        }
        for (int index = 0; index < collisionList.size(); index++){
            Entity entity = collisionList.get(index);
            if (entity.getBehavior().getChecksCollisions()){
                entity.checkCollisions(collisionList, delta);
            }
        }
    }
        
    /**
     * @see herbivore.arch.Renderable#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        if (sky != null){
            sky.render(renderer);
        }
        renderer.setTranslation(level.getScroll());
        for (RenderPassEnum pass : RenderPassEnum.values()){
            for (Entity entity:renderList){
                if (entity.getRenderPass() == pass){
                    entity.render(renderer);
                }
            }
        }
        renderer.setTranslation(new Location());
        if (titleAlpha > 0){
            renderer.setAlpha(titleAlpha);
            renderer.drawString(name, titleFont, titleLoc.x, titleLoc.y);
            renderer.setAlpha(1f);
        }
        renderer.setTranslation(level.getScroll());
    }
    
    /**
     * adds an entity loaded from the specified prefab to the world at the specified coordinates
     * @see herbivore.game.EntityFactory#loadEntity(herbivore.res.Resource, java.lang.Object[])
     * @param prefabLocation the location of the prefab to be loaded as a customizable resource
     * @param x the x coordinate
     * @param y the y coordinate
     * @param args the varargs argument list to pass the prefab
     */
    public void add(String prefabLocation, float x, float y, Object... args){
        EntityFactory.setNextAddLocation(new Location(x, y));
        add(EntityFactory.loadEntity(Resource.getCustomizableResource("prefab/" + prefabLocation), this, args), x, y);
        EntityFactory.setNextAddLocation(null);
    }
    
    /**
     * add an entity to the world at the specified coordinates
     * @param entity the entity to add
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void add(Entity entity, float x, float y){
        entity.setSpace(this);
        entity.doInit();
        Location theLocation = entity.getBounds().getLocation();
        if (theLocation.x == 0 && theLocation.y == 0){
            entity.editBounds(x, y, -1, -1);
        }
        if (entity.getAddAtBottom()){
            entity.editBounds(-1, y - entity.getBounds().height, -1, -1);
        }
        entities.add(entity);
        level.getMasterEntityList().add(entity);
        EntityBehavior behavior = entity.getBehavior();
        if (behavior.getRenders()){
            renderList.add(entity);
        }
        if (behavior.getUpdates()){
            updateList.add(entity);
        }
        if (behavior.getCollides()){
            collisionList.add(entity);
        }
        if (entity instanceof EntityPlayer){
            playerJoined((EntityPlayer)entity);
        }
    }
    
    /**
     * @param entity the entity to remove
     */
    public void remove(Entity entity){
        entities.remove(entity);
        level.getMasterEntityList().remove(entity);
        EntityBehavior behavior = entity.getBehavior();
        if (behavior.getRenders()){
            renderList.remove(entity);
        }
        if (behavior.getUpdates()){
            updateList.remove(entity);
        }
        if (behavior.getCollides()){
            collisionList.remove(entity);
        }
        if (entity instanceof EntityPlayer){
            for (Sound sound : ambiences){
                sound.stop();
            }
        }
    }
    
    /**
     * called when the player is added to this space
     * @param player the player
     */
    private void playerJoined(EntityPlayer player){
        level.setPlayer(player);
        int windowWidth = Renderer.get().getWindowWidth();
        Location nextScroll = new Location(-(player.getBounds().x - windowWidth/2), 0);
        if (nextScroll.x > 0f){
            nextScroll.x = 0f;
        }
        if (nextScroll.x - windowWidth < -width){
            nextScroll.x = width + windowWidth;
        }
        level.setScroll(!joined? scrollStartLoc : nextScroll);
        titleLoc = null;
        titlePeaked = false;
        titleHold = 0;
        titleAlpha = 0f;
        joined = true;
        for (Sound sound : ambiences){
            sound.play(true);
        }
    }
    
    /**
     * returns all ground entities found in the specified bounds
     * @param bounds the bounds to search
     * @return 
     */
    public List<EntityGround> groundAt(Bounds bounds){
        List<EntityGround> found = new ArrayList();
        for (Entity check : collisionList){
            if (check instanceof EntityGround){
                EntityGround ground = (EntityGround)check;
                if (!ground.getHasSubpiece() && ground.getBounds().intersects(bounds)){
                    found.add(ground);
                }
            }
        }
        return found;
    }
    
    /**
     * calculates then returns size of the space, with the height being equal to that of the tallest entity
     * @return the size of the space
     */
    public Area getSize(){
        float height = 0;
        for (Entity check : renderList){
            if (check.getBounds().y > height){
                height = check.getBounds().y;
            }
        }
        return new Area(width, height);
    }
    
    /**
     * adds a looping ambience to the space with the specified internal volume
     * @param resource the .wav sound resource
     * @param volume the internal volume
     */
    public void addAmbience(Resource resource, float volume){
        Sound sound = resource.loadAsSound();
        sound.setInternalVolume(volume);
        ambiences.add(sound);
    }
    
    public void setScrollStartLoc(Location scrollStartLoc){this.scrollStartLoc = scrollStartLoc;}
    
    public List<Entity> getEntities(){return entities;}
    public List<Sound> getAmbiences(){return ambiences;}
    public Location getScrollStartLoc(){return scrollStartLoc;}
    public String getName(){return name;}
    public Level getLevel(){return level;}
    public float getWidth(){return width;}

    private List<Entity> renderList, updateList, collisionList, entities;
    private List<Sound> ambiences;
    private Location scrollStartLoc;
    private String name;
    private Level level;
    private Point titleLoc;
    private Font titleFont;
    private Sky sky;
    private float width;
    private boolean titlePeaked, joined;
    private float titleAlpha;
    private int titleHold;
}
