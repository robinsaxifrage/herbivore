package herbivore.run;
import herbivore.Herbivore;
import herbivore.arch.Clickable;
import herbivore.config.ConfigUtils;
import herbivore.game.HudPlacementEnum;
import herbivore.game.IngameUI;
import herbivore.game.Operation;
import herbivore.game.Sky;
import herbivore.game.Space;
import herbivore.game.Level;
import herbivore.game.WorldGenerator;
import herbivore.game.entity.EntityActor;
import herbivore.game.entity.EntityItem;
import herbivore.geom.Area;
import herbivore.geom.Bounds;
import herbivore.geom.Location;
import herbivore.misc.InputList;
import herbivore.render.Font;
import herbivore.render.Renderer;
import herbivore.res.Resource;
import herbivore.sound.Sound;
import herbivore.sound.SoundEngine;
import herbivore.ui.UIUtils;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Mouse;

/**
 * a runner that implements the game itself
 * @author herbivore
 */
public class RunnerGame 
    extends Runner {
    
    /**
     * creates a new runner game with the specified world generator
     * @param generator the generator to use
     */
    public RunnerGame(WorldGenerator generator){
        this.generator = generator;
        setClearsStack(true);
        setUsesLoadingScreen(true);
    }
    
    /**
     * @see herbivore.run.Runner#init()
     */
    @Override
    protected void init(){
        hudFont = UIUtils.getFont("hudFontSize", "basicFontColor");
        disabledHudFont = UIUtils.getFont("hudFontSize", "disabledFontColor");
        hudPlacementRule = HudPlacementEnum.valueOf(ConfigUtils.get("gameplay", "hudPlacement", String.class) + "_placement");
        scrollTriggerZoneSize = ConfigUtils.get("gameplay", "scrollTriggerZoneSize", int.class);
        drawHud = ConfigUtils.get("gameplay", "drawHud", boolean.class);
        verboseHud = ConfigUtils.get("gameplay", "verboseHud", boolean.class);
        aiProcessing = true;
        sky = Sky.get();
        loadWorld();
    }
    
    /**
     * @see herbivore.run.Runner#update(int)
     */
    @Override
    public void update(int delta){
        if (currentUI != null && currentUI.isEnded()){
            setCurrentUI(null);
        }
        EntityActor player = world.getPlayer();
        if (inventoryOpen){
            player.setMoveLeft(false);
            player.setMoveRight(false);
        }
        sky.update(delta);
        world.update(delta);
        Bounds playerBounds = player.getBounds();
        Area spaceSize = player.getSpace().getSize();
        int windowWidth = Renderer.get().getWindowWidth();
        Location scroll = world.getScroll();
        if (spaceSize.width > windowWidth){
            if (playerBounds.x + scroll.x < scrollTriggerZoneSize){
                if (scroll.x < 0f){
                    scroll.x += player.getSpeed()*delta;
                }
                else {
                    scroll.x = 0f;
                }
            }
            else if (playerBounds.x + playerBounds.width + scroll.x > windowWidth - scrollTriggerZoneSize){
                if (-(scroll.x - windowWidth) < spaceSize.width){
                    scroll.x -= player.getSpeed()*delta;
                }
                else {
                    scroll.x = -spaceSize.width + windowWidth;
                }
            }
        }
        else {
            scroll.x = windowWidth/2f - spaceSize.width/2f;
        }
        if (scroll.y > 0){
            scroll.y -= 0.55f*delta;
        }
        else {
            scroll.y = 0;
        }
        SoundEngine.get().setListener(player);
    }

    /**
     * @see herbivore.run.Runner#render(herbivore.render.Renderer)
     */
    @Override
    public void render(Renderer renderer){
        world.render(renderer);
        renderer.setAlpha(1f);
        if (drawHud){
            render_drawHud(renderer);
        }
        if (inventoryOpen){
            render_drawInv(renderer);
        }
        else if (statsOpen){
            render_drawStats(renderer);
        }
        renderer.setTranslation(new Location());
        if (currentUI != null){
            currentUI.render(renderer);
        }
    }
    
    /**
     * renders the hud
     * @param renderer the renderer to use
     */
    private void render_drawHud(Renderer renderer){
        int textHeight = hudFont.getHeight("x");
        List<Operation> operations = world.getPlayer().getOperations();
        List<String> controls = new ArrayList();
        if (hudPlacementRule == HudPlacementEnum.dynamic_placement){
            int y = -10;
            EntityActor player = world.getPlayer();
            Bounds playerBounds = player.getBounds();
            for (Operation operation:operations){
                if (!controls.contains(operation.getControlName())){
                    String text = getOperationText(operation, player.isFlipped());
                    controls.add(operation.getControlName());
                    renderer.drawString(text, operation.enabled()? hudFont : disabledHudFont, player.isFlipped()? (int)playerBounds.x + (int)playerBounds.width + 10 : (int)playerBounds.x - 10 - hudFont.getWidth(text), (int)playerBounds.y + y);
                    y += textHeight;
                }
            }
        }
        else if (hudPlacementRule == HudPlacementEnum.static_placement){
            renderer.setTranslation(new Location());            
            int y = renderer.getWindowHeight() - textHeight - 30;
            for (int index = operations.size() - 1; index >= 0; index--){
                Operation operation = operations.get(index);
                if (!controls.contains(operation.getControlName())){
                    String text = getOperationText(operation, false);
                    controls.add(operation.getControlName());
                    renderer.drawString(text, operation.enabled()? hudFont : disabledHudFont, renderer.getWindowWidth()/2 - hudFont.getWidth(text)/2, y);
                    y -= textHeight;
                }
            }
        }
    }
    
    /**
     * renders the statistics
     * @param renderer the renderer to use
     */   
    private void render_drawStats(Renderer renderer){
        EntityActor player = world.getPlayer();
        int stam = (int)(((double)player.getStamina()/player.getStaminaMax())*100);
        if (stam > 100){
            stam = 100;
        }
        int health = player.getHealth();
        String text = "stamina - " + stam + "% / health - " + health;
        float x = 0;
        float y = 0;
        if (hudPlacementRule == HudPlacementEnum.dynamic_placement){
            Bounds playerBounds = player.getBounds();
            x = playerBounds.x + playerBounds.width/2 - hudFont.getWidth(text)/2;
            y = (int)playerBounds.y - 50;
        }
        else if (hudPlacementRule == HudPlacementEnum.static_placement){
            x = renderer.getWindowWidth()/2 - hudFont.getWidth(text)/2;
            y = 30;
        }
        renderer.drawString(text, hudFont, x, y);
    }
    
    /**
     * renders the inventory user interface
     * @param renderer the renderer to use
     */
    private void render_drawInv(Renderer renderer){
        EntityActor player = world.getPlayer();
        int width = 0;
        int dashWidth = hudFont.getWidth("-");
        int bracketWidth = hudFont.getWidth("[");
        int textHeight = hudFont.getHeight("[");
        for (EntityItem item : player.getInventory()){
            if (item != null){
                width += item.getBounds().width;
            }
            else {
                width += dashWidth;
            }
            width += 10;
        }
        int stringDrawY, itemDrawY, itemNameDrawY, x;
        boolean additive;
        if (hudPlacementRule == HudPlacementEnum.dynamic_placement){
            Bounds playerBounds = player.getBounds();
            x = (int)playerBounds.x + (int)playerBounds.width/2 - width/2;
            stringDrawY = (int)playerBounds.y - 50;
            itemDrawY = (int)playerBounds.y - 50 + textHeight/2;
            itemNameDrawY = (int)playerBounds.y - 100;
            additive = false;
        }
        else if (hudPlacementRule == HudPlacementEnum.static_placement){
            x = renderer.getWindowWidth()/2 - width/2;
            stringDrawY = 30 + textHeight;
            itemDrawY = 30 + textHeight/2;
            itemNameDrawY = 30;
            additive = true;
        }
        else {
            return;
        }
        for (int index = 0; index < player.getInventory().size(); index++){
            EntityItem item = player.getInventory().get(index);
            int multi;
            if (index == player.getInventory().getCurrentItem()){
                renderer.drawString("[", hudFont, x, stringDrawY);
                x += bracketWidth*1.5;
            }
            if (item != null){
                Bounds itemBounds = item.getBounds();
                item.renderFloating(renderer, new Location(x, itemDrawY + (additive? itemBounds.height/2 : -itemBounds.height/2)));
                multi = (int)itemBounds.width;
            }
            else {
                renderer.drawString("-", hudFont, x, stringDrawY);
                multi = dashWidth;
            }
            if (index == player.getInventory().getCurrentItem()){
                String text = player.getInventory().current() != null? player.getInventory().current().getName() : "<empty>";
                renderer.drawString(text, hudFont, x + multi/2 - hudFont.getWidth(text)/2, itemNameDrawY);
                multi += bracketWidth*0.5f;
            }
            x += multi;
            if (index == player.getInventory().getCurrentItem()){
                renderer.drawString("]", hudFont, x, stringDrawY);
                x += bracketWidth;
            }
            x += 10;
        }
    }
    
    /**
     * @see herbivore.run.Runner#parseInput(herbivore.misc.InputList)
     */
    @Override
    public void parseInput(InputList inputList){
        if (currentUI != null){
            currentUI.parseInput(inputList);
        }
        parseInput_playerControl(inputList);
        parseInput_inventoryControl(inputList);
        if (inputList.containsRelease("pauseGame")){
            Herbivore.get().pushRunner(new PopupRunner(Resource.getResource("res/menu/gamePaused.menu")));
        }
        if (inputList.containsPress("showStats")){
            statsOpen = true;
        }
        if (inputList.containsRelease("hideStats")){
            statsOpen = false;
        }
        List<Operation> operations = world.getPlayer().getOperations();
        for (Operation operation : operations){
            if (inputList.containsRelease(operation.getControlName()) && operation.enabled()){
                operation.perform();
                break;
            }
        }
    }
    
    /**
     * parses the input list for player affecting control events
     * @param inputList the input list to parse
     */
    private void parseInput_playerControl(InputList inputList){
        EntityActor player = world.getPlayer();
        if (inputList.containsPress("moveLeft")){
            player.setMoveLeft(true);
        }
        if (inputList.containsRelease("moveLeft")){
            player.setMoveLeft(false);
        }
        if (inputList.containsPress("moveRight")){
            player.setMoveRight(true);
        }
        if (inputList.containsRelease("moveRight")){
            player.setMoveRight(false);
        }
        if (inputList.containsPress("sprint")){
            player.setSprinting(true);
        }
        if (inputList.containsRelease("sprint")){
            player.setSprinting(false);
        }
        if (inputList.containsPress("jump")){
            player.jump();
        }
    }
    
    /**
     * parses the input list for inventory affecting control events
     * @param inputList the input list to parse
     */
    private void parseInput_inventoryControl(InputList inputList){
        EntityActor player = world.getPlayer();
        if (inputList.containsPress("showInv")){
            inventoryOpen = true;
        }
        if (inputList.containsRelease("hideInv")){
            inventoryOpen = false;
        }
        if (inventoryOpen){
            if (inputList.containsRelease("invSelectionLeft")){
                player.getInventory().modCurrent(-1);
            }
            else if (inputList.containsRelease("invSelectionRight")){
                player.getInventory().modCurrent(1);
            }
        }
    }

    /**
     * @see herbivore.run.Runner#getClickables(java.util.List)
     */
    @Override
    public void getClickables(List<Clickable> clickables){
        if (currentUI != null){
            currentUI.getClickables(clickables);
        }
        else {
            clickables.addAll(world.getPlayer().getSpace().getEntities());
        }
    }
    
    /**
     * @see herbivore.run.Runner#destroy() 
     */
    @Override
    protected void destroy(){
        for (Space space : world){
            for (Sound sound : space.getAmbiences()){
                sound.stop();
            }
        }
    }
    
    /**
     * @param operation the operation to represent
     * @param flipped whether or not the text should be flipped
     * @return a string representing the operation
     */
    private String getOperationText(Operation operation, boolean flipped){
        String controlKey = ConfigUtils.get("controls", operation.getControlName(), String.class);
        String description = operation.getDescription(verboseHud);
        return flipped? "[" + controlKey + "] - " +  description :  description + " - [" + controlKey + "]";
    }
    
    /**
     * generates loads the world from this games world generator
     */
    public void loadWorld(){
        world = generator.generate(this);
        world.setGame(this);
    }
    
    /**
     * closes both the inventory and statistics
     */
    public void closeHuds(){
        statsOpen = false;
        inventoryOpen = false;
    }
    
    /**
     * sets the current in game gui
     * @param currentUI the new current gui
     */
    public void setCurrentUI(IngameUI currentUI){
        Mouse.setGrabbed(currentUI == null);
        this.currentUI = currentUI;
    }
    
    public void setAiProcessing(boolean aiProcessing){this.aiProcessing = aiProcessing;}
    
    public Level getWorld(){return world;}    
    @Override
    public boolean getGrabsMouse(){return currentUI == null;}
    public boolean getAiProcessing(){return aiProcessing;}
    
    private HudPlacementEnum hudPlacementRule;
    private WorldGenerator generator;
    private IngameUI currentUI;
    private Level world;
    private Font hudFont, disabledHudFont;
    private Sky sky;
    private boolean inventoryOpen, statsOpen, drawHud, verboseHud, aiProcessing;
    private int scrollTriggerZoneSize;
}
