package herbivore.geom;
import java.awt.geom.Point2D;

/**
 * a class representing a set of geometrical 2d coordinates
 * @author herbivore
 */
public class Location
    extends Point2D.Float {
    
    /**
     * creates a new location with coordinates == 0
     */
    public Location(){
    }
    
    /**
     * creates a new location with the specified locations coordinates
     * @param other the location to get this locations coordinates from
     */
    public Location(Location other){
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * creates a new location with the specified coordinates
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Location(float x, float y){
        this.x = x;
        this.y = y;
    }
    
}