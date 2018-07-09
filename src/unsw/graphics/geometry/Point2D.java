package unsw.graphics.geometry;

import java.nio.IntBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.Point2DBuffer;

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
     * @param x
     * @param y
     */
    public Point2D(float x, float y) {
        super();
        this.x = x;
        this.y = y;
    }
    
    /**
     * Draw this point as a dot on the canvas.
     * 
     * This is useful for debugging and for very trivial examples.
     * @param gl
     */
    public void draw(GL3 gl) {
        Point2DBuffer buffer = new Point2DBuffer(1);
        buffer.put(0, this);
        IntBuffer bufIds = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, bufIds);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufIds.get(0));
        
        gl.glBufferData(GL.GL_ARRAY_BUFFER, 2*Float.BYTES, buffer.getBuffer(), GL.GL_STATIC_DRAW);
        
        gl.glVertexAttribPointer(0, 2, GL.GL_FLOAT, false, 0, 0);
        gl.glDrawArrays(GL.GL_POINTS, 0, 1);
        
        gl.glDeleteBuffers(1, bufIds);
        
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}
