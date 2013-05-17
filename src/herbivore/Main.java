package herbivore;
import herbivore.config.ConfigUtils;
import herbivore.misc.ThreadPolicyEnum;
import java.awt.EventQueue;

/** 
 * the runtime startup class
 * @author herbivore
 * @see #main(java.lang.String[])
 */
public class Main {
    
    /**
     * determines thread policy and begins execution
     * @param args the command line arguments
     */
    public static void main(String[] args){
        //"_thread" is concatinated because "new" is an allowed enum value
        ThreadPolicyEnum threadPolicy = ThreadPolicyEnum.valueOf(ConfigUtils.get("runtime", "threadPolicy") + "_thread");
        Herbivore herbivore = Herbivore.get();
        switch (threadPolicy){
            case main_thread:
                //run in main thread
                herbivore.run();
                break;
            case new_thread:
                //run in new thread
                new Thread(herbivore).start();
                break;
            case queue_thread:
                //run after this thread completes exicution
                EventQueue.invokeLater(herbivore);
                break;
            default:
                throw new RuntimeException("unknown thread policy: " + threadPolicy);
        }
    }
    
}
