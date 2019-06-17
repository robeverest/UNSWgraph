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
 * A triangle in 3D space
 *
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Triangle3D {
    private List<Point3D> points;
    
    public Triangle3D(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2) {
        List<Point3D> points = new ArrayList<Point3D>();
        points.add(new Point3D(x0, y0, z0));
        points.add(new Point3D(x1, y1, z1));
        points.add(new Point3D(x2, y2, z2));
        this.points = points;
    }

    public void draw(GL3 gl, CoordFrame3D frame) {        
        Point3DBuffer buffer = new Point3DBuffer(points);

        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, points.size() * 3 * Float.BYTES,
                buffer.getBuffer(), GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, points.size());

        gl.glDeleteBuffers(1, names, 0);
    }
    
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
}
