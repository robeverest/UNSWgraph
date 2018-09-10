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
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;

/**
 * A simple example that draws a textured cube using indexing.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class TexturedCube extends Application3D {

    private float rotationY;
    private Point3DBuffer vertexBuffer;
    private Point2DBuffer texCoordBuffer;
    private IntBuffer indicesBuffer;
    private int verticesName;
    private int texCoordsName;
    private int indicesName;
    
    private Texture texture;

    public TexturedCube() {
        super("Texture Cube", 600, 600);
        rotationY = 0;
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, 1, 1, 10));
    }

    public static void main(String[] args) {
        TexturedCube example = new TexturedCube();
        example.start();
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);
        
        Shader.setInt(gl, "tex", 0);
        
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        
        Shader.setPenColor(gl, Color.WHITE);
       
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
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
        gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);
        
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
                new Point3D(-1,1,1),
                new Point3D(1,-1,1), 
                new Point3D(1,1,1),
                new Point3D(1,-1,-1), 
                new Point3D(1,1,-1),
                new Point3D(-1,-1,-1), 
                new Point3D(-1,1,-1),
                new Point3D(-1,-1,1),  //Repeating the starting vertices 
                new Point3D(-1,1,1))); // as they have their own tex coords 
        
        texCoordBuffer = new Point2DBuffer(Arrays.asList(
                new Point2D(0,0),
                new Point2D(0,1f),
                new Point2D(0.25f,0),
                new Point2D(0.25f,1f),
                new Point2D(0.5f,0),
                new Point2D(0.5f,1f),
                new Point2D(0.75f,0),
                new Point2D(0.75f,1f),
                new Point2D(1,0),
                new Point2D(1,1f)));
        
        indicesBuffer = GLBuffers.newDirectIntBuffer(new int[] {
            0,2,1,
            1,2,3,
            2,4,3,
            3,4,5,
            4,6,5,
            5,6,7,
            6,8,7,
            7,8,9,
        });

        int[] names = new int[3];
        gl.glGenBuffers(3, names, 0);
        
        verticesName = names[0];
        texCoordsName = names[1];
        indicesName = names[2];
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 3 * Float.BYTES,
                vertexBuffer.getBuffer(), GL.GL_STATIC_DRAW);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, texCoordBuffer.capacity() * 2 * Float.BYTES,
                texCoordBuffer.getBuffer(), GL.GL_STATIC_DRAW);
       
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * Integer.BYTES,
                indicesBuffer, GL.GL_STATIC_DRAW);
        
        Shader shader = new Shader(gl, "shaders/vertex_tex_3d.glsl", "shaders/fragment_tex_3d.glsl");
        shader.use(gl);
        
        texture = new Texture(gl, "res/textures/canLabel.bmp", "bmp", false);
    }
    
    @Override
    public void destroy(GL3 gl) {
        super.destroy(gl);
        gl.glDeleteBuffers(3, new int[] { indicesName, verticesName, texCoordsName }, 0);
    }
}