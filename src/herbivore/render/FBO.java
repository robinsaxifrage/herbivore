package herbivore.render;
import java.nio.ByteBuffer;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * a class representing an opengl frame buffer object,
 * which is used as a pseudo-texture
 * @author herbivore
 */
public final class FBO {

    /**
     * creates a frame buffer object
     * @param renderer the renderer to generate this frame buffer object with
     */
    protected FBO(Renderer renderer){
        frameBuffer = glGenFramebuffersEXT();
        colorTexture = glGenTextures();
        enable();
        glBindTexture(GL_TEXTURE_2D, colorTexture);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, renderer.getWindowWidth(), renderer.getWindowHeight(), 0, GL_RGBA, GL_INT, (ByteBuffer)null);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colorTexture, 0);
        disable();
    }
    
    /**
     * enables the frame buffer object in opengl
     */
    public void enable(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBuffer);
    }
    
    /**
     * disables the frame buffer object in opengl
     */
    public void disable(){
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }
    
    public int getTextureID(){return colorTexture;}
    
    private int colorTexture, frameBuffer;
}