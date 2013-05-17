package herbivore.script;
import java.util.HashMap;
import java.util.Map;

/**
 * a class representing .js file that contains multiple functions
 * @author herbivore
 */
public class ScriptFile {
    
    /**
     * creates a new script file with the specified contents
     * @param contents the contents of this script file
     */
    public ScriptFile(String contents){
        functions = new HashMap();
        String[] functionBlocks = contents.split("js_function ");
        for (String block : functionBlocks){
            if (!block.equals("")){
                block = "function " + block;
                String functionName = block.substring(9, block.indexOf("("));
                functions.put(functionName, block);
            }
        }
    }
    
    /**
     * invokes a function of this script file with the specified arguments
     * @see herbivore.script.ScriptUtils#runScript(java.lang.String, java.lang.String, herbivore.script.ScriptFile, java.lang.Object[])
     * @param functionName the name of the function
     * @param args the varargs argument list
     * @return the object returned by the script function
     */
    public Object invoke(String functionName, Object... args){
        return ScriptUtils.runScript(functionName, getFunction(functionName), this, args);
    }
    
    /**
     * @param functionName the name of the function to find
     * @return the arguments of the function
     */
    public String argumentsOf(String functionName){
        String function = getFunction(functionName);
        return function.substring(function.indexOf("(") + 1, function.indexOf(")"));
    }
    
    /**
     * returns a function, or throws an exception if it doesn't exist
     * @param name the name of the function
     * @return the function if it exists
     */
    private String getFunction(String name){
        String function = functions.get(name);
        if (function == null){
            throw new IllegalArgumentException("the function " + name + " does not exist");
        }
        return function;
    }
    
    private Map<String, String> functions;
}
