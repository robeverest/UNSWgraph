/**
 * 
 */
package unsw.graphics;

import java.awt.Color;

import com.jogamp.nativewindow.NativeSurface;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * The basis for all UNSWgraph applications.
 * 
 * Applications in UNSWgraph are single window applications containing an OpenGL
 * surface.
 * 
 * @author Robert Clifton-Everest
 *
 */
public abstract class Application implements GLEventListener {

    private Color background;

    private GLWindow window;

    private String title;

    private int width;

    private int height;

    private FPSAnimator animator;

    /**
     * Construct an Application. The window for the application will have the
     * given title, width, and height.
     * 
     * The dimensions are given in window units. How many pixels this
     * corresponds to is system dependent (e.g. it is different between Mac,
     * Linux and Windows). See {@link NativeSurface} for more details.
     * 
     * @param title What appears on the title bar of the application window
     * @param width The width of the window (in window units)
     * @param height The height of the window (in window units)
     */
    public Application(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.background = new Color(1f, 1f, 1f);
    }

    /**
     * Start the application.
     * 
     * This displays the window and starts an animation timer that causes the
     * window to refresh at 60 frames a second.
     * 
     */
    public void start() {
        // Get an OpenGL 3 profile.
        GLProfile glProfile = GLProfile.get(GLProfile.GL3);

        // The capabilities are what OpenGL features the hardware is capable of
        // using
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);
        
        glCapabilities.setNumSamples(8);
        glCapabilities.setSampleBuffers(true);

        // Use newt to create a window with an OpenGL canvas
        window = GLWindow.create(glCapabilities);
        window.setTitle(title);
        window.setSize(width, height);
        window.setResizable(false);

        // Turn on debug mode (REALLY USEFUL)
        window.setContextCreationFlags(GLContext.CTX_OPTION_DEBUG);

        window.setVisible(true);

        // By adding this object as a GLEventListener, the window will call its
        // methods when it the window is initialised, reshaped and on every
        // frame that is drawn.
        window.addGLEventListener(this);

        // Create an animator. The animator will tell the window to redraw
        // itself 60 times a second.
        animator = new FPSAnimator(window, 60);

        // THis is just to make sure everything shuts down properly when the
        // window is closed.
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent e) {
                animator.stop();
                System.exit(0);
            }
        });

        // Start the actual animator.
        animator.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        display(gl);
    }

    /**
     * This is called to draw each frame.
     * 
     * @param gl
     */
    public abstract void display(GL3 gl);

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        destroy(gl);
    }

    /**
     * This is called when the window is closed. Subclasses should override this
     * to safely free or destroy any allocated resources.
     * 
     * @param gl
     */
    public abstract void destroy(GL3 gl);

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        init(gl);
    }

    /**
     * This is called when the window is first created. Subclasses should
     * override this for resource allocation.
     * 
     * @param gl
     */
    public abstract void init(GL3 gl);

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        GL3 gl = drawable.getGL().getGL3();
        reshape(gl, width, height);
    }

    /**
     * Called when the window is resized.
     * 
     * @param gl
     * @param width
     * @param height
     */
    public abstract void reshape(GL3 gl, int width, int height);

    /**
     * Get the background color.
     * 
     * @return
     */
    public Color getBackground() {
        return background;
    }

    /**
     * Set the background color.
     * 
     * @param background
     */
    public void setBackground(Color background) {
        this.background = background;
    }

    /**
     * Get the GLWindow that corresponds to this application.
     * 
     * @return
     */
    public GLWindow getWindow() {
        return window;
    }
    
    /**
     * Get the default shader associated with this application.
     * @return
     */
    public abstract Shader getDefaultShader();
    

    public FPSAnimator getAnimator() {
        return animator;
    }

}
