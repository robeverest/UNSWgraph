/**
 * 
 */
package unsw.graphics;

import java.util.Arrays;

import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;

/**
 * A vector of rank 3.
 * 
 * This can be used to represent homogenous coordinates in 2D.
 * 
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Vector3 {
    private float[] values;
    
    /**
     * Construct a vector from the given array of float.
     * 
     * @throws IllegalArgumentException if values.length != 3
     * @param values
     */
    public Vector3(float[] values) {
        if (values.length != 3)
            throw new IllegalArgumentException("Vector3 constructor passed an array of length " + values.length);
        
        this.values = Arrays.copyOf(values, 3);
    }
    
    /**
     * Construct a vector from the given x, y and z values.
     * @param x
     * @param y
     * @param z
     */
    public Vector3(float x, float y, float z) {
        this.values = new float[] { x, y, z };
    }
    
    /**
     * Compute the dot product of this vector with the given vector.
     * @param b
     * @return
     */
    public float dotp(Vector3 b) {
        float r = 0;
        for (int i = 0; i < values.length; i++) {
            r += this.values[i] * b.values[i];
        }
        return r;
    }
    
    /**
     * Ignores the final column to create a 2D point
     * @return
     */
    public Point2D asPoint2D() {
        return new Point2D(values[0], values[1]);
    }

    /**
     * Compute the cross product of this vector with v.
     * @param v
     * @return
     */
    public Vector3 cross(Vector3 v) {
        return new Vector3(values[1]*v.values[2] - values[2]*v.values[1],
                values[2]*v.values[0] - values[0]*v.values[2],
                values[0]*v.values[1] - values[1]*v.values[0]);
    }

    /**
     * Add 0 as a w component.
     * @return
     */
    public Vector4 extend() {
        return new Vector4(values[0], values[1], values[2], 0);
    }

    public float getX() {
        return values[0];
    }
    
    public float getY() {
        return values[1];
    }
    
    public float getZ() {
        return values[2];
    }

    public Vector3 normalize() {
        return scale(1/length());
    }

    public Vector3 scale(float s) {
        return new Vector3(values[0] * s, values[1] * s, values[2] * s);
    }

    private float length() {
        return (float) Math.sqrt(values[0]*values[0] + values[1]*values[1] + values[2]*values[2]);
    }

    public Vector3 negate() {
        return scale(-1);
    }

    public Vector3 plus(Vector3 b) {
        return new Vector3(getX() + b.getX(), getY() + b.getY(), getZ() + b.getZ());
    }

    public Point3D asPoint3D() {
        return new Point3D(getX(), getY(), getZ());
    }

    public Vector3 minus(Vector3 v) {
        return plus(v.negate());
    }
}
