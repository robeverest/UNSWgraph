package unsw.graphics.examples;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

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

public class MinificationExample extends Application3D implements KeyListener {

    private static final int IMAGE_SIZE = 64;
    
    private float d;
    
    private ByteBuffer chessImageBuf = Buffers.newDirectByteBuffer(IMAGE_SIZE*IMAGE_SIZE*4);

    private Texture textureGrass;
    private Texture textureSky;
    
    private TriangleMesh grass;
    private TriangleMesh sky;

    private Shader shader;

    public MinificationExample() {
        super("Minification Example", 800, 800);
        setBackground(new Color(0.8f, 0.8f, 0.8f));
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
        MinificationExample s = new MinificationExample();
        s.start();
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);

        getWindow().addKeyListener(this);

        // Generate procedural texture.
        createChessboard();

        // Load procedural textures
//        textureGrass = new Texture(gl, chessImageBuf, IMAGE_SIZE, false);
        textureGrass = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
        textureSky = new Texture(gl, "res/textures/sky.bmp", "bmp", true);

        shader = new Shader(gl, "shaders/vertex_tex_3d.glsl",
                "shaders/fragment_tex_3d.glsl");
        shader.use(gl);
        
        //Build the meshes
        List<Point3D> grassVerts = new ArrayList<>();
        grassVerts.add(new Point3D(-100, 0, 100));
        grassVerts.add(new Point3D(100, 0, 100));
        grassVerts.add(new Point3D(100, 0, -100));
        grassVerts.add(new Point3D(-100, 0, -100));
        
        List<Point2D> grassTexCoords = new ArrayList<>();
        grassTexCoords.add(new Point2D(0, 0));
        grassTexCoords.add(new Point2D(8, 0));
        grassTexCoords.add(new Point2D(8, 8));
        grassTexCoords.add(new Point2D(0, 8));
        
        List<Integer> grassIndices = Arrays.asList(0,1,2, 0,2,3);
        
        grass = new TriangleMesh(grassVerts, grassIndices, false, grassTexCoords);
        grass.init(gl);
        
        List<Point3D> skyVerts = new ArrayList<>();
        skyVerts.add(new Point3D(-100, 0, -70));
        skyVerts.add(new Point3D(100, 0, -70));
        skyVerts.add(new Point3D(100, 120, -70));
        skyVerts.add(new Point3D(-100, 120, -70));
        
        List<Point2D> skyTexCoords = new ArrayList<>();
        skyTexCoords.add(new Point2D(0, 0));
        skyTexCoords.add(new Point2D(1, 0));
        skyTexCoords.add(new Point2D(1, 1));
        skyTexCoords.add(new Point2D(0, 1));
        
        List<Integer> skyIndices = Arrays.asList(0,1,2, 0,2,3);
        
        sky = new TriangleMesh(skyVerts, skyIndices, false, skyTexCoords);
        sky.init(gl);
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);
        
        Shader.setPenColor(gl, Color.WHITE);
        
        Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);

        CoordFrame3D viewFrame = CoordFrame3D.identity()
                .translate(0, -10, -15 - d);
        Shader.setViewMatrix(gl, viewFrame.getMatrix());

        gl.glBindTexture(GL.GL_TEXTURE_2D, textureGrass.getId());
        grass.draw(gl);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureSky.getId());
        sky.draw(gl);
    }

    @Override
    public void destroy(GL3 gl) {
        super.destroy(gl);
        shader.destroy(gl);
        textureGrass.destroy(gl);
        textureSky.destroy(gl);
    }

    @Override
    public void reshape(GL3 gl, int w, int h) {
        Shader.setProjMatrix(gl, Matrix4.frustum(-5, 5, -5, 5, 5, 200));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        
        case KeyEvent.VK_UP:
            if (d > -50.0) d -= 0.2;
          
            break;
        case KeyEvent.VK_DOWN:
            if (d < 200) d += 0.2;
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
