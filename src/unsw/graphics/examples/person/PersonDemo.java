/**
 * 
 */
package unsw.graphics.examples.person;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.CoordFrame2D;

/**
 * Draw a person with a scene tree.
 * 
 * The person can be controlled with the keys:
 *   - 1 and 2 for the left elbow
 *   - q and w for the left shoulder
 *   - a and s for the left hip
 *   - z and x for the left knee
 *   - 9 and 0 for the right elbow
 *   - i and o for the right shoulder
 *   - j and k for the right hip
 *   - n and m for the right knee
 *
 * @author Robert Clifton-Everest
 * 
 */
public class PersonDemo extends Application2D implements KeyListener {
    
    private boolean useCamera;
    
    private Person person;
    
    private Camera camera;

    public PersonDemo() {
        super("Person", 600, 600);
        person = new Person(0, 0, 0, 1);
        camera = new Camera();
        useCamera = false;
    }

    public static void main(String[] args) {
        PersonDemo example = new PersonDemo();
        example.start();
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
       
        CoordFrame2D frame;
        if (!useCamera) {
            // Bring everything into view by scaling down the wprld
            frame = CoordFrame2D.identity().scale(0.05f, 0.05f);
        } else { 
            // Use a camera instead
            camera.setView(gl);
            frame = CoordFrame2D.identity();
        }
        
        person.draw(gl,frame);
        
        //Draw the camera so we can see it if we're not using a camera view
        //camera.draw(gl, frame);
    }
    
    @Override
    public void init(GL3 gl) {
        super.init(gl);
        getWindow().addKeyListener(person);
        getWindow().addKeyListener(camera);
        getWindow().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            useCamera ^= true;
    }

    @Override
    public void keyReleased(KeyEvent arg0) {}

}
