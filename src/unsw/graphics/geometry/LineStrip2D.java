/**
 * 
 */
package unsw.graphics.geometry;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Shader;

/**
 * A line strip in 2D space.
 * 
 * In a line strip, a series of points are joined with a line between each
 * adjacent pair.
 * 
 * This class is mutable, as new points can be added to the end of the strip.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class LineStrip2D {
    private List<Point2D> points;

    public LineStrip2D() {
        points = new ArrayList<Point2D>();
    }
    
    /**
     * Construct a line strip with the given points.
     * @param points
     */
    public LineStrip2D(List<Point2D> points) {
        this.points = new ArrayList<Point2D>(points);
    }
    
    /**
     * Construct a line strip with the given values representing the points.
     * 
     * Argument 2*i and 2*i+1 form point i on the strip. e.g.
     * 
     * <code>new LineStrip2D(0,0, 1,0, 1,1);</code>
     * 
     * creates a line strip going from (0,0) to (1,0) to (1,1).
     * 
     * @param values
     */
    public LineStrip2D(float... values) {
        if (values.length % 2 != 0)
            throw new IllegalArgumentException("Odd number of arguments");
        List<Point2D> points = new ArrayList<Point2D>();
        for (int i = 0; i < values.length / 2; i++) {
            points.add(new Point2D(values[2*i], values[2*i + 1]));
        }
        this.points = points;
    }

    /**
     * Draw the line strip in the given coordinate frame.
     * @param gl
     */
    public void draw(GL3 gl, CoordFrame2D frame) {
        Point2DBuffer buffer = new Point2DBuffer(points);

        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, points.size() * 2 * Float.BYTES,
                buffer.getBuffer(), GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(Shader.POSITION, 2, GL.GL_FLOAT, false, 0, 0);
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawArrays(GL.GL_LINE_STRIP, 0, points.size());

        gl.glDeleteBuffers(1, names, 0);
    }
    
    /**
     * Draw the line on the canvas.
     * @param gl
     */
    public void draw(GL3 gl) {
        draw(gl, CoordFrame2D.identity());
    }

    public void add(Point2D p) {
        points.add(p);
    }

    public Point2D getLast() {
        return points.get(points.size() - 1);
    }

    public List<Point2D> getPoints() {
        return points;
    }
}
