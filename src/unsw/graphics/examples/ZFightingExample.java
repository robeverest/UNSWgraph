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
 * A simple example to test out drawing in 3D.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class ZFightingExample extends Application3D implements MouseListener {

    private static final float ROTATION_SCALE = 0.5f;
    
    private int rotateX, rotateY;
    
    private int mouseX, mouseY;

    public ZFightingExample() {
        super("Z-fighting", 600, 600);
        rotateX = 0;
        rotateY = 0;
    }
    
    @Override
    public void init(GL3 gl) {
        super.init(gl);
        getWindow().addMouseListener(this);
    }


    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, 1, 1, 10));
    }

    public static void main(String[] args) {
        ZFightingExample example = new ZFightingExample();
        example.start();
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        
        CoordFrame3D frame = CoordFrame3D.identity()
                .rotateX(rotateX)
                .rotateY(rotateY);
        TriangleFan3D tri1 = new TriangleFan3D(-1.5f,-2,-2, 0.5f,-2,-2, 0.5f,2,-2, -1.5f,2,-2);
        TriangleFan3D tri2 = new TriangleFan3D(-0.5f,-2,-2, 1.5f,-2,-2, 1.5f,2,-2, -0.5f,2,-2);
       
        Shader.setPenColor(gl, Color.BLUE);
        //enable polygon offset for filled polygons       
        gl.glEnable(GL3.GL_POLYGON_OFFSET_FILL);
        //push this polygon to the front a little
        gl.glPolygonOffset(-1,-1); 
        //push to the back a little
        //gl.glPolygonOffset(1,1);
        tri1.draw(gl, frame);
        
        //If you do not turn this off again it will not work!
        gl.glDisable(GL3.GL_POLYGON_OFFSET_FILL);
        
        Shader.setPenColor(gl, Color.GREEN);
        tri2.draw(gl, frame);
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
