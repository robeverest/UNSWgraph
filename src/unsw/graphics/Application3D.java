package unsw.graphics;

import java.awt.Color;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

/**
 * A UNSWgraph 3D application.
 * 
 * 3D applications allow for shapes to be drawn in a view volume and projected into a canvas.
 * Unlike 2D applications, 3D applications must implement the reshape() method and use that to
 * compute a projection transform. By default the projection is orthographic.
 * 
 * @author Robert Clifton-Everest
 *
 */
public abstract class Application3D extends Application {
    
    /**
     * The current shader.
     */
    private Shader shader;

    /**
     * Construct a new 2D application.
     * 
     * Dimensions are in window units. See {@link Application}.
     * 
     * @param title The title of the window for this application
     * @param width The width of the window (in window units)
     * @param height The height of the window (in window units)
     */
    public Application3D(String title, int width, int height) {
        super(title, width, height);
    }

    /**
     * Called to draw window contents. By default it just clears the screen to
     * the background color and sets the view and model transform as the identity transform. 
     * Subclasses are expected to override this method to draw their application's content (but 
     * still calling super.display()).
     * 
     * @param gl
     */
    @Override
    public void display(GL3 gl) {
        // Set the clear color.
        gl.glClearColor(getBackground().getRed()/255f, getBackground().getGreen()/255f,
                getBackground().getBlue()/255f, getBackground().getAlpha()/255f);

        // Clear the screen with the defined clear color
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        // The default model transform is the identity transform
        Shader.setModelMatrix(gl, Matrix4.identity());
        
        // ... as is the view matrix
        Shader.setViewMatrix(gl, Matrix4.identity());
        
        // Reshape is called here just to make sure that if the shader is changed,
        // then the projection matrix property is set.
        reshape(gl, getWindow().getWidth(), getWindow().getHeight());
        
        Shader.setPenColor(gl, Color.BLACK);
    }

    @Override
    public void destroy(GL3 gl) {
        shader.destroy(gl);
    }

    @Override
    public void init(GL3 gl) {
        shader = new Shader(gl, "shaders/vertex_3d.glsl",
                "shaders/fragment_3d.glsl");
        shader.use(gl);

        // Make points big (10 pixels wide) so we can see them clearly
        gl.glPointSize(10);
        
        // Turn on the depth buffer
        gl.glEnable(GL.GL_DEPTH_TEST);
        
        // Cull back faces
        gl.glEnable(GL.GL_CULL_FACE);
    }
    
    @Override
    public void reshape(GL3 gl, int width, int height) {
        // The projection matrix is orthographic by default
        Shader.setProjMatrix(gl, Matrix4.orthographic(-1, 1, -1, 1, 1, 10));
    }

    @Override
    public Shader getDefaultShader() {
        return shader;
    }

}
