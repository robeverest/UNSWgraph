package unsw.graphics.examples;

import java.awt.Color;
import java.nio.IntBuffer;
import java.util.Arrays;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point3D;

/**
 * A simple example that draws a cube using indexing.
 *
 * @author Robert Clifton-Everest
 *
 */
public class IndexedCube extends Application3D {

    private float rotationY;

    public IndexedCube() {
        super("Cube", 600, 600);
        rotationY = 0;
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, 1, 1, 10));
    }

    public static void main(String[] args) {
        IndexedCube example = new IndexedCube();
        example.start();
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);
        Shader.setPenColor(gl, Color.MAGENTA);
        CoordFrame3D frame = CoordFrame3D.identity()
                .translate(0, 0, -2)
                .scale(0.5f, 0.5f, 0.5f);
        drawCube(gl, frame.rotateY(rotationY));

        rotationY += 1;
    }

    /**
     * Draw a cube centered around (0,0) with bounds of length 1 in each
     * direction.
     *
     * @param gl
     * @param frame
     */
    private void drawCube(GL3 gl, CoordFrame3D frame) {
        Point3DBuffer vertexBuffer = new Point3DBuffer(Arrays.asList(
                new Point3D(-1,-1,1),
                new Point3D(1,-1,1),
                new Point3D(1,1,1),
                new Point3D(-1,1,1),
                new Point3D(-1,-1,-1),
                new Point3D(1,-1,-1),
                new Point3D(1,1,-1),
                new Point3D(-1,1,-1)));

        IntBuffer indicesBuffer = GLBuffers.newDirectIntBuffer(new int[] {
            0,1,2,
            2,3,0,
            1,5,6,
            6,2,1,
            5,4,7,
            7,6,5,
            4,0,3,
            3,7,4,
            3,2,6,
            6,7,3,
            4,5,1,
            1,0,4
        });

        int[] names = new int[2];
        gl.glGenBuffers(2, names, 0);

        int verticesName = names[0];
        int indicesName = names[1];

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 3 * Float.BYTES,
                vertexBuffer.getBuffer(), GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * Integer.BYTES,
                indicesBuffer, GL.GL_STATIC_DRAW);

        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawElements(GL.GL_TRIANGLES, indicesBuffer.capacity(),
                GL.GL_UNSIGNED_INT, 0);
        gl.glDeleteBuffers(2, names, 0);
    }
}