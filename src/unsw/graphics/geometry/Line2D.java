/**
 * 
 */
package unsw.graphics.geometry;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Shader;

/**
 * A line in 2D space.
 * 
 * This class is immutable.
 * @author Robert Clifton-Everest
 *
 */
public class Line2D {
    private Point2D start, end;

    /** 
     * Construct a line from 'start' to 'end'
     * @param start
     * @param end
     */
    public Line2D(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
    }
    
    /**
     * Construct a line from (x0,y0) to (x1,y1)
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     */
    public Line2D(float x0, float y0, float x1, float y1) {
        this(new Point2D(x0, y0), new Point2D(x1, y1));
    }

    public Point2D getStart() {
        return start;
    }

    public Point2D getEnd() {
        return end;
    }
    
    /**
     * Draw the line in the given coordinate frame.
     * @param gl
     */
    public void draw(GL3 gl, CoordFrame2D frame) {
        Point2DBuffer buffer = new Point2DBuffer(2);
        buffer.put(0, start);
        buffer.put(1, end);
        
        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, 2*2*Float.BYTES, buffer.getBuffer(), GL.GL_STATIC_DRAW);
        
        gl.glVertexAttribPointer(Shader.POSITION, 2, GL.GL_FLOAT, false, 0, 0);
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawArrays(GL.GL_LINES, 0, 2);
        
        gl.glDeleteBuffers(1, names, 0);
    }
    
    /**
     * Draw the line on the canvas.
     * @param gl
     */
    public void draw(GL3 gl) {
        draw(gl, CoordFrame2D.identity());
    }
}
