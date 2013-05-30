package herbivore.render;
import herbivore.config.BuildInfo;
import herbivore.config.ConfigUtils;
import herbivore.game.Sky;
import herbivore.geom.Bounds;
import herbivore.geom.Location;
import herbivore.misc.Logger;
import herbivore.misc.SpeedTracker;
import herbivore.res.LoadUtils;
import herbivore.res.Resource;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/**
 * a class used for opengl rendering
 * @author herbivore
 */
public class Renderer {
    
    /**
     * @return the singleton instance of the renderer
     */
    public static Renderer get(){
        if (instance == null){
            instance = new Renderer();
        }
        return instance;
    }
    
    /**
     * resets the renderer
     */
    public static void reset(){
        Display.destroy();
        instance = new Renderer();
    }
    
    private static Renderer instance;
    
    /**
     * creates a new render engine and initializes it
     */
    private Renderer(){
        init_loadSettings();
        init_initializeDisplay();
        init_initializeOpenGL();
        init_generateVignette();
        init_generateIcons();
        versionFont = new Font(25, Color.red);
        loadingFont = new Font(50, Color.white);
        screenSigner = new SimpleDateFormat("mm-dd-yy");
        speedTracker = SpeedTracker.get();
    }
    
    /**
     * loads all configurable settings from the configuration file
     */
    private void init_loadSettings(){
        width = ConfigUtils.get("graphics", "windowWidth", int.class);
        height = ConfigUtils.get("graphics", "windowHeight", int.class);
        vSync = ConfigUtils.get("graphics", "vSync", boolean.class);
        fullscreen = ConfigUtils.get("graphics", "fullscreen", boolean.class);
        drawVersion = ConfigUtils.get("graphics", "drawVersion", boolean.class);
        drawVignette = ConfigUtils.get("graphics", "drawVignette", boolean.class);
        softwareGL = ConfigUtils.get("graphics", "softwareGL", boolean.class);
        decoratedWindow = ConfigUtils.get("graphics", "decoratedWindow", boolean.class);
        System.setProperty("org.lwjgl.opengl.Window.undecorated", !decoratedWindow + "");
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", softwareGL + "");
    }
    
    /**
     * sets up and initializes the display 
     */
    private void init_initializeDisplay(){
        Display.setTitle(BuildInfo.getBuildTitle());
        Display.setVSyncEnabled(vSync);
        try {
            if (width < 800 || height < 600){
                Logger.info("screen resolution is too low, resetting to 800px by 600px");
                width = 800;
                height = 600;
            }
            if (fullscreen){
                Display.setFullscreen(true);
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                width = screenSize.width;
                height = screenSize.height;
            }
            else {
                Display.setDisplayMode(new DisplayMode(width, height));
            }
            Display.create();
            Logger.info("display created at " + Display.getWidth() + "px by " + Display.getHeight() + "px");
        }
        catch (LWJGLException exception){
            Logger.error(exception, "display creation failed");
        }
    }
    
