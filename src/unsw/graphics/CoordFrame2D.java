/**
 * 
 */
package unsw.graphics;

import java.awt.Color;

import com.jogamp.opengl.GL3;

import unsw.graphics.geometry.Line2D;
import unsw.graphics.geometry.Point2D;

/**
 * A coordinate frame in 2D.
 * 
 * Coordinate frames allow for geometry to be transformed before being drawn to the canvas.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class CoordFrame2D {
    private Matrix3 matrix;
    
    /**
     * Use the given 3X3 matrix as a coordinate frame.
     * 
     * @param matrix
     */
    public CoordFrame2D(Matrix3 matrix) {
        this.matrix = matrix;
    }
    
    /**
     * The identity coordinate frame has an i-axis of (1,0), a j-axis of (0,1) and an origin at 
     * (0,0)
     * @return
     */
    public static CoordFrame2D identity() {
        return new CoordFrame2D(Matrix3.identity());
    }
    
    /**
     * A new coordinate frame that is the translation of this frame along the given vector.
     * @param x
     * @param y
     * @return
     */
    public CoordFrame2D translate(float x, float y) {
        return new CoordFrame2D(matrix.multiply(Matrix3.translation(x, y)));
    }
    
    /**
     * Treating the argument as a displacement from this coordinate frame, returns a new coordinate 
     * frame that is a translation of this frame along that displacement.
     * @param point
     * @return
     */
    public CoordFrame2D translate(Point2D point) {
        return new CoordFrame2D(matrix.multiply(Matrix3.translation(point)));
    }
    
    /**
     * A new coordinate frame that is a rotation of this frame by the given degrees.
     * @param degrees
     * @return
     */
    public CoordFrame2D rotate(float degrees) {
        return new CoordFrame2D(matrix.multiply(Matrix3.rotation(degrees)));
    }

    /**
     * A new coordinate frame that is this frame scaled by the given amount in the x and y 
     * directions.
     * @param x
     * @param y
     * @return
     */
    public CoordFrame2D scale(float x, float y) {
        return new CoordFrame2D(matrix.multiply(Matrix3.scale(x,y)));
    }

    /**
     * A new coordinate frame that is this frame sheared by the given amount in the horizontal
     * direction.
     * @param h
     * @return
     */
    public CoordFrame2D horizontalShear(float h) {
        return new CoordFrame2D(matrix.multiply(Matrix3.horizontalShear(h)));
    }

    /**
     * A new coordinate frame that is this frame sheared by the given amount in the vertical
     * direction.
     * @param v
     * @return
     */
    public CoordFrame2D verticalShear(float v) {
        return new CoordFrame2D(matrix.multiply(Matrix3.horizontalShear(v)));
    }

    /**
     * Get the matrix representation of this coordinate frame.
     * @return
     */
    public Matrix3 getMatrix() {
        return matrix;
    }
    
    /**
     * Draw the coordinate frame on the canvas.
     * 
     * This method is useful for debugging.
     * 
     * @param gl
     */
    public void draw(GL3 gl) {
       Line2D iAxis = new Line2D(0f,0f, 1f,0f); 
       Line2D jAxis = new Line2D(0f,0f, 0f,1f);
       Shader.setPenColor(gl, Color.RED);
       iAxis.draw(gl,this);
       Shader.setPenColor(gl, Color.GREEN);
       jAxis.draw(gl,this);
       Shader.setPenColor(gl, Color.BLACK);
    }
}
