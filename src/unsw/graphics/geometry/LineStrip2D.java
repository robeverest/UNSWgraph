/**
 * 
 */
package unsw.graphics.geometry;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

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
    
    public LineStrip2D(List<Point2D> points) {
        points = new ArrayList<Point2D>(points);
    }

    public void draw(GL3 gl) {
        Point2DBuffer buffer = new Point2DBuffer(points);

        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, points.size() * 2 * Float.BYTES,
                buffer.getBuffer(), GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(Shader.POSITION, 2, GL.GL_FLOAT, false, 0, 0);
        gl.glDrawArrays(GL.GL_LINE_STRIP, 0, points.size());

        gl.glDeleteBuffers(1, names, 0);
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
