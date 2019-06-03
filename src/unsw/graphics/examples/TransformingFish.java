/**
 * 
 */
package unsw.graphics.examples;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.geometry.Line2D;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;

/**
 * Draw more than one fish.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class TransformingFish extends Application2D {

    public TransformingFish() {
        super("Transforming fish", 600, 600);
    }

    public static void main(String[] args) {
        TransformingFish example = new TransformingFish();
        example.start();
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);   
        drawFish(gl);
    }
    
    public void drawFish(GL3 gl) {
        LineStrip2D body = new LineStrip2D(0.5f,0, 0,0.5f, -0.5f,0, 0,-0.5f, 0.5f,0);
        LineStrip2D tail = new LineStrip2D(0.5f,0, 0.75f,-0.5f, 0.75f,0.5f, 0.5f,0);
        Point2D eye = new Point2D(-0.2f, 0.1f);
        Line2D mouth = new Line2D(-0.5f,0, -0.4f,0);
        
        body.draw(gl);
        tail.draw(gl);
        eye.draw(gl);
        mouth.draw(gl);
    }

}
