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

    public HelloLine() {
        super("HelloLine", 600, 600);
        this.setBackground(new Color(1f, 1f, 1f));
    }

    public static void main(String[] args) {
        HelloLine example = new HelloLine();
        example.start();
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);

        Point2D point1 = new Point2D(0f, 0f);
        Point2D point2 = new Point2D(0.5f, 0.5f);

        Line2D line = new Line2D(point1, point2);
        line.draw(gl);
    }

}
