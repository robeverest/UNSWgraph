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
 * A slightly more complex "Hello World" example using UNSWgraph. It draws a
 * line in the window.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class HelloLine extends Application2D {
    
    private float endY;

    public HelloLine() {
        super("HelloLine", 600, 600);
        this.setBackground(new Color(1f, 1f, 1f));
        endY = 0;
    }

    public static void main(String[] args) {
        HelloLine example = new HelloLine();
        example.start();
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);
        
        endY += 0.01;
        
        if (endY > 1) {
            endY = -1;
        }

        Point2D start = new Point2D(0f, 0f);
        Point2D end = new Point2D(0.5f, endY);

        Line2D line = new Line2D(start, end);
        line.draw(gl);
    }

}
