/**
 * 
 */
package unsw.graphics;

import java.util.Arrays;

import unsw.graphics.geometry.Point2D;

/**
 * A matrix in 3D space.
 * 
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Matrix3 {
    // Matrix is stored in column-major order to match OpenGL
    private float[] values;
    
    /**
     * Construct a matrix from the given flat array of values.
     * @param values The values for the matrix in column-major order.
     */
    public Matrix3(float[] values) {
        if (values.length != 9)
            throw new IllegalArgumentException("Matrix3 constructor passed an array of length " + values.length);
        this.values = Arrays.copyOf(values, 9);
    }
    
    /**
     * Create an identity matrix.
     */
    public static Matrix3 identity() {
        float[] values = new float[] {
            1, 0, 0, // i
            0, 1, 0, // j
            0, 0, 1  // phi
        };
        return new Matrix3(values);
    }
    
    /**
     * Create a translation matrix to the given point.
     * @param x The x coordinate of the new origin
     * @param y The y coordinate of the new origin
     * @return
     */
    public static Matrix3 translation(float x, float y) {
        float[] values = new float[] {
            1, 0, 0, // i
            0, 1, 0, // j
            x, y, 1  // phi
        };
        return new Matrix3(values);
    }
    
    /**
     * Create a translation matrix to the given point.
     * @param x The x coordinate of the new origin
     * @param y The y coordinate of the new origin
     * @return
     */
    public static Matrix3 translation(Point2D point) {
        return translation(point.getX(), point.getY());
    }
    
    /**
     * Create a rotation matrix.
     * @param degrees Rotate around the origin this many degrees
     * @return
     */
    public static Matrix3 rotation(float degrees) {
        double radians = Math.toRadians(degrees);
        float[] values = new float[] {
            (float) Math.cos(radians), (float) Math.sin(radians), 0, // i
            (float) -Math.sin(radians), (float) Math.cos(radians), 0, // j
            0, 0, 1  // phi
        };
        return new Matrix3(values);
    }
    
    /**
     * Create a scale matrix.
     * @param x Scale the i-axis by this much
     * @param y Scale the j-axis by this much
     * @return
     */
    public static Matrix3 scale(float x, float y) {
        float[] values = new float[] {
            x, 0, 0, // i
            0, y, 0, // j
            0, 0, 1  // phi
        };
        return new Matrix3(values);
    }
    
    /**
     * Create a horizontal shear matrix.
     * @param h
     * @return
     */
    public static Matrix3 horizontalShear(float h) {
        float[] values = new float[] {
            1, 0, 0, // i
            h, 1, 0, // j
            0, 0, 1  // phi
        };
        return new Matrix3(values);
    }
    
    /**
     * Create a vertical shear matrix.
     * @param h
     * @return
     */
    public static Matrix3 verticalShear(float v) {
        float[] values = new float[] {
            1, v, 0, // i
            0, 1, 0, // j
            0, 0, 1  // phi
        };
        return new Matrix3(values);
    }
    
    @Override
    public String toString() {
        String str = "";
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                str += values[x*3 + y] + " ";
            }
            str += "\n";
        }
        return str;
    }
    
    /**
     * Multiply this matrix by the given matrix.
     * @param mat
     * @return
     */
    public Matrix3 multiply(Matrix3 mat) {
        float[] r = new float[9];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                r[x*3 + y] = getRow(y).dotp(mat.getColumn(x));
            }
        }
        return new Matrix3(r);
    }
    
    /**
     * Multiply this matrix by the given (column) vector.
     * @param mat
     * @return
     */
    public Vector3 multiply(Vector3 v) {
        float[] r = new float[3];
        for (int y = 0; y < 3; y++) {
            r[y] = getRow(y).dotp(v);
        }
        return new Vector3(r);
    }
    
    private Vector3 getColumn(int x) {
        float[] vec = new float[3];
        for (int y = 0; y < 3; y++) {
            vec[y] = values[x*3 + y];
        }
        return new Vector3(vec);
    }
    
    private Vector3 getRow(int y) {
        float[] vec = new float[3];
        for (int x = 0; x < 3; x++) {
            vec[x] = values[x*3 + y];
        }
        return new Vector3(vec);
    }

    /**
     * Get the values stored in this matrix in column-major order.
     * @return
     */
    public float[] getValues() {
        return Arrays.copyOf(values, 9);
    }

}
