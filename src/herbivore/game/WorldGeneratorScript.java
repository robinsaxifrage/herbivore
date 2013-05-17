package herbivore.game;
import herbivore.res.LoadUtils;
import herbivore.res.Resource;
import herbivore.run.RunnerGame;
import herbivore.script.ScriptFile;

/**
 * a class to generate the world from a levels folder
 * @author herbivore
 */
public class WorldGeneratorScript
    implements WorldGenerator {

    /**
     * creates a new world generator from the specified directory
     * @param resource the directory that contains the level to load
     */
    public WorldGeneratorScript(Resource resource){
        LoadUtils.assertCustomSearchLocation(resource.getResourcePath());
        scriptFile = resource.getSubResource("script.js").loadAsScriptFile();
    }
    
    /**
     * @see herbivore.game.WorldGenerator#generate(herbivore.run.RunnerGame)
     */
    @Override
    public Level generate(RunnerGame game){
        return (Level)scriptFile.invoke("generate");
    }

    private ScriptFile scriptFile;
}
