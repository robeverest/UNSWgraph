/**
 * 
 */
package unsw.graphics;

import java.awt.Color;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

/**
 * A UNSWgraph 2D application.
 * 
 * 2D applications allow for shapes to be drawn on a canvas. By default, the
 * canvas is centered at (0,0) the right side side at x = -1 and the right at x
 * = 1. Similarly the bottom is at y = -1 and the top is at y = 1.
 * 
 * @author Robert Clifton-Everest
 *
 */
public abstract class Application2D extends Application {

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
    public Application2D(String title, int width, int height) {
        super(title, width, height);
    }

    /**
     * Called to draw window contents. By default it just clears the screen to
     * the background color. Subclasses are expected to override this method to
     * draw their application's content (but still calling super.display()).
     * 
     * @param gl
     */
    @Override
    public void display(GL3 gl) {
        // Set the clear color.
        gl.glClearColor(getBackground().getRed()/255f, getBackground().getGreen()/255f,
                getBackground().getBlue()/255f, getBackground().getAlpha()/255f);

        // Clear the screen with the defined clear color
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
        // The default model transform is the identity transform
        Shader.setModelMatrix(gl, Matrix3.identity());
        
        // ... as is the view matrix
        Shader.setViewMatrix(gl, Matrix3.identity());
        
        Shader.setPenColor(gl, Color.BLACK);
    }

    @Override
    public void init(GL3 gl) {
        shader = new Shader(gl, "shaders/vertex_2d.glsl",
                "shaders/fragment_2d.glsl");
        shader.use(gl);

        // Make points big (10 pixels wide) so we can see them clearly
        gl.glPointSize(10);
    }

    @Override
    public void destroy(GL3 gl) {
        shader.destroy(gl);
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        // We're not doing anything here yet
    }
    
    @Override
    public Shader getDefaultShader() {
        return shader;
    }
}
