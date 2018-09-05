package unsw.graphics.world;

import unsw.graphics.geometry.Point3D;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private Point3D position;
    
    public Tree(float x, float y, float z) {
        position = new Point3D(x, y, z);
    }
    
    public Point3D getPosition() {
        return position;
    }
    

}
