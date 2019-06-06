package unsw.graphics.examples.sailing.objects;

import com.jogamp.newt.Window;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;

import unsw.graphics.geometry.Point2D;
import unsw.graphics.scene.Camera;



/**
 * A mouse handler for the sailing game.
 * 
 * It keeps track of:
 *  1) the mouse position in world coordinates
 *  2) mouse press/release events that have happened in the past frame
 *
 * Mouse is a singleton class. There is only one instance, stored in Mouse.theMouse.
 * 
 * You need to add this object as a MouseListener and MouseMotionListener to your window to make it work:
 * 
 * getWindow().addMouseListener(Mouse.theMouse);
 * getWindow().addMouseMotionListener(Mouse.theMouse);
 *
 * @author malcolmr
 */
public class Mouse extends MouseAdapter {

    public static final Mouse theMouse = new Mouse();
    
    private MouseEvent lastEvent;
    /**
     * The position of the mouse in world coordinates
     */
    private Point2D myPosition;

    private boolean[] myPressed;
    private boolean[] myReleased;
    private boolean[] myWasPressed;
    private boolean[] myWasReleased;

    private Mouse() {
        myPosition = new Point2D(0, 0);

        myPressed = new boolean[3];
        myReleased = new boolean[3];
        myWasPressed = new boolean[3];
        myWasReleased = new boolean[3];     
    }

    /**
     * When the window is reshaped, store the new projection matrix and viewport
     * 
     * @param gl
     */
    public void reshape(int width, int height) {
        
    }

    /**
     * When the view is updated, compute and store the position of the mouse in the world coordinate
     * frame.
     *
     * Update any mouse presses or releases that have happened.
     * 
     * @param gl
     */
    public void update(Camera camera, Window window) {
        if (lastEvent != null) {
            float x = 2f*lastEvent.getX()/window.getSurfaceWidth() - 1f;
            float y = -2f*lastEvent.getY()/window.getSurfaceHeight() + 1f;
            myPosition = camera.fromView(x, y);
        }
        
        for (int i = 0; i < 3; i++) {
            myWasPressed[i] = myPressed[i];
            myWasReleased[i] = myReleased[i];
            myPressed[i] = false;
            myReleased[i] = false;            
        }
    }

    /**
     * Get the current mouse position in world coordinates.
     * 
     * @return
     */
    public Point2D getPosition() {
        return myPosition;
    }

    /**
     * Store mouse movement events to record the latest mouse position.
     * 
     * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        lastEvent = e;
    }

    /**
     * Store mouse movement events to record the latest mouse position.
     *
     * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        lastEvent = e;
    }

    /**
     * Store the most recent mouse press event for each of the buttons.
     *  
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        myPressed[e.getButton()-1] = true;
    }

    /**
     * Store the most recent mouse press release for each of the buttons.
     * 
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        myReleased[e.getButton()-1] = true;
    }

    /**
     * Returns true if the specified mouse button was pressed this frame.
     * 
     * @param button should be 1, 2 or 3
     * @return
     */
    public boolean wasPressed(int button) {
        return myWasPressed[button-1];
    }

    /**
     * Returns true if the specified mouse button was released this frame.
     * 
     * @param button should be 1, 2 or 3
     * @return
     */
    public boolean wasReleased(int button) {
        return myWasReleased[button-1];
    }

}
