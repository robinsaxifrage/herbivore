package herbivore.game.entity;
import herbivore.arch.Clickable;
import herbivore.arch.Renderable;
import herbivore.arch.Updatable;
import herbivore.config.BuildInfo;
import herbivore.game.Collidable;
import herbivore.game.EntityBehavior;
import herbivore.game.RenderPassEnum;
import herbivore.game.Space;
import herbivore.geom.Area;
import herbivore.geom.Bounds;
import herbivore.geom.Location;
import herbivore.misc.Logger;
import herbivore.render.Renderer;
import herbivore.render.SpriteSheet;
import herbivore.res.Resource;
import herbivore.script.ScriptFile;
import java.awt.Rectangle;
import java.util.List;
import org.ini4j.Ini;
import org.newdawn.slick.Image;

/**
 * the most basic class for entities contained inside a space. contains all
 * the nessesary framework to create an implementable entity for the world
 * @author herbivore
 */
public abstract class Entity
    implements Updatable, Renderable, Collidable, Clickable {
    
    /**
     * creates a new entity without a base resource
     */
    protected Entity(){
        this(null);
    }
    
    /**
     * creates a new entity with the specified base resource.
     * this method is protected to ensure all entities have access to 
     * protected methods in the entity package
     * @param baseResource the base resource to use
     */
    protected Entity(Resource baseResource){
        setBaseResource(baseResource);
        hasBaseResource = baseResource != null;
        behavior = new EntityBehavior();
        bounds = new Bounds();
    }
    
    /**
     * a wrapper for the initialization method
     */
    public final void doInit(){
        if (!initialized){
            init();
        }
        initialized = true;
    }
    
    /**
     * initializes the entity and loads needed resources
     */
    protected void init(){
        if (hasBaseResource){        
            data = baseResource.getSubResource("data.ini").loadAsIni();
            scriptFile = baseResource.getSubResource("script.js").loadAsScriptFile();
            name = getData().get("basic", "name", String.class);
            addAtBottom = getData().get("basic", "addAtBottom", boolean.class);
            if (behavior.getRenders()){
                if (renderPass == null){
                    renderPass = RenderPassEnum.valueOf(getData().get("basic", "renderPass", String.class));
                }
                generateTextureAndBounds();
            }
        }
        if (!suppressInitScript){
            exicuteScript("init");
        }
    }
    
    /**
     * a wrapper for the destroy method
     */
    public final void doDestroy(){
        destroy();
        space.remove(this);
    }
    
    /**
     * destroys the entity
     */
    protected void destroy(){
        exicuteScript("destroy");
    }
    
    /**
     * @see herbivore.arch.Updatable#update(int)
     */
    @Override
    public void update(int delta){
        if (((flipped && moveRight) || (!flipped && moveLeft)) && !(moveLeft && moveRight)){
            flipped = !flipped;
        }
        float trueSpeed = getSpeed();
        if (moveLeft){
            bounds.x -= trueSpeed*delta;
        }
        if (moveRight){
            bounds.x += trueSpeed*delta;
        }
        if (!getBehavior().getCanFall()){
            return;
        }
        float fallSpeed = 0.89f*delta;
        Bounds check = new Bounds(bounds.x, bounds.y, bounds.width, bounds.height + fallSpeed);
        List<EntityGround> hits = space.groundAt(check);
        ground = null;
        float distance = Float.MAX_VALUE;
        float y = getBounds().y;
        for (int index = 0; index < hits.size(); index++){
            EntityGround hit = hits.get(index);
            if (hit.getBounds().y - y < distance){
                ground = hit;
                distance = hit.getBounds().y - y;
            }
        }
        if (ground != null){
            bounds.y = ground.getBounds().y - bounds.height;
        }
        else {
            bounds.y += fallSpeed;
        }
    }
    
    /**
     * checks for and executes the collisions this entity can generate
     * @param collisionList the list of entities to check for collisions
     * @param delta the delta time in milliseconds. used to calculate movement speed
     */
    public void checkCollisions(List<Entity> collisionList, int delta){
        Bounds myBounds = new Bounds(getBounds());
        float val = getSpeed()*delta;
        if (moveLeft){
            myBounds.x -= val;
        }
        myBounds.width += val;
        for (int index = 0; index < collisionList.size(); index++){
            Entity entity = collisionList.get(index);
            if (!equals(entity) && myBounds.intersects(entity.getBounds())){
                collide(entity);
            }
        }
    }
    
    /**
     * @see herbivore.game.Collidable#collide(herbivore.game.entity.Entity)
     */
    @Override
    public void collide(Entity entity){
        exicuteScript("collide", entity);
    }
    
    /**
     * @see herbivore.arch.Renderable#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        boolean isFlipped = flipped;
        if (this instanceof EntityActor && spriteIndex == 0){
            isFlipped = false;
        }
        renderer.drawImage(sprites[spriteIndex].getFlippedCopy(isFlipped, false), getBounds());
    }
    
    /**
     * executes a function from this entities script file, if it exists
     * @param functionName the name of the function
     * @param args the varargs arguments of the function
     */
    protected void exicuteScript(String functionName, Object... args){
        if (!hasBaseResource){
            return;
        }
        try {
            scriptFile.invoke(functionName, this, args);
        }
        catch (IllegalArgumentException exception){
            Logger.verboseDebug("script " + functionName + " does not exists for " + name);
        }
    }
    
    /**
     * checks whether a sub-resource exists inside this entity, and if it doesn't 
     * returns a fallback. exists to prevent re-implementation of functionality
     * @param subResource the sub-resource to check for
     * @param fallback the resource to fallback to
     * @return 
     */
    protected Resource getResourceOrFallback(String subResource, Resource fallback){
        Resource resource = getBaseResource().getSubResource(subResource);
        if (resource.exists()){
            return resource;
        }
        return fallback;
    }
    
    /**
     * loads and then generates the texture and bounds of this entity
     */
    private void generateTextureAndBounds(){
        Resource resource = getBaseResource().getSubResource("sprites.png");
        if (resource.exists()){
            SpriteSheet mesh = new SpriteSheet(resource);
            int spriteCount = getData().get("sprites", "spriteCount", int.class);
            int spriteWidth = mesh.getWidth()/spriteCount;
            int spriteHeight = mesh.getHeight();
            mesh.setDefaultSize(spriteWidth, spriteHeight);
            sprites = new Image[spriteCount];
            for (int index = 0; index < spriteCount; index++){
                sprites[index] = mesh.chop(spriteWidth*index, 0);
            }
            bounds.setArea(new Area(spriteWidth*BuildInfo.getTextureResizeRatio(), spriteHeight*BuildInfo.getTextureResizeRatio()));
        }
    }
    
    /**
     * moves this entity to the specified space 
     * @param newSpace this entity's new space
     * @param x the x coordinate to add this entity at
     * @param y the y coordinate to add this entity at
     */
    protected void moveToSpace(Space newSpace, float x, float y){
        space.remove(this);
        bounds.x = x;
        bounds.y = y - bounds.height;
        newSpace.add(this, -1, -1);
    }
    
    /**
     * spawns an alert message at this entity
     * @param text the message text
     */
    public void notifyFrom(String text){
        Entity entity = new EntityText(text, 2000);
        space.add(entity, bounds.x + bounds.width/2 - entity.getBounds().width/2, bounds.y);
    }
    
    /**
     * @return whether or not this entity exists in any space
     */
    protected boolean exists(){
        return space != null;
    }
    
    /**
     * @return this entity's current sprite
     */
    protected Image getCurrentSprite(){
        return sprites[spriteIndex];
    }
    
    /**
     * edits this entity's bounds with the specified values as long as 
     * <code>value != -1</code>
     * @param x the x value
     * @param y the y value
     * @param width the width value
     * @param height the height value
     */
    public void editBounds(float x, float y, float width, float height){
        if (x != -1){
            bounds.x = x;
        }
        if (y != -1){
            bounds.y = y;
        }
        if (width != -1){
            bounds.width = width;
        }
        if (height != -1){
            bounds.height = height;
        }
    }
    
    /**
     * @see herbivore.arch.Clickable#getClickBounds()
     */
    @Override
    public Rectangle getClickBounds(){
        Rectangle rect = getBounds().getBounds();
        Location scroll = space.getLevel().getScroll();
        rect.x += scroll.x;
        rect.y += scroll.y;
        return rect;
    }
    
    protected final void setBaseResource(Resource baseResource){this.baseResource = baseResource;}
    protected final void setSprites(Image[] sprites){this.sprites = sprites;}
    protected void setSuppressInitScript(boolean suppressInitScript){this.suppressInitScript = suppressInitScript;}
    
    public void setRenderPass(RenderPassEnum renderPass){this.renderPass = renderPass;}
    public void setName(String name){this.name = name;}
    public void setSpace(Space space){this.space = space;}
    public void setMoveLeft(boolean moveLeft){this.moveLeft = moveLeft;}
    public void setMoveRight(boolean moveRight){this.moveRight = moveRight;}
    public void setFlipped(boolean flipped){this.flipped = flipped;}
    public void setSpeed(float speed){this.speed = speed;}
    public void setSpriteIndex(int spriteIndex){this.spriteIndex = spriteIndex;}
    
    public EntityBehavior getBehavior(){return behavior;}
    public EntityGround getGround(){return ground;}
    public RenderPassEnum getRenderPass(){return renderPass;} 
    public Resource getBaseResource(){return baseResource;}
    public Image[] getSprites(){return sprites;}
    @Override
    public String getClickedName(){return name;}
    public String getName(){return name;}
    public Bounds getBounds(){return bounds;}
    public Space getSpace(){return space;}
    public Ini getData(){return data;}
    public boolean getMoveLeft(){return moveLeft;}   
    public boolean getMoveRight(){return moveRight;} 
    public boolean getAddAtBottom(){return addAtBottom;}
    public boolean isFlipped(){return flipped;}
    public float getSpeed(){return speed;}
    public int getSpriteIndex(){return spriteIndex;}
    
    private EntityBehavior behavior;
    private EntityGround ground;
    private ScriptFile scriptFile;
    private RenderPassEnum renderPass;
    private Resource baseResource;
    private Image[] sprites;
    private Bounds bounds;
    private String name;
    private Space space;
    private Ini data;
    private boolean moveLeft, moveRight, addAtBottom, initialized, flipped, hasBaseResource, suppressInitScript;
    private float speed;
    private int spriteIndex;
}