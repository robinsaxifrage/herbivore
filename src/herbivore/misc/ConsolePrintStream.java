package herbivore.misc;
import herbivore.run.RunnerConsole;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * a class representing a print stream that writes
 * to the programs in-house console
 * @author herbivore
 */
public class ConsolePrintStream
    extends PrintStream {

    /**
     * creates a new console print stream
     */
    public ConsolePrintStream(){
        this("");
    }
    
    /**
     * creates a new console print stream with the specified color tag
     * @param colorTag the color tag text to append to every line
     */
    public ConsolePrintStream(String colorTag){
        super(new OutputStream(){
            @Override
            public void write(int b){
            }
        });
        this.colorTag = colorTag;
    }

    /**
     * writes the specified string to the console with this print streams color tag
     * @param string the string to writes
     */
    @Override
    public void print(String string){
        String[] array = string.split("\n");
        for (int index = 0; index < array.length; index++){
            String text = array[array.length - index - 1].replaceAll("\t", "       ");
            RunnerConsole.Static.append(colorTag + text);
        }
    }
    
    @Override
    public void print(int it){print(it + "");}
    @Override
    public void print(char it){print(it + "");}
    @Override
    public void print(long it){print(it + "");}
    @Override
    public void print(float it){print(it + "");}
    @Override
    public void print(char[] it){print(new String(it));}
    @Override
    public void print(double it){print(it + "");}
    @Override
    public void print(boolean it){print(it + "");}
    @Override
    public void print(Object it){print(it == null? "null" : it.toString());}
    @Override
    public void println(String string){print(string + "\n");}        
    
    private String colorTag;
}