package herbivore.config;
import herbivore.misc.Logger;
import herbivore.res.LoadUtils;
import herbivore.res.Resource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.ini4j.Ini;

/**
 * an ease of access class to allow quick configuration file reads
 * @author herbivore
 */
public class ConfigUtils {
    
    /**
     * initializes the utility
     */
    private static void init(){
        if (instance == null){
            instance = Resource.getResource("options.ini").loadAsIni();
        }
    }
    
    /**
     * a javascript friendly configuration reading method
     * @param sectionName the name of the section
     * @param optionName the name of the option
     * @return the value of the option, represented as a String
     */
    public static String get(String sectionName, String optionName){
        return get(sectionName, optionName, String.class);
    }
    
    /**
     * @param sectionName the name of the section
     * @param optionName the name of the option
     * @param clazz the class of option to be loaded
     * @return the value of the option
     */
    public static <T extends Object> T get(String sectionName, String optionName, Class<T> clazz){
        init();
        return instance.get(sectionName, optionName, clazz);
    }
    
    /**
     * sets an option to a specified value
     * @param sectionName the name of the section
     * @param optionName the name of the option
     * @param value the new value of the option
     */
    public static void put(String sectionName, String optionName, Object value){
        init();
        instance.put(sectionName, optionName, value);
    }
    
    /**
     * stores the configuration file
     */
    public static void store(){
        init();
        try {
            Writer writer = new FileWriter(LoadUtils.getFile("options.ini"));
            instance.store(writer);
            writer.flush();
            writer.close();
        }
        catch (IOException exception){
            Logger.error(exception, "config file storage failed");
        }  
    }    
    
    private static Ini instance;
}
