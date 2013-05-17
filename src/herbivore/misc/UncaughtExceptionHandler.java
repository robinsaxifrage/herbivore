package herbivore.misc;

/**
 * a class representing the default uncaught exception
 * handler for this program
 * @author herbivore
 */
public class UncaughtExceptionHandler 
    implements Thread.UncaughtExceptionHandler {

    /**
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable){
        Logger.error(throwable, "system crash");
        System.exit(1);
    }
    
}
