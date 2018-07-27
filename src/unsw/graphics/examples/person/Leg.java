package unsw.graphics.examples.person;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.geometry.Line2D;
import unsw.graphics.geometry.Triangle2D;

/**
 * The leg for the person demo
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class Leg {

    private static final float THIGH_LENGTH = 6.0f;
    private static final float SHIN_LENGTH = 6.0f;    

    private boolean isLeft;
    private float myHipAngle;
    private float myKneeAngle;
    
    public Leg(boolean left) {
        isLeft = left;
        
        myHipAngle = 45;
        myKneeAngle = -45;
    }

    public void rotateHip(float angle) {
        myHipAngle += angle;
    }
    
    public void rotateKnee(float angle) {
        myKneeAngle += angle;
    }
    
    public void draw(GL3 gl, CoordFrame2D frame) {

        CoordFrame2D legFrame = frame
                // Flip left to right if necessary
                .scale(isLeft ? -1 : 1, 1)
                // Rotate the thigh
                .rotate(myHipAngle);

        // Draw the thigh
        Line2D thigh = new Line2D(0,0, 0, -THIGH_LENGTH);
        thigh.draw(gl, legFrame);
       
        // Move the coordinate frame to draw the shin
        CoordFrame2D shinFrame = legFrame.translate(0, -THIGH_LENGTH)
                .rotate(myKneeAngle);

        Line2D shin = new Line2D(0,0, 0, -SHIN_LENGTH);
        shin.draw(gl, shinFrame);
            
        // Move the coordinate frame to draw the foot
        CoordFrame2D footFrame = shinFrame.translate(0, -SHIN_LENGTH);
        Triangle2D foot = new Triangle2D(0,0, 0,-1, 2,-1);
        foot.draw(gl, footFrame);
    }

}
