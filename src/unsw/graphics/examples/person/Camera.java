package unsw.graphics.examples.person;


import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;



/**
 * The camera for the person demo
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class Camera implements KeyListener {

    private Point2D myPos;
    private float myAngle;
    private float myScale;

    public Camera() {
        myPos = new Point2D(0, 0);
        myAngle = 0;
        myScale = 10;
    }
    
    public void draw(GL3 gl, CoordFrame2D frame) {
        CoordFrame2D cameraFrame = frame.translate(myPos)
                .rotate(myAngle)
                .scale(myScale, myScale);

        //Draw the camera
        LineStrip2D camera = new LineStrip2D(1,1, -1,1, -1,-1, 1,-1, 1,1);
        camera.draw(gl, cameraFrame);
    }

    /**
     * Set the view transform
     * 
     * Note: this is the inverse of the model transform above
     * 
     * @param gl
     */
    public void setView(GL3 gl) {
        CoordFrame2D viewFrame = CoordFrame2D.identity()
                .scale(1/myScale, 1/myScale)
                .rotate(-myAngle)
                .translate(-myPos.getX(), -myPos.getY());
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            if (e.isShiftDown())
                myAngle += 5;
            else
                myPos = new Point2D(myPos.getX() - 1, myPos.getY());                
            break;
            
        case KeyEvent.VK_RIGHT:
            if (e.isShiftDown())
                myAngle -= 5;
            else
                myPos = new Point2D(myPos.getX() + 1, myPos.getY());                
            break;

        case KeyEvent.VK_DOWN:
            if (e.isShiftDown())
                myScale *= 1.1;
            else
                myPos = new Point2D(myPos.getX(), myPos.getY() - 1);
            break;

        case KeyEvent.VK_UP:
            if (e.isShiftDown())
                myScale /= 1.1;
            else
                myPos = new Point2D(myPos.getX(), myPos.getY() + 1);
            break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {}

}
