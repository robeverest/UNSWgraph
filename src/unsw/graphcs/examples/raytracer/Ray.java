package unsw.graphcs.examples.raytracer;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;

/**
 * Defines a Ray starting at a given position and extending along the given direction.
 * @author robertce
 *
 */
public class Ray {
    
    private Point3D position;
    
    private Vector3 direction;
    
    public Ray(Point3D position, Vector3 direction) {
        this.position = position;
        this.direction = direction;
    }
    
    public Point3D getPosition() {
        return position;
    }
    
    public Vector3 getDirection() {
        return direction;
    }

    public Point3D pointAt(float t) {
        return position.translate(direction.scale(t));
    }

    public static Ray betweenPoints(Point3D A, Point3D B) {
        return new Ray(A,B.minus(A));
    }

    /**
     * Assuming this ray is hitting the given position (unchecked), reflect it around
     * the given normal.
     * @param position
     * @param normal
     * @return
     */
    public Ray reflectAround(Point3D position, Vector3 normal) {
        Vector3 r = direction.minus(normal.scale(2 * direction.dotp(normal)));
        return new Ray(position, r);
    }
    
    /**
     * Create a translation of this ray along its direction by t-amount. 
     * @param dt
     * @return
     */
    public Ray offset(float dt) {
        return new Ray(position.translate(direction.scale(dt)), direction);
    }
}
