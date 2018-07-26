package unsw.graphics.examples;

import java.awt.Color;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.geometry.Triangle2D;

public class TwoTriangles extends Application2D {

    public TwoTriangles() {
        super("Two triangles", 600, 600);
        setBackground(new Color(1f,1f,0));
    }
    
    public static void main(String[] args) {
        TwoTriangles example = new TwoTriangles();
        example.start();
    }
    
    @Override 
    public void display(GL3 gl) {
        super.display(gl);
        Triangle2D tri1 = new Triangle2D(0, 0, 1, 1, -1, 1);
        Triangle2D tri2 = new Triangle2D(0, 0, -1, -1, 1, -1);
        tri1.draw(gl);
        tri2.draw(gl);
    }

}
