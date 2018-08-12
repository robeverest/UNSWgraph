package unsw.graphics.examples;

import java.awt.Color;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.TriangleFan3D;

/**
 * A simple example that draws a cube.
 * 
 * You can use this to play around with rotations and projection.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class CubeField extends Application3D implements MouseListener{

    private static final float ROTATION_SCALE = 0.5f;
    
    private int rotateX, rotateY;
    
    private int mouseX, mouseY;
    
    public CubeField() {
        super("Cube", 600, 600);
    }
    
    
    @Override
    public void init(GL3 gl) {
        super.init(gl);
        getWindow().addMouseListener(this);
    }


    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, 1, 1, 100));
    }

    public static void main(String[] args) {
        CubeField example = new CubeField();
        example.start();
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        CoordFrame3D frame = CoordFrame3D.identity()
                .rotateX(rotateX)
                .rotateY(rotateY)
                .translate(-7.5f, -1, -3)
                .scale(0.5f, 0.5f, 0.5f);
        for (int i = 0 ; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                CoordFrame3D frame2 = frame.translate(j*3, 0, -i*3);
                drawCube(gl, frame2);
            }
        }
    }

    /**
     * Draw a cube centered around (0,0) with bounds of length 1 in each direction.
     * @param gl
     * @param frame
     */
    private void drawCube(GL3 gl, CoordFrame3D frame) {
        TriangleFan3D face = new TriangleFan3D(-1,-1,1, 1,-1,1, 1,1,1, -1,1,1);
        
        // Front
        Shader.setPenColor(gl, Color.RED);
        face.draw(gl, frame);
        
        // Left
        Shader.setPenColor(gl, Color.BLUE);
        face.draw(gl, frame.rotateY(-90));
        
        // Right
        Shader.setPenColor(gl, Color.GREEN);
        face.draw(gl, frame.rotateY(90));
        
        // Back
        Shader.setPenColor(gl, Color.CYAN);
        face.draw(gl, frame.rotateY(180));
        
        // Bottom
        Shader.setPenColor(gl, Color.YELLOW);
        face.draw(gl, frame.rotateX(-90));
        
        // Top
        Shader.setPenColor(gl, Color.MAGENTA);
        face.draw(gl, frame.rotateX(90));
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - mouseX;
        int dy = e.getY() - mouseY;

        // Note: dragging in the x dir rotates about y
        //       dragging in the y dir rotates about x
        rotateY += dx * ROTATION_SCALE;
        rotateX += dy * ROTATION_SCALE;

    
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent arg0) { }

    @Override
    public void mouseEntered(MouseEvent arg0) { }

    @Override
    public void mouseExited(MouseEvent arg0) { }

    @Override
    public void mousePressed(MouseEvent arg0) { }

    @Override
    public void mouseReleased(MouseEvent arg0) { }

    @Override
    public void mouseWheelMoved(MouseEvent arg0) { }

}