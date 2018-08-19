package unsw.graphics.examples;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * This is a simple application for viewing models.
 * 
 * Different PLY models have vastly different scales, so you may need to scale
 * the model up or down to view it properly.
 * 
 * High resolution models are not included with UNSWgraph due to their large
 * file sizes. They can be downloaded here:
 * 
 * https://www.dropbox.com/s/tg2y5kvzbgb3pco/big.zip?dl=1
 * 
 * @author Robert Clifton-Everest
 *
 */
public class ModelViewer extends Application3D {

    private static final boolean USE_LIGHTING = false;

    private float rotateY;

    private TriangleMesh model;

    private TriangleMesh base;

    public ModelViewer() throws IOException {
        super("Model viewer", 600, 600);
        model = new TriangleMesh("res/models/bunny.ply", true);
        base = new TriangleMesh("res/models/cube_normals.ply", true);
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);
        model.init(gl);
        base.init(gl);
        if (USE_LIGHTING) {
            Shader shader = new Shader(gl, "shaders/vertex_gouraud.glsl",
                    "shaders/fragment_gouraud.glsl");
            shader.use(gl);
        }
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

        // Compute the view transform
        CoordFrame3D view = CoordFrame3D.identity().translate(0, 0, -2)
                // Uncomment the line below to rotate the camera
                // .rotateY(rotateY)
                .translate(0, 0, 2);
        Shader.setViewMatrix(gl, view.getMatrix());

        // Set the lighting properties
        Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
        Shader.setFloat(gl, "lightIntensity", 1);
        Shader.setFloat(gl, "ambientIntensity", 0.2f);
        
        // Set the material properties
        Shader.setFloat(gl, "ambientCoeff", 1);
        Shader.setFloat(gl, "diffuseCoeff", 0.5f);
        Shader.setFloat(gl, "specularCoeff", 0.8f);
        Shader.setFloat(gl, "phongExp", 16f);

        // The coordinate frame for both objects
        CoordFrame3D frame = CoordFrame3D.identity().translate(0, -0.5f, -2);

        // The coordinate frame for the model we're viewing.
        CoordFrame3D modelFrame = frame
                // Uncomment the line below to rotate the model
                .rotateY(rotateY)

                // This translation and scale works well for the bunny and
                // dragon1
                .translate(0, -0.2f, 0).scale(5, 5, 5);
        // This scale works well for the apple
        // .scale(5, 5, 5);
        // This translation and scale works well for dragon2
        // .translate(0,0.33f,0).scale(0.008f, 0.008f, 0.008f);
        Shader.setPenColor(gl, new Color(0.5f, 0.5f, 0.5f));
        model.draw(gl, modelFrame);

        // A blue base for the model to sit on.
        CoordFrame3D baseFrame = 
                frame.translate(0, -0.5f, 0).scale(0.5f, 0.5f, 0.5f);
        Shader.setPenColor(gl, Color.BLUE);
        base.draw(gl, baseFrame);

        rotateY += 1;
    }
    
    @Override
    public void destroy(GL3 gl) {
        super.destroy(gl);
        model.destroy(gl);
        base.destroy(gl);
    }

}