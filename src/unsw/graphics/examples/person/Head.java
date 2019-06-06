package unsw.graphics.examples.person;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Polygon2D;

/**
 * The head for the person demo
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class Head {

    private static final int VERTICES = 32;
    private static final float RADIUS = 3;
    
    private Polygon2D poly;
    
    public Head() {
        List<Point2D> points = new ArrayList<Point2D>();
        for (int i = 0; i < VERTICES; i++) {
            float a = (float) (i * Math.PI * 2 / VERTICES); // java.util.Math uses radians!!!
            float x = RADIUS * (float) Math.cos(a);
            float y = RADIUS * ((float) Math.sin(a) + 1); // Off center
            Point2D p = new Point2D(x, y);
            points.add(p);
        }
        
        poly = new Polygon2D(points);
    }

    public void draw(GL3 gl, CoordFrame2D frame) {
        poly.draw(gl, frame);
    }

}
