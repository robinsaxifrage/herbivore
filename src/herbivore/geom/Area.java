package herbivore.geom;

/**
 * a class representing a geometrical 2d area
 * @author herbivore
 */
public class Area {
    
    /**
     * creates an area with dimension == 0
     */
    public Area(){
    }
    
    /**
     * creates a new area with the specified areas dimensions
     * @param other the area to get this areas dimensions from
     */
    public Area(Area other){
        this.width = other.width;
        this.height = other.height;
    }
    
    /**
     * creates an area with the specified dimensions
     * @param width the width dimension
     * @param height the height dimension
     */
    public Area(float width, float height){
        this.width = width;
        this.height = height;
    }
    
    /**
     * @param bounds the bounds to check for containment
     * @return whether or not the specified bounds are contained within this area
     */
    public boolean contains(Bounds bounds){
        return new Bounds(width, height).contains(bounds);
    }
    
    /**
     * @param bounds the bounds to check for intersection
     * @return whether or not the specified bounds intersect this area
     */
    public boolean intersects(Bounds bounds){
        return new Bounds(width, height).intersects(bounds);
    }
    
    public float width, height;
}