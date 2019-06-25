package unsw.graphics.geometry;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.Vector4;

/**
 * A point in 3D space.
 * 
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Point3D {
    private float x, y, z;

    /**
     * Construct a point from the given x, y, and z coordinates.
     * 
     * @param x
     * @param y
     * @param z
     */
    public Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Draw this point as a dot in the given coordinate frame.
     * 
     * This is useful for debugging and for very trivial examples.
     * 
     * @param gl
     * @param frame
     */
    public void draw(GL3 gl, CoordFrame3D frame) {
        Point3DBuffer buffer = new Point3DBuffer(1);
        buffer.put(0, this);
        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, 3 * Float.BYTES, buffer.getBuffer(),
                GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
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
        draw(gl, CoordFrame3D.identity());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    
    public float getZ() {
        return z;
    }
    
    /**
     * Translate the point by the given vector
     * @param v
     * @return
     */
    public Point3D translate(Vector3 v) {
        return translate(v.getX(), v.getY(), v.getZ());
    }
    
    /**
     * Translate the point by the given vector
     * @param dx
     * @param dy
     * @param dz
     * @return
     */
    public Point3D translate(float dx, float dy, float dz) {
        return new Point3D(x + dx, y + dy, z + dz);
    }
    
    /**
     * Convert this point to a homogenous coordinate (1 for the w value)
     * 
     * @return
     */
    public Vector4 asHomogenous() {
        return new Vector4(new float[] {x, y, z, 1});
    }

    /**
     * Subtract the given point from this point, yielding a vector.
     * @param p
     * @return
     */
    public Vector3 minus(Point3D p) {
        return asHomogenous().minus(p.asHomogenous()).trim();
    }

}
