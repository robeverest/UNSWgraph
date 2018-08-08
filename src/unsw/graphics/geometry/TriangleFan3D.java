/**
 * 
 */
package unsw.graphics.geometry;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;

/**
 * A triangle fan in 3D
 * 
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class TriangleFan3D {
    private List<Point3D> points;

    public TriangleFan3D(List<Point3D> points) {
        this.points = new ArrayList<Point3D>(points);
    }
    
    /**
     * Construct a triangle fan with the given values representing the vertices.
     * 
     * Argument 3*i, 3*i+1 and 3*i+2 form vertex i on the . e.g.
     * 
     * <code>new TriangleFan3D(0,0,0, 1,0,0, 1,1,0, -1,1,0);</code>
     * 
     * creates a triangle fan with vertices (0,0,0), (1,0,0), (1,1,0), (-1,1,0).
     * 
     * @param values
     */
    public TriangleFan3D(float... values) {
        if (values.length % 3 != 0)
            throw new IllegalArgumentException("Odd number of arguments");
        List<Point3D> points = new ArrayList<Point3D>();
        for (int i = 0; i < values.length / 3; i++) {
            points.add(new Point3D(values[3*i], values[3*i + 1], values[3*i + 2]));
        }
        this.points = points;
    }

    /**
     * Draw the triangle fan in the given coordinate frame.
     * @param gl
     */
    public void draw(GL3 gl, CoordFrame3D frame) {
        Point3DBuffer buffer = new Point3DBuffer(points);

        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, points.size() * 3 * Float.BYTES,
                buffer.getBuffer(), GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, points.size());

        gl.glDeleteBuffers(1, names, 0);
    }
    
    /**
     * Draw the triangle fan on the canvas.
     * @param gl
     */
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }

}
