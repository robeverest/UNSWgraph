package unsw.graphics.examples;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Line3D;
import unsw.graphics.geometry.Triangle3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * This example shows off different forms of anti-aliasing.
 * @author Robert Clifton-Everest
 *
 */
public class AntiAndMulti extends Application3D implements KeyListener {

    // Rotation of scene.
    private float Xangle = 0, Yangle = 0, Zangle = 0;
    
    private TriangleMesh cube;

    public AntiAndMulti() {
        super("Antialising example", 800, 800);
        try {
            cube = new TriangleMesh("res/models/cube.ply");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AntiAndMulti example = new AntiAndMulti();
        example.start();
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);
        // Antialiasing control
//        gl.glEnable(GL3.GL_LINE_SMOOTH); // Enable line antialiasing.
//        gl.glHint(GL3.GL_LINE_SMOOTH_HINT, GL3.GL_NICEST);

        // Multisampling control.
        gl.glEnable(GL3.GL_MULTISAMPLE); // Enable multisampling.

        int numSamples[] = new int[1]; // Number of sample buffers.
        gl.glGetIntegerv(GL3.GL_SAMPLES, numSamples, 0);
        System.out.println("Num samples " + numSamples[0]);

        // Move the scene
        CoordFrame3D frame = CoordFrame3D.identity().translate(0, 0, -15)
                .rotateZ(Zangle).rotateY(Yangle).rotateX(Xangle);

        // Draw a line segment.
        Shader.setPenColor(gl, Color.RED);
        Line3D line = new Line3D(-10, 0, 0, 10, 0, 0);
        line.draw(gl, frame);

         Shader.setPenColor(gl, Color.BLACK);
         // Draw a wire cube reference frame.
         gl.glDisable(GL.GL_CULL_FACE);
         gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_LINE);
         
         cube.draw(gl, frame.scale(3.5f, 3.5f, 3.5f));
                  
         gl.glEnable(GL.GL_CULL_FACE);
         gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_FILL);
        
         // Draw two adjacent triangles.
         Shader.setPenColor(gl,Color.BLUE);
         Triangle3D tri1 = new Triangle3D(6,1,0, 12,1,0, 6,8,0);
         tri1.draw(gl, frame);
         Shader.setPenColor(gl,Color.YELLOW);
         Triangle3D tri2 = new Triangle3D(12,8,0, 6,8,0, 12,1,0);
         tri2.draw(gl, frame);
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);
        getWindow().addKeyListener(this);
        cube.init(gl);
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.frustum(-5.0f, 5.0f, -5.0f, 5.0f, 5.0f, 100.0f));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {

        case KeyEvent.VK_X:
            if (e.isShiftDown()) {
                Xangle += 5.0;
                if (Xangle > 360.0)
                    Xangle -= 360.0;
            } else {
                Xangle -= 5.0;
                if (Xangle < 0.0)
                    Xangle += 360.0;
            }
            break;
        case KeyEvent.VK_Y:
            if (e.isShiftDown()) {
                Yangle -= 5.0;
                if (Yangle < 0.0)
                    Yangle += 360.0;
            } else {
                Yangle += 5.0;
                if (Yangle > 360.0)
                    Yangle -= 360.0;
            }
            break;

        case KeyEvent.VK_Z:
            if (e.isShiftDown()) {
                Zangle -= 5.0;
                if (Zangle < 0.0)
                    Zangle += 360.0;
            } else {
                Zangle += 5.0;
                if (Zangle > 360.0)
                    Zangle -= 360.0;
            }
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
