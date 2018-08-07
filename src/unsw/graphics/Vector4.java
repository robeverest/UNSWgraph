/**
 * 
 */
package unsw.graphics;

import java.util.Arrays;

import unsw.graphics.geometry.Point3D;

/**
 * A vector of rank 4.
 * 
 * This can be used to represent homogenous coordinates in 3D.
 * 
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Vector4 {
    private float[] values;
    
    /**
     * Construct a vector from the given array of float.
     * 
     * @throws IllegalArgumentException if values.length != 4
     * @param values
     */
    public Vector4(float[] values) {
        if (values.length != 4)
            throw new IllegalArgumentException("Vector4 constructor passed an array of length " + values.length);
        
        this.values = Arrays.copyOf(values, 4);
    }
    
    /**
     * Construct a vector from the given x, y and z values.
     * @param x
     * @param y
     * @param z
     * @param w
     */
    public Vector4(float x, float y, float z, float w) {
        this.values = new float[] { x, y, z, w };
    }
    
    /**
     * Compute the dot product of this vector with the given vector.
     * @param b
     * @return
     */
    public float dotp(Vector4 b) {
        float r = 0;
        for (int i = 0; i < values.length; i++) {
            r += this.values[i] * b.values[i];
        }
        return r;
    }
    
    /**
     * Ignores the final value to create a 2D point
     * @return
     */
    public Point3D asPoint3D() {
        return new Point3D(values[0], values[1], values[2]);
    }
}
