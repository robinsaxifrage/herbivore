package herbivore.arch;
import herbivore.render.Renderer;

/**
 * an interface for implementation by classes that are rendered
 * @author herbivore
 */
public interface Renderable {

    /**
     * a function to render all needed components
     * @see herbivore.render.Renderer
     * @param renderer the renderer to use
     */
    public void render(Renderer renderer);
    
}
