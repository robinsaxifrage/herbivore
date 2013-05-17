package herbivore.config;

/**
 * the static class containing all build-universal constants
 * @author herbivore
 */
public class BuildInfo {

    /**
     * @return the current builds name and version
     */
    public static String getBuildTitle(){
        return "herbivore 1.04";
    }
    
    /**
     * @return all of the file endings this program recognizes as archives
     */
    public static String[] getSupportedArchives(){
        return new String[]{
            ".entity",
            ".menu",
            ".conv",
            ".note",
            ".ai",
            ".op"
        };
    }
    
    /**
     * @return the ratio that every texture is resized when rendered
     * @see herbivore.game.entity.Entity#generateTextureAndBounds() 
     * @see herbivore.game.entity.EntityItem#generateTextureAndBoundsEquipped(java.lang.String)
     */
    public static int getTextureResizeRatio(){
        return 6;
    }
    
    /**
     * @return the denominator used in audio proximity calculations
     * @see herbivore.sound.Sound#calculateDistance(herbivore.game.entity.Entity, herbivore.game.entity.Entity)
     */
    public static int getAudioProximityDenominator(){
        return 50;
    }
    
}
