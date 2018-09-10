package unsw.graphics.examples;

import java.awt.Color;
import java.nio.ByteBuffer;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.Application3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;

/**
 * This program that allows the user to press the space bar and cycle through
 * different textures Shows how to map texture co-ordinates to a simple
 * rectangle. Two procedures are loaded from files, two are generated
 * procedurally
 * 
 * @author Angela
 * @author Robert Clifton-Everest
 *
 */

public class SimpleTextureExample extends Application3D implements KeyListener {

    private static final int NUM_TEXTURES = 4;
    private static final int IMAGE_SIZE = 64;

    // Buffers for the procedural textures
    private ByteBuffer chessImageBuf = GLBuffers
            .newDirectByteBuffer(IMAGE_SIZE * IMAGE_SIZE * 4);
    private ByteBuffer randomImageBuf = GLBuffers
            .newDirectByteBuffer(IMAGE_SIZE * IMAGE_SIZE * 4);
    private String textureFileName1 = "res/textures/kittens.jpg";
    private String textureFileName2 = "res/textures/canLabel.bmp";
    private String textureExt1 = "jpg";
    private String textureExt2 = "bmp";
    private int currIndex = 0; // Currently displayed texture index

    private Texture myTextures[];

    private Shader shader;

    public SimpleTextureExample() {
        super("Simple Texture Example", 800, 800);
        setBackground(new Color(0.8f, 0.8f, 0.8f));
    }

    // Creates a random Texture. Each pixel has random R,G,B value
    // And an alpha value of 255. Pixels values go from 0..255 (not 0..1 like
    // opengl settings)
    private void createRandomTex() {

        int i, j;
        for (i = 0; i < IMAGE_SIZE; i++) {
            for (j = 0; j < IMAGE_SIZE; j++) {

                randomImageBuf.put((byte) (255 * Math.random())); // R
                randomImageBuf.put((byte) (255 * Math.random())); // G
                randomImageBuf.put((byte) (255 * Math.random())); // B
                randomImageBuf.put((byte) 0xFF); // A
            }
        }
        randomImageBuf.rewind();
    }

    // Create 64 x 64 RGBA image of a chessboard.
    private void createChessboard() {
        int i, j;
        for (i = 0; i < IMAGE_SIZE; i++)
            for (j = 0; j < IMAGE_SIZE; j++)
                if ((((i / 8) % 2 == 1) && ((j / 8) % 2 == 1))
                        || (!((i / 8) % 2 == 1) && !((j / 8) % 2 == 1))) {

                    chessImageBuf.put((byte) 0x00); // R
                    chessImageBuf.put((byte) 0x00); // G
                    chessImageBuf.put((byte) 0x00); // B
                    chessImageBuf.put((byte) 0xFF); // A
                } else {

                    chessImageBuf.put((byte) 0xFF); // R
                    chessImageBuf.put((byte) 0xFF); // G
                    chessImageBuf.put((byte) 0xFF); // B
                    chessImageBuf.put((byte) 0xFF); // A
                }
        chessImageBuf.rewind();
    }

    public static void main(String[] args) {
        SimpleTextureExample s = new SimpleTextureExample();
        s.start();
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);

        getWindow().addKeyListener(this);

        // Load in textures from files
        myTextures = new Texture[NUM_TEXTURES];
        myTextures[0] = new Texture(gl, textureFileName1, textureExt1, false);
        myTextures[1] = new Texture(gl, textureFileName2, textureExt2, false);

        // Generate procedural texture.
        createChessboard();
        createRandomTex();

        // Load procedural textures
        myTextures[2] = new Texture(gl, chessImageBuf, IMAGE_SIZE, false);
        myTextures[3] = new Texture(gl, randomImageBuf, IMAGE_SIZE, false);

        shader = new Shader(gl, "shaders/vertex_tex_3d.glsl",
                "shaders/fragment_tex_3d.glsl");
        shader.use(gl);
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);

        Shader.setInt(gl, "tex", 0); // tex in the shader is the 0'th active texture

        gl.glActiveTexture(GL.GL_TEXTURE0); // All future texture operations are 
                                            // for the 0'th active texture
        gl.glBindTexture(GL.GL_TEXTURE_2D,
                myTextures[currIndex].getId()); // Bind the texture id of the 
                                                // texture we want to the 0th 
                                                // active texture

        Shader.setViewMatrix(gl, Matrix4.translation(0, 0, -20));

//         float[] color = { 0, 0, 0, 1};

//        gl.glTexParameterfv(GL.GL_TEXTURE_2D, GL3.GL_TEXTURE_BORDER_COLOR,
//                color, 0);

        // Set wrap mode for texture in S direction
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
                GL.GL_MIRRORED_REPEAT);
        // Set wrap mode for texture in T direction
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
                GL3.GL_MIRRORED_REPEAT);

        int[] names = new int[4];
        gl.glGenBuffers(4, names, 0);

        // A green quad drawn as a triangle fan
        Shader.setPenColor(gl, Color.WHITE);
        Point3DBuffer quad = new Point3DBuffer(4);
        quad.put(0, -10, 0, 0);
        quad.put(1, 10, 0, 0);
        quad.put(2, 10, 10, 0);
        quad.put(3, -10, 10, 0);

        Point2DBuffer quadTexCoords = new Point2DBuffer(4);
        quadTexCoords.put(0, 0f, 0f);
        quadTexCoords.put(1, 1f, 0f);
        quadTexCoords.put(2, 1f, 1f);
        quadTexCoords.put(3, 0f, 1f);

        // Copy across the buffer for the vertex positions
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, quad.capacity() * 3 * Float.BYTES,
                quad.getBuffer(), GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);

        // Copy across the buffer for the texture coordinates
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[1]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                quadTexCoords.capacity() * 2 * Float.BYTES,
                quadTexCoords.getBuffer(), GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);

        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, quad.capacity());

        // A red triangle
        Shader.setPenColor(gl, Color.RED);
        Point3DBuffer tri = new Point3DBuffer(3);
        tri.put(0, -10, -10, 0);
        tri.put(1, 10, -10, 0);
        tri.put(2, 0, -5, 0);

        Point2DBuffer triTexCoords = new Point2DBuffer(3);
        triTexCoords.put(0, 0, 0);
        triTexCoords.put(1, 1, 0);
        triTexCoords.put(2, 0.5f, 1);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[2]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, tri.capacity() * 3 * Float.BYTES,
                tri.getBuffer(), GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[3]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                triTexCoords.capacity() * 2 * Float.BYTES,
                triTexCoords.getBuffer(), GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);

        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, tri.capacity());

        gl.glDeleteBuffers(4, names, 0);
    }

    @Override
    public void destroy(GL3 gl) {
        super.destroy(gl);
        shader.destroy(gl);
        for (Texture t : myTextures)
            t.destroy(gl);
    }

    @Override
    public void reshape(GL3 gl, int w, int h) {
        Shader.setProjMatrix(gl, Matrix4.frustum(-3, 3, -3, 3, 5, 100));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        switch (e.getKeyCode()) {
        case KeyEvent.VK_SPACE:
            currIndex++;
            currIndex = currIndex % NUM_TEXTURES;
            break;

        default:
            break;
        }

    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

}
