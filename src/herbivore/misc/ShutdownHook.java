package herbivore.misc;
import herbivore.config.ConfigUtils;
import herbivore.res.LoadUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * a runnable class to be executed when the jvm terminates
 * @see herbivore.Herbivore#run_init()
 * @author herbivore
 */
public class ShutdownHook 
    implements Runnable {    
    
    /**
     * stores the configuration file, closes all registered closeable objects
     * and cleans up the temporary directories
     */
    @Override
    public void run(){
        ConfigUtils.store();
        try {
            for (Closeable close : Static.closes){
                close.close();
            }
        }
        catch (IOException exception){
            Logger.error(exception, "failed to close a thing");
        }
        File tempDirectory = LoadUtils.getFile("res/temp");
        if (tempDirectory.exists()){
            for (File nextFile : tempDirectory.listFiles()){
                try {
                    Files.delete(nextFile.toPath());
                }
                catch (IOException exception){
                    Logger.error(exception, "failed to delete temp file " + nextFile.getName());
                }
            }
        }
        tempDirectory.delete();
    }
    
    /**
     * a static class to manage all static aspects of the shutdown hook
     */
    public static class Static {
        
       /**
        * adds a closeable object to the list of closeable objects
        * to be closed on execution of this shutdown hook
        * @param closeable the closeable to add
        */
       public static void addClose(Closeable closeable){
           if (closes == null){
               closes = new ArrayList();
           }
           closes.add(closeable);
       }
       
       /**
        * informs the shutdown hook that the application is crashing
        */
       public static void assertShutdownIsCrash(){
           crashed = true;
       }

       private static List<Closeable> closes;
       private static boolean crashed;
    }
}
