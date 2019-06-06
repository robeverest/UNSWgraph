package unsw.graphics.geometry;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;

/**
 * A point in 2D space.
 * 
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Point2D {
    private float x, y;

    /**
     * Construct a point from the given x and y coordinates.
     * 
     * @param x
     * @param y
     */
    public Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Draw this point as a dot in the given coordinate frame.
     * 
     * This is useful for debugging and for very trivial examples.
     * 
     * @param gl
     * @param frame
     */
    public void draw(GL3 gl, CoordFrame2D frame) {
        Point2DBuffer buffer = new Point2DBuffer(1);
        buffer.put(0, this);
        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, 2 * Float.BYTES, buffer.getBuffer(),
                GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(Shader.POSITION, 2, GL.GL_FLOAT, false, 0, 0);
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawArrays(GL.GL_POINTS, 0, 1);

        gl.glDeleteBuffers(1, names, 0);
    }
    
    /**
     * Draw this point as a dot on the canvas.
     * 
     * This is useful for debugging and for very trivial examples.
     * 
     * @param gl
     */
    public void draw(GL3 gl) {
        draw(gl, CoordFrame2D.identity());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    
    /**
     * Translate the point by the given vector
     * @param dx
     * @param dy
     * @return
     */
    public Point2D translate(float dx, float dy) {
        return new Point2D(x + dx, y + dy);
    }
    
    /**
     * Convert this point to a homogenous coordinate (1 for the z value)
     * 
     * @return
     */
    public Vector3 asHomogenous() {
        return new Vector3(new float[] {x, y, 1});
    }

}
