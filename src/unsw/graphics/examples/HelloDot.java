/**
 * 
 */
package unsw.graphics.examples;

import java.awt.Color;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.geometry.Point2D;

/**
 * A simple "Hello World" example using UNSWgraph. It just draws a dot in a
 * window
 * 
 * @author Robert Clifton-Everest
 *
 */
public class HelloDot extends Application2D {

    public HelloDot() {
        super("HelloDot", 600, 600);
        this.setBackground(new Color(1f, 1f, 1f));
    }

    public static void main(String[] args) {
        HelloDot example = new HelloDot();
        example.start();
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);
        Point2D point = new Point2D(0f, 0f);
        point.draw(gl);
    }

}
