package unsw.graphcs.examples.raytracer;


import java.awt.Color;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;

/**
 * A simple checkerboard on the x-z plane
 * @author Robert Clifton-Everest
 *
 */
public class Plane extends SceneObject {

    private static final Vector3 up = new Vector3(0, 1, 0);

    public Plane(Point3D pos, float shininess) {
        super(pos, shininess);
    }

    @Override
    public float intersectWithRay(Ray r) {
        return - r.getPosition().getY() / r.getDirection().getY();
    }

    @Override
    public Color getColour(Point3D pos) {
        //Using logical XOR (^) here
        if (Math.round(pos.getX()) % 2 == 0 ^ Math.round(pos.getZ()) % 2 == 0) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    @Override
    public Vector3 getNormal(Point3D p_hit) {
        return up;
    }


}
