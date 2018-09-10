package unsw.graphics.examples;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
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

    private static final boolean USE_LIGHTING = true;
    
    private static final boolean USE_TEXTURE = true;
    
    private static final boolean USE_CUBEMAP = false; //Lighting must also be on

    private float rotateY;

    private TriangleMesh model;

    private TriangleMesh base;
    
    private Texture texture;

    public ModelViewer() throws IOException {
        super("Model viewer", 600, 600);
        model = new TriangleMesh("res/models/bunny.ply", true, true);
        base = new TriangleMesh("res/models/cube_normals.ply", true, true);
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);
        model.init(gl);
        base.init(gl);
        if (USE_CUBEMAP) {
            texture = new Texture(gl, "res/textures/darkskies/darkskies_lf.png",
                    "res/textures/darkskies/darkskies_rt.png",
                    "res/textures/darkskies/darkskies_dn.png",
                    "res/textures/darkskies/darkskies_up.png",
                    "res/textures/darkskies/darkskies_ft.png",
                    "res/textures/darkskies/darkskies_bk.png", "png", false);
        } else if (USE_TEXTURE) {
            texture = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", false);
        }
        
        Shader shader = null;
        if (USE_CUBEMAP) {
            shader = new Shader(gl, "shaders/vertex_phong.glsl",
                    "shaders/fragment_cubemap.glsl");
        } else if (USE_LIGHTING && USE_TEXTURE) {
            shader = new Shader(gl, "shaders/vertex_tex_phong.glsl",
                    "shaders/fragment_tex_phong.glsl");
        } else if (USE_LIGHTING) {
            shader = new Shader(gl, "shaders/vertex_phong.glsl",
                    "shaders/fragment_phong.glsl");
        } else if (USE_TEXTURE) {
            shader = new Shader(gl, "shaders/vertex_tex_3d.glsl",
                    "shaders/fragment_tex_3d.glsl");
        } else {
            shader = new Shader(gl, "shaders/vertex_3d.glsl", "shaders/fragment_3d.glsl");
        }
        shader.use(gl);
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
        
        //Set the texture if we're using it.
        if (USE_CUBEMAP) {
            Shader.setInt(gl, "tex", 0);
            
            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, texture.getId());
            
            Shader.setPenColor(gl, Color.WHITE);
        } else if (USE_TEXTURE) {
            Shader.setInt(gl, "tex", 0);
            
            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
            
            Shader.setPenColor(gl, Color.WHITE);
        } else {
            Shader.setPenColor(gl, new Color(0.5f, 0.5f, 0.5f));
        }
        
        // Compute the view transform
        CoordFrame3D view = CoordFrame3D.identity().translate(0, 0, -2)
                // Uncomment the line below to rotate the camera
                // .rotateY(rotateY)
                .translate(0, 0, 2);
        Shader.setViewMatrix(gl, view.getMatrix());

        // Set the lighting properties
        if (USE_LIGHTING) {
            Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
            Shader.setColor(gl, "lightIntensity", Color.WHITE);
            Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
            
            // Set the material properties
            Shader.setColor(gl, "ambientCoeff", Color.WHITE);
            Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
            Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
            Shader.setFloat(gl, "phongExp", 16f);
        }

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
//         .scale(5, 5, 5);
        // This translation and scale works well for dragon2
//         .translate(0,0.33f,0).scale(0.008f, 0.008f, 0.008f);
        // This translation and scale works well for the tree
//           .translate(0,0.5f,0).scale(0.1f,0.1f,0.1f);
        
        
        model.draw(gl, modelFrame);

        // A blue base for the model to sit on.
        CoordFrame3D baseFrame = 
                frame.translate(0, -0.5f, 0).scale(0.5f, 0.5f, 0.5f);
        if (!USE_TEXTURE && !USE_CUBEMAP)
            Shader.setPenColor(gl, Color.BLUE);
        base.draw(gl, baseFrame);

        rotateY += 1;
    }
    
    @Override
    public void destroy(GL3 gl) {
        super.destroy(gl);
        model.destroy(gl);
        base.destroy(gl);
        texture.destroy(gl);
    }

}