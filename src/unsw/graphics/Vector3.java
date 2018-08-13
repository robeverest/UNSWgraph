/**
 * 
 */
package unsw.graphics;

import java.util.Arrays;

import unsw.graphics.geometry.Point2D;

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
}
