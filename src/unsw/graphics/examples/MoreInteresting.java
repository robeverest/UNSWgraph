/**
 * 
 */
package unsw.graphics.examples;

import java.awt.Color;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.geometry.Line2D;
import unsw.graphics.geometry.Point2D;

/**
 * A more interesting example. What does it do? You have to run it to find out.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class MoreInteresting extends Application2D {

    public MoreInteresting() {
        super("More interesting example", 600, 600);
    }

    public static void main(String[] args) {
        MoreInteresting example = new MoreInteresting();
        example.start();
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        
        Point2D right, top, left, bottom;
        right  = new Point2D(0.5f, 0);
        top    = new Point2D(0, 0.5f);
        left   = new Point2D(-0.5f, 0);
        bottom = new Point2D(0, -0.5f);
        
        Line2D topRight, topLeft, bottomLeft, bottomRight;
        topRight    = new Line2D(right, top);
        topLeft     = new Line2D(top, left);
        bottomLeft  = new Line2D(left, bottom);
        bottomRight = new Line2D(bottom, right);
        
        topRight.draw(gl);
        topLeft.draw(gl);
        bottomLeft.draw(gl);
        bottomRight.draw(gl);
        
        Point2D topTail, bottomTail;
        topTail    = new Point2D(0.75f, 0.5f);
        bottomTail = new Point2D(0.75f, -0.5f);
        
        Line2D topTailLine, bottomTailLine, middleTailLine;
        topTailLine    = new Line2D(topTail, right);
        bottomTailLine = new Line2D(bottomTail, right);
        middleTailLine = new Line2D(topTail, bottomTail);
        
        topTailLine.draw(gl);
        bottomTailLine.draw(gl);
        middleTailLine.draw(gl);
        
        Point2D eye = new Point2D(-0.2f, 0.1f);
        eye.draw(gl);
        
        Line2D mouth = new Line2D(new Point2D(-0.5f, 0), new Point2D(-0.4f, 0));
        mouth.draw(gl);
    }

}
