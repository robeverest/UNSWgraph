package unsw.graphics.examples.person;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.geometry.Line2D;
import unsw.graphics.geometry.Point2D;


/**
 * The person in the person demo.
 * 
 * Draws the person and allows for manipulation as a key listener.
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class Person implements KeyListener { 
    
    static private float TORSO_LENGTH = 12;
    private static final float SHOULDER_HEIGHT = TORSO_LENGTH * 3 / 4;

    private Point2D myPosition;
    private float myRotation;
    private float myScale;

    private Arm myLeftArm;
    private Arm myRightArm;
    private Leg myLeftLeg;
    private Leg myRightLeg;
    private Head myHead;

    public Person(float x, float y, float rotation, float scale) {
        
        myPosition = new Point2D(x,y);
        myRotation = rotation;
        myScale = scale;
        
        myLeftArm = new Arm(true);
        myRightArm = new Arm(false);
        myLeftLeg = new Leg(true);
        myRightLeg = new Leg(false);
        myHead = new Head();
    }

    public void draw(GL3 gl, CoordFrame2D frame) {
        
        // Compute the coordinate frame for the person
        CoordFrame2D personFrame = frame
                .translate(myPosition)
                .rotate(myRotation)
                .scale(myScale, myScale);

            
        // draw torso
        Line2D torso = new Line2D(0,0, 0,TORSO_LENGTH);
        torso.draw(gl,personFrame);

        // draw legs
        myLeftLeg.draw(gl, personFrame);
        myRightLeg.draw(gl, personFrame);
            
        // move coordinates to neck and draw the head
        CoordFrame2D headFrame = personFrame.translate(0, TORSO_LENGTH);
        myHead.draw(gl, headFrame);
            
        // move coordinates to shoulders and draw the arms
        CoordFrame2D shoulderFrame = personFrame.translate(0, SHOULDER_HEIGHT);
        myLeftArm.draw(gl, shoulderFrame);
        myRightArm.draw(gl, shoulderFrame);
    }    
    
    @Override
    public void keyPressed(KeyEvent e) {
        
        switch(e.getKeyCode()) {
        
        case KeyEvent.VK_1:
            myLeftArm.rotateElbow(-5);
            break;
            
        case KeyEvent.VK_2:
            myLeftArm.rotateElbow(5);
            break;
            
        case KeyEvent.VK_Q:
            myLeftArm.rotateShoulder(-5);
            break;
        
        case KeyEvent.VK_W:
            myLeftArm.rotateShoulder(5);
            break;
        
        case KeyEvent.VK_A:
            myLeftLeg.rotateHip(5);
            break;
        
        case KeyEvent.VK_S:
            myLeftLeg.rotateHip(-5);
            break;

        case KeyEvent.VK_Z:
            myLeftLeg.rotateKnee(5);
            break;

        case KeyEvent.VK_X:
            myLeftLeg.rotateKnee(-5);
            break;

        case KeyEvent.VK_9:
            myRightArm.rotateElbow(5);
            break;
            
        case KeyEvent.VK_0:
            myRightArm.rotateElbow(-5);
            break;
            
        case KeyEvent.VK_I:
            myRightArm.rotateShoulder(5);
            break;
        
        case KeyEvent.VK_O:
            myRightArm.rotateShoulder(-5);
            break;
        
        case KeyEvent.VK_J:
            myRightLeg.rotateHip(-5);
            break;
        
        case KeyEvent.VK_K:
            myRightLeg.rotateHip(5);
            break;

        case KeyEvent.VK_N:
            myRightLeg.rotateKnee(-5);
            break;

        case KeyEvent.VK_M:
            myRightLeg.rotateKnee(5);
            break;
          
        }
        
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    
}
