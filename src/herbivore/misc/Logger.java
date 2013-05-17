package herbivore.misc;
import herbivore.config.ConfigUtils;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * a static class for logging debug messages, info and errors.
 * upon initialization of this class, <code>System.out</code> is
 * assigned to the in-house console while <code>System.err</code>
 * is used to print all logged content to the jvm console, if it
 * exists
 * @author herbivore
 */
public class Logger {
    
    /**
     * initializes the logger, if it has not already been initialized
     */
    private static void init(){
        if (initialized){
            return;
        }
        logEntrySigner = new SimpleDateFormat(ConfigUtils.get("logging", "timeSigner"));
        debugEnabled = ConfigUtils.get("logging", "enabled", boolean.class);
        verbose = ConfigUtils.get("logging", "verbose", boolean.class);
        System.setOut(new ConsolePrintStream());
        errorPrintStream = new ConsolePrintStream("<red>");
        debugPrintStream = new ConsolePrintStream("<gray>");
        initialized = true;
    }
    
    /**
     * logs an error
     * @param exception the error's exception
     * @param message the error message to log
     */
    public static void error(Throwable exception, String message){
        init();
        log("error", message, errorPrintStream);
        exception.printStackTrace(System.err);
    }
    
    /**
     * logs a piece of info
     * @param message the info message to log
     */
    public static void info(String message){
        init();
        log("info", message, System.out);
    }
    
    /**
     * logs a debug message, if debugging is enabled in the configuration file
     * @param message the debug message to log
     */
    public static void debug(String message){
        init();
        if (debugEnabled){
            log("debug", message, debugPrintStream);
        }
    }
    
    /**
     * logs a verbose debug message, if debugging and verbosity are
     * enabled in the configuration file. this should be used in place of debug
     * when the message is likely to occur so much that it clutters the output stream
     * @param message the verbose debug message to log
     */
    public static void verboseDebug(String message){
        init();
        if (debugEnabled && verbose){
            log("debug", message, debugPrintStream);
        }
    }
    
    /**
     * logs a message to the specified output
     * @param type the type of message being logged
     * @param message the message to log
     * @param output the output stream to use
     */
    private static void log(String type, String message, PrintStream output){
        message = logEntrySigner.format(new Date()) + " " + type + ": " + message;
        output.println(message);
        System.err.println(message);
    }
    
    private static PrintStream errorPrintStream, debugPrintStream;
    private static DateFormat logEntrySigner;
    private static boolean initialized, debugEnabled, verbose;
}
