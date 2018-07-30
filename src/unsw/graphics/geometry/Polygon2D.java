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
 * A convex polygon in 2D space.
 * 
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Polygon2D {
    private List<Point2D> points;

    public Polygon2D(List<Point2D> points) {
        this.points = new ArrayList<Point2D>(points);
    }
    
    /**
     * Construct a polygon with the given values representing the vertices.
     * 
     * Argument 2*i and 2*i+1 form vertex i on the polygon. e.g.
     * 
     * <code>new LineStrip2D(0,0, 1,0, 1,1, -1,1);</code>
     * 
     * creates a polygon with vertices (0,0), (1,0), (1,1), (-1,1).
     * 
     * @param values
     */
    public Polygon2D(float... values) {
        if (values.length % 2 != 0)
            throw new IllegalArgumentException("Odd number of arguments");
        List<Point2D> points = new ArrayList<Point2D>();
        for (int i = 0; i < values.length / 2; i++) {
            points.add(new Point2D(values[2*i], values[2*i + 1]));
        }
        this.points = points;
    }

    /**
     * Draw the polygon in the given coordinate frame.
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
        gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, points.size());

        gl.glDeleteBuffers(1, names, 0);
    }
    
    public void drawOutline(GL3 gl, CoordFrame2D frame) {
        // TODO: You need to write this method.
        // It should draw an outline of a polygon using GL_LINE_LOOP
    }
    
    /**
     * Draw the polygon on the canvas.
     * @param gl
     */
    public void draw(GL3 gl) {
        draw(gl, CoordFrame2D.identity());
    }

}
