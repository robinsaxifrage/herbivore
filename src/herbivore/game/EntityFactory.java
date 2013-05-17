package herbivore.game;
import herbivore.game.entity.Entity;
import herbivore.geom.Location;
import herbivore.misc.Logger;
import herbivore.res.Resource;
import herbivore.script.ScriptFile;
import java.lang.reflect.InvocationTargetException;
import org.ini4j.Ini;

/**
 * the static class for loading entities from archives
 * @author herbivore
 */
public class EntityFactory {
    
    /**
     * loads an entity from a specified .entity archive
     * @param prefab the .entity archive
     * @param space the space that the entity will be added to
     * @param arguments the varargs arguments to pass to the entity
     * @return the loaded entity, or null if the load failed
     */
    public static Entity loadEntity(Resource prefab, Space space, Object... arguments){
        Ini data = prefab.getSubResource("data.ini").loadAsIni();
        ScriptFile script = prefab.getSubResource("script.js").loadAsScriptFile();
        String className = "herbivore.game.entity.Entity" + data.get("basic", "class");
        Entity entity = null;
        try {
            Class clazz = Class.forName(className);
            entity = (Entity)clazz.getConstructor(Resource.class).newInstance(prefab);
        }
        catch (InvocationTargetException exception){
            Logger.error(exception.getTargetException(), "prefab declares invalid class: " + className);
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException exception){
            Logger.error(exception, "prefab declares invalid class: " + className);
        }
        if (arguments.length > 0){
            script.invoke("acceptArgs", entity, arguments);
        }
        entity.setSpace(space);
        if (nextAddLocation != null){
            entity.editBounds(nextAddLocation.x, nextAddLocation.y, -1, -1);
        }
        entity.doInit();
        return entity;
    }
    
    public static void setNextAddLocation(Location nextAddLocation){EntityFactory.nextAddLocation = nextAddLocation;}

    private static Location nextAddLocation;
}
