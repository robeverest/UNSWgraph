package unsw.graphics.examples;

import java.awt.Color;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Triangle3D;

/**
 * A simple example to test out drawing in 3D.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class HelloTriangle3D extends Application3D {

    public HelloTriangle3D() {
        super("Hello triangle", 600, 600);
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        // Doing nothing else in this method, for now.
    }

    public static void main(String[] args) {
        HelloTriangle3D example = new HelloTriangle3D();
        example.start();
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        
        gl.glDepthMask(false);
        // tri1 is in front of tri2
        Triangle3D tri1 = new Triangle3D(0,0,-1, 0.5f,0,-1, 0,1,-1);
        Triangle3D tri2 = new Triangle3D(-1,0,-2, 1,0,-2, 0,0.5f,-2);
        
        Shader.setPenColor(gl, new Color(0, 1f, 0, 0.7f));
        tri2.draw(gl);
        
        Shader.setPenColor(gl, new Color(0,0,1f,0.7f));
        tri1.draw(gl);
    }

}
