package herbivore.render;
import herbivore.config.BuildInfo;
import herbivore.misc.Logger;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.BufferedImageUtil;

/**
 * a static class to help with the modification of
 * textures using the java 2d api (not that inefficient piece of shit!)
 * @author herbivore
 */
public class Java2DUtils {
    
    /**
     * tiles an image to the specified width
     * @param tile the image to tile
     * @param width the width to use
     * @return a new image, with the specified image tiled on it
     */
    public static BufferedImage createTiledImage(BufferedImage tile, float width){
        image = new BufferedImage((int)(width/BuildInfo.getTextureResizeRatio()), tile.getHeight(), BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        int twidth = tile.getWidth();
        for (int x = 0; x < width; x += twidth){
            graphics.drawImage(tile, null, x, 0);
        }
        graphics.dispose();
        return image;
    }
    
    /**
     * generates a 1px^2 image used for fills
     * @param color the color to use
     * @param alpha the alpha value to use
     * @return the 1px^2 image
     */
    public static BufferedImage generateFill(Color color, float alpha){
        image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setColor(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue()));
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        graphics.fillRect(0, 0, 1, 1);
        graphics.dispose();
        return image;
    }
    
    /**
     * converts a java 2d buffered image to a slickutil texture
     * @param image the image to convert
     * @return the converted slick texture
     */
    public static Image convertToTexture(BufferedImage image){
        try {
            return new Image(BufferedImageUtil.getTexture("converted bufferedimage", image));
        }
        catch (IOException exception){
            Logger.error(exception, "failed to convert texture");
        }
        return null;
    }
    
    private static BufferedImage image;
    private static Graphics2D graphics;
}
