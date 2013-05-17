package herbivore.geom;
import java.awt.geom.Rectangle2D;

/**
 * a class representing a geometrical 2d bounding box
 * @author herbivore
 */
public class Bounds
    extends Rectangle2D.Float {

    /**
     * creates a new bounds with dimensions and coordinates == 0
     */
    public Bounds(){
    }
    
    /**
     * creates a new bounds with the specified bounds coordinates and dimensions
     * @param other the bounds to get this bounds coordinates and dimensions from
     */
    public Bounds(Bounds other){
        this.x = other.x;
        this.y = other.y;
        this.width = other.width;
        this.height = other.height;
    }
    
    /**
     * creates a new bounds with the specified dimensions
     * @param width the width dimension
     * @param height the height dimension
     */
    public Bounds(float width, float height){
        this.width = width;
        this.height = height;
    }
    
    /**
     * creates a new bounds with the specified coordinates and dimensions
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width dimension
     * @param height the height dimension
     */
    public Bounds(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }    
    
    /**
     * sets this bounds location to the specified location
     * @param location the location to use
     */
    public void setLocation(Location location){
        x = location.x;
        y = location.y;
    }
    
    /**
     * sets this bounds area to the specified area
     * @param area the area to use
     */
    public void setArea(Area area){
        width = area.width;
        height = area.height;
    }
    
    /**
     * calculates the distance between this bounds and the specified bounds
     * @param other the other bounds to use
     * @return the shortest distance between the two bounds centers
     */
    public float distance(Bounds other){
        double xDistance = other.getCenterX() - getCenterX();
        double yDistance = other.getCenterY() - getCenterY();
        if (xDistance < 0f){
            xDistance = -xDistance;
        }
        if (yDistance < 0f){
            yDistance = -yDistance;
        }
        return (float)Math.sqrt((xDistance*xDistance) + (yDistance*yDistance));
    }
    
    public Location getLocation(){return new Location(x, y);}
    public Area getArea(){return new Area(width, height);}
}