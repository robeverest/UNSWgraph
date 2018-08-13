package unsw.graphics.examples;

import java.io.IOException;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.TriangleMesh;

/**
 * This is a simple application for viewing models.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class ModelViewer extends Application3D {
    
    private float rotateY;
    
    private TriangleMesh model;

    public ModelViewer() throws IOException {
        super("Model viewer", 600, 600);
        model = new TriangleMesh("res/models/bun_zipper_res4.ply");
    }
    
    @Override
    public void init(GL3 gl) {
        super.init(gl);
        model.init(gl);
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, 1, 1, 100));
    }

    public static void main(String[] args) throws IOException {
        ModelViewer example = new ModelViewer();
        example.start();
        
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        CoordFrame3D frame = CoordFrame3D.identity()
                .translate(0, -0.5f, -2)
                .rotateY(rotateY)
                .scale(5, 5, 5);
        model.draw(gl, frame);
        
        rotateY += 1;
    }

}