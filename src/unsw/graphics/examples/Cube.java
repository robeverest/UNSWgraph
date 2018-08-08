package unsw.graphics.examples;

import java.awt.Color;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.TriangleFan3D;

/**
 * A simple example that draws a cube.
 * 
 * You can use this to play around with rotations and projection.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Cube extends Application3D {

    public Cube() {
        super("Cube", 600, 600);
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        // Doing nothing, for now.
    }

    public static void main(String[] args) {
        Cube example = new Cube();
        example.start();
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        CoordFrame3D frame = CoordFrame3D.identity()
                .translate(0, 0, -2)
                .scale(0.5f, 0.5f, 0.5f);
        drawCube(gl, frame.rotateY(30).rotateX(30));
    }

    /**
     * Draw a cube centered around (0,0) with bounds of length 1 in each direction.
     * @param gl
     * @param frame
     */
    private void drawCube(GL3 gl, CoordFrame3D frame) {
        TriangleFan3D face = new TriangleFan3D(-1,-1,1, 1,-1,1, 1,1,1, -1,1,1);
        
        // Front
        Shader.setPenColor(gl, Color.RED);
        face.draw(gl, frame);
        
        // Left
        Shader.setPenColor(gl, Color.BLUE);
        face.draw(gl, frame.rotateY(-90));
        
        // Right
        Shader.setPenColor(gl, Color.GREEN);
        face.draw(gl, frame.rotateY(90));
        
        // Back
        Shader.setPenColor(gl, Color.CYAN);
        face.draw(gl, frame.rotateY(180));
        
        // Bottom
        Shader.setPenColor(gl, Color.YELLOW);
        face.draw(gl, frame.rotateX(-90));
        
        // Right
        Shader.setPenColor(gl, Color.MAGENTA);
        face.draw(gl, frame.rotateX(90));
    }
}