    /**
     * initializes the double buffering system and opengl,
     * in orthographic rendering mode
     */
    private void init_initializeOpenGL(){
        fbo = new FBO(this);
        setAlpha(1.0f);
        setTranslation(new Location());
        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    
    /**
     * generates the vignette to be drawn over the frames, if 
     * it is enabled in the configuration file
     */
    private void init_generateVignette(){
        vignette = new FBO(this);
        Image img = Resource.getResource("res/art/display/vignette.png").loadAsImage();
        vignette.enable();
        drawImage(img, new Bounds(width, height));
        vignette.disable();
    }
    
    /**
     * loads all possible icons for the display, and then sets
     * the appropriate one
     */
    private void init_generateIcons(){
        String[] iconDims = new String[]{"16", "32", "128"};
        ByteBuffer[] iconTextures = new ByteBuffer[iconDims.length];
        for (int index = 0; index < iconDims.length; index++){
            iconTextures[index] = ByteBuffer.wrap(Resource.getResource("res/art/display/icon" + iconDims[index] + ".png").loadAsImage().getTexture().getTextureData());
        }
        Display.setIcon(iconTextures);
    }
    
    /**
     * begins a new render by clearing the back and front end buffers
     */
    public void beginRender(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        fbo.enable();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    /**
     * ends a render by drawing the vignette and/or/nor version and drawing
     * the back buffer onto the front buffer, to be grabbed by the display
     * when it swaps buffers
     */
    public void endRender(){
        if (drawVersion){
            String text = BuildInfo.getBuildTitle() + " @ " + speedTracker.getFPS() + " fps";
            drawString(text, versionFont, 10, 10);
        }
        if (drawVignette){
            drawFBO(vignette);
        }
        fbo.disable();
        drawFBO(fbo);
    }
    
    /**
     * pushes the transformation matrix into memory and translates to
     * the center of the specified bounds, as well as sets rotation and alpha
     * @param bounds the bounds to use
     */
    private void pushAndPrep(Rectangle2D bounds){
        glPushMatrix();
        double halfWidth = bounds.getWidth()/2;
        double halfHeight = bounds.getHeight()/2;
        glTranslated(bounds.getX() + halfWidth + translation.x, bounds.getY() + halfHeight + translation.y, 0f);
        glRotatef(rotation, 0f, 0f, 1f);
        glTranslated(-halfWidth, -halfHeight, 0f);
        getColorAndAlpha(Color.white).bind();
    }
    
    /**
     * enables the 2d texture capability and sets the min/mag filters to
     * the nearest neighbor algorithm
     */
    private void enableTextures(){
        glEnable(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }
    
    /**
     * @param color the color to use
     * @return the specified color with the renderer's current alpha value
     */
    private Color getColorAndAlpha(Color color){
        Color theColor = new Color(color);
        theColor.a = alpha;
        return theColor;
    }
    
    /**
     * draws an frame buffer object with the frames dimensions
     * @param fbo the frame buffer object to draw
     */
    public void drawFBO(FBO fbo){
        enableTextures();
        glBindTexture(GL_TEXTURE_2D, fbo.getTextureID());
        glBegin(GL_QUADS);
            glTexCoord2f(0, 1);
            glVertex2f(0f, 0f);
            glTexCoord2f(1, 1);
            glVertex2f(width, 0f);
            glTexCoord2f(1, 0);
            glVertex2f(width, height);
            glTexCoord2f(0, 0);
            glVertex2f(0f, height);
        glEnd();
        glDisable(GL_TEXTURE_2D);
    }
    
    /**
     * draws an image
     * @param image the image to draw
     * @param bounds the bounds to draw the image at
     */
    public void drawImage(Image image, Rectangle2D bounds){
        enableTextures();
        pushAndPrep(bounds);
        image.draw(0f, 0f, (float)bounds.getWidth(), (float)bounds.getHeight(), getColorAndAlpha(Color.white));
        glPopMatrix();
        glDisable(GL_TEXTURE_2D);
        Color.white.bind();
    }
    
    /**
     * draws a string
     * @param string the string to draw
     * @param font the font to draw the string with
     * @param x the x coordinate to draw at
     * @param y the y coordinate to draw at
     */
    public void drawString(String string, Font font, float x, float y){
        enableTextures();
        pushAndPrep(new Bounds(x, y, font.getWidth(string), font.getHeight(string)));
        font.getFont().drawString(0f, 0f, string, getColorAndAlpha(Color.white));
        glPopMatrix();
        glDisable(GL_TEXTURE_2D);
        Color.white.bind();
    }
    
    /**
     * draws a line between two points
     * @param x1 the x coordinate of point 1
     * @param y1 the y coordinate of point 1
     * @param x2 the x coordinate of point 2
     * @param y2 the y coordinate of point 2
     * @param stroke the stroke to use
     * @param color the color to use
     */
    public void drawLine(int x1, int y1, int x2, int y2, int stroke, Color color){
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); 
        getColorAndAlpha(color).bind();
        glLineWidth(stroke); 
        glBegin(GL_LINES); 
            glVertex2i(x1, y1); 
            glVertex2i(x2, y2); 
        glEnd(); 
        Color.white.bind();
    }
    
    /**
     * draws a quad, or square
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width dimension
     * @param height the height dimension
     * @param color the color to use
     */
    public void drawQuad(int x, int y, int width, int height, Color color){
        getColorAndAlpha(color).bind();
        glBegin(GL_QUADS);
            glVertex2i(x, y);
            glVertex2i(x, y + height);
            glVertex2i(x + width, y + height);
            glVertex2i(x + width, y);
        glEnd();
        Color.white.bind();
    }
    
    /**
     * draws the single-shot loading frame and swaps the displays buffers
     */
    public void drawLoadingFrame(){
        String text = "loading";
        int textWidth = loadingFont.getWidth(text);
        int textHeight = loadingFont.getHeight(text);
        int x = width/2 - textWidth/2;
        int y = height/2 - textHeight/2;
        beginRender();
        Sky.get().render(this);
        setAlpha(0.75f);
        drawQuad(x - 10, y - 10, textWidth + 20, textHeight + 20, Color.black);
        setAlpha(1f);
        drawString(text, loadingFont, x, y);
        endRender();
        try {
            Display.swapBuffers();
        }
        catch (LWJGLException exception){
            Logger.error(exception, "error swapping buffers for loading screen");
        }
    }
    
    /**
     * captures a screen shot of the current frame and saves it to the
     * screen shot directory at <code>res/screens/</code>
     */
    public void captureScreenShot(){
        int bytesPerPixel = 4;
        ByteBuffer screenBuffer = BufferUtils.createByteBuffer(width * height * bytesPerPixel);
        fbo.enable();
        glReadPixels(0, 0,width, height, GL_RGBA,GL_UNSIGNED_BYTE, screenBuffer);
        fbo.disable();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x ++ ) {
                int index = y * width * bytesPerPixel + x * bytesPerPixel;
                int red = screenBuffer.get(index) & 0xFF;
                int green = screenBuffer.get(index + 1) & 0xFF;
                int blue = screenBuffer.get(index + 2) & 0xFF;
                image.setRGB(x, height - y - 1, (0xFF << 24) | (red << 16) | (green << 8) | blue);
            }
        }
        String location = "res/screens/" + screenSigner.format(new Date());
        File file = LoadUtils.getFile(location + ".png");
        int times = 1;
        while (file.exists()){
            file = LoadUtils.getFile(location + "(" + times + ").png");
            times++;
        }
        try {
            ImageIO.write(image, "PNG", file);
        }
        catch(IOException exception){
            Logger.error(exception, "failed to save screenshot");
        }
        Logger.info("screenshot saved to " + file.getPath());
    }
    
    public void setSpeedTracker(SpeedTracker speedTracker){this.speedTracker = speedTracker;}
    public void setTranslation(Location translation){this.translation = translation;}
    public void setRotation(float rotation){this.rotation = rotation;}
    public void setAlpha(float alpha){this.alpha = alpha;}
    
    public boolean isWindowDecorated(){return decoratedWindow;}
    public int getWindowHeight(){return height;}
    public int getWindowWidth(){return width;}
    
    private SpeedTracker speedTracker;
    private DateFormat screenSigner;
    private Location translation;
    private Font versionFont, loadingFont;
    private FBO fbo, vignette;
    private boolean softwareGL, decoratedWindow, vSync, fullscreen, drawVersion, drawVignette;
    private float rotation, alpha;
    private int width, height;
}