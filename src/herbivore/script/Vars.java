package herbivore.script;
import java.util.HashMap;
import java.util.Map;

/**
 * a static class for use by scripts to store
 * and retrieve string variables
 * @author herbivore
 */
public class Vars {
    
    /**
     * initializes the utility
     */
    private static void init(){
        if (map != null){
            return;
        }
        map = new HashMap();
    }
    
    /**
     * @param reference the reference of the value to get
     * @return the value, or <code>"none"</code> if none was found
     */
    public static String get(String reference){
        init();
        String value = map.get(reference);
        if (value == null){
            value = "none";
        }
        return value;
    }
    
    /**
     * sets a reference to the specified value
     * @param reference the reference to set
     * @param value the value to use
     */
    public static void set(String reference, String value){
        init();
        map.put(reference, value);
    }
    
    private static Map<String, String> map;
}
