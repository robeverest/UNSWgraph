package unsw.graphics.examples.person;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.geometry.Line2D;
import unsw.graphics.geometry.Triangle2D;

/**
 * The arm for the person demo
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class Arm {

    private static final float UPPER_ARM_LENGTH = 6.0f;
    private static final float FORE_ARM_LENGTH = 6.0f;

    private boolean isLeft;
    private float myShoulderAngle;
    private float myElbowAngle;

    public Arm(boolean left) {
        isLeft = left;
        myShoulderAngle = 45;
        myElbowAngle = 45;
    }

    public void rotateShoulder(float angle) {
        myShoulderAngle += angle;
    }

    public void rotateElbow(float angle) {
        myElbowAngle += angle;
    }

    public void draw(GL3 gl, CoordFrame2D frame) {
        CoordFrame2D armFrame = frame
            // Flip left to right if necessary
            .scale(isLeft ? -1 : 1, 1)
            // Rotate the arm
            .rotate(myShoulderAngle);

        // Draw the upper arm
        Line2D upperArm = new Line2D(0,0, 0, -UPPER_ARM_LENGTH);
        upperArm.draw(gl, armFrame);
            
        // Move the coordinate frame to draw the forearm
        CoordFrame2D forearmFrame = armFrame.translate(0, -UPPER_ARM_LENGTH)
                .rotate(myElbowAngle);

        Line2D foreArm = new Line2D(0,0, 0, -FORE_ARM_LENGTH);
        foreArm.draw(gl, forearmFrame);
            
        // Move the coordinate frame to draw the hand
        CoordFrame2D handFrame = forearmFrame.translate(0, -FORE_ARM_LENGTH);
        
        // Hand is a triangle with sides 1,1 and 2*sqrt(3)
        float y = (float) -Math.sqrt(3);
        Triangle2D hand = new Triangle2D(0,0, -1,y, 1,y);
        hand.draw(gl, handFrame);   
    }

}
