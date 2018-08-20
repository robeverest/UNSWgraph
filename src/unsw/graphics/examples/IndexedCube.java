package unsw.graphics.examples;

import java.awt.Color;
import java.io.IOException;
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
import unsw.graphics.geometry.TriangleMesh;

/**
 * A simple example that draws a cube using indexing.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class IndexedCube extends Application3D {

    private float rotationY;
    private Point3DBuffer vertexBuffer;
    private IntBuffer indicesBuffer;
    private int verticesName;
    private int indicesName;
    private TriangleMesh cube;

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
//        drawCube(gl, frame.rotateY(rotationY));
        
        cube.draw(gl, frame.rotateY(rotationY));

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
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawElements(GL.GL_TRIANGLES, indicesBuffer.capacity(), 
                GL.GL_UNSIGNED_INT, 0);
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);
        vertexBuffer = new Point3DBuffer(Arrays.asList(
                new Point3D(-1,-1,1), 
                new Point3D(1,-1,1), 
                new Point3D(1,1,1),
                new Point3D(-1,1,1),
                new Point3D(-1,-1,-1), 
                new Point3D(1,-1,-1), 
                new Point3D(1,1,-1),
                new Point3D(-1,1,-1)));
        
        indicesBuffer = GLBuffers.newDirectIntBuffer(new int[] {
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
        
        verticesName = names[0];
        indicesName = names[1];
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 3 * Float.BYTES,
                vertexBuffer.getBuffer(), GL.GL_STATIC_DRAW);
       
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * Integer.BYTES,
                indicesBuffer, GL.GL_STATIC_DRAW);
        
        try {
            cube = new TriangleMesh("res/models/cube.ply");
            cube.init(gl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void destroy(GL3 gl) {
        super.destroy(gl);
        gl.glDeleteBuffers(2, new int[] { indicesName, verticesName }, 0);
        cube.destroy(gl);
    }
}