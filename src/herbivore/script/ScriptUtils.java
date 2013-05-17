package herbivore.script;
import herbivore.misc.Logger;
import herbivore.res.Resource;
import java.util.HashMap;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * a static class for javascript parsing and invoking utilities
 * @author herbivore
 */
public class ScriptUtils {

    /**
     * initializes the utility
     */
    private static void init(){
        if (initialized){
            return;
        }
        functionTop = "var packs = JavaImporter();\n"
        + "packs.importPackage(java.lang);\n"
        + "packs.importPackage(Packages.herbivore);\n"
        + "packs.importPackage(Packages.herbivore.game);\n"
        + "packs.importPackage(Packages.herbivore.geom);\n"
        + "packs.importPackage(Packages.herbivore.config);\n"
        + "packs.importPackage(Packages.herbivore.res);\n"
        + "packs.importPackage(Packages.herbivore.run);\n"
        + "packs.importPackage(Packages.herbivore.render);\n"
        + "packs.importPackage(Packages.herbivore.misc);\n"
        + "packs.importPackage(Packages.herbivore.script);\n"
        + "packs.importPackage(Packages.herbivore.sound);\n"
        + "packs.importPackage(Packages.herbivore.ui);\n"
        + "with (packs){";
        functionBottom = "}";
        scriptFiles = new HashMap();
        initialized = true;
    }
    
    /**
     * runs a script from the console
     * @param params the string representation of the parameters
     * @param script the script to execute, without a function around it
     * @param args the varargs arguments
     */
    public static void runConsoleScript(String params, String script, Object... args){
        script = "function run(" + params + "){" + script + "}";
        runScript("console script", script, null, args);
    }
    
    /**
     * runs a script from the specified script file
     * @param name the name reference of the script to be run
     * @param function the function contents
     * @param file the parent script file of the function
     * @param args the argument list for the function
     * @return the object returned by the script
     */
    protected static Object runScript(String name, String function, ScriptFile file, Object[] args){
        init();
        Context context = Context.enter();
        Scriptable scope = context.initStandardObjects();
        Logger.verboseDebug("running script: " + name);
        function = format(function, file);
        try {
            Function functionObject = context.compileFunction(scope, function, name, 0, null);
            Object object = functionObject.call(context, scope, context.newObject(scope), args);
            if (object instanceof NativeJavaObject){
                return ((NativeJavaObject)object).unwrap();
            }
            else {
                return object;
            }
        }
        catch (EcmaError exception){
            System.out.println(exception.getMessage());
            Logger.error(exception, "error in script: " + name + " at line " + function.split("\n")[exception.getScriptStack()[0].lineNumber]);
        }
        catch (Exception exception){
            String message = exception.getMessage();
            String line = "unknown";
            if (message.endsWith(")") && message.contains("#")){ 
                try {
                    line = function.split("\n")[Integer.parseInt(message.substring(message.indexOf("#") + 1, message.indexOf(")")))];
                }
                catch (StringIndexOutOfBoundsException ex){}
            }
            Logger.error(exception, "unexpected exception in script " + name + " (line: " + line + ")");
        }
        return null;
    }
    
    /**
     * formats a script function
     * @param function the function to format
     * @param file the parent file of the function
     * @return 
     */
    private static String format(String function, ScriptFile file){
        if (file != null){
            nextId++;
            scriptFiles.put(nextId, file);
            function = function.replace("$exists", "ScriptUtils.regexInOnly_objectExists");
            function = function.replace("$this", "ScriptUtils.regexInOnly_getScriptFile(" + nextId + ")");
        }
        StringBuilder functionBuilder = new StringBuilder(function);
        functionBuilder.insert(functionBuilder.indexOf("{") + 1, functionTop);
        functionBuilder.insert(functionBuilder.lastIndexOf("}"), functionBottom);
        return functionBuilder.toString();
    }

    /**
     * prints the help file for the console
     */
    public static void printHelp(){
        System.out.print(Resource.getResource("res/consolehelp.txt").loadAsText());
    }
    
    /**
     * a regex only method used for checking object existence inside scripts.
     * it is regexed in for the phrase <code>$exists</code>
     * @param object the object to check for existence
     * @return whether or not the object exists
     */
    public static boolean regexInOnly_objectExists(Object object){
        return object != null;
    }
    
    /**
     * a regex only method used to retrieve the parent script file of 
     * an executing script. it is regexed in for the phrase <code>$this</code>
     * @param id the id of the script to use
     * @return the parent script file
     */
    public static ScriptFile regexInOnly_getScriptFile(int id){
        return scriptFiles.get(id);
    }
        
    private static Map<Integer, ScriptFile> scriptFiles;
    private static String functionTop, functionBottom; 
    private static boolean initialized;
    private static int nextId;
}
