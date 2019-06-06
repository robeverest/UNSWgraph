package unsw.graphics.scene;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;


/**
 * A Scene consists of a scene tree and a camera attached to the tree.
 * 
 * Every object in the scene tree is updated on each display call.
 * Then the scene tree is rendered.
 *
 * You shouldn't need to modify this class.
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 * 
 */
public class Scene {

    private Camera myCamera;

    private SceneObject root;
    private long myTime;

    /**
     * Construct a new scene with a camera attached to the root object.
     *
     */
    public Scene() {
        root = new SceneObject();
        myTime = System.currentTimeMillis();
        myCamera = new Camera(root);
    }

    public void reshape(int width, int height) {
        
        // tell the camera that the screen has reshaped
        myCamera.reshape(width, height);
    }

    public void draw(GL3 gl) {

        // set the view matrix based on the camera position
        myCamera.setView(gl); 
        
        // update the objects
        update();

        // draw the scene tree
        root.draw(gl, CoordFrame2D.identity());        
    }

    private void update() {
        
        // compute the time since the last frame
        long time = System.currentTimeMillis();
        float dt = (time - myTime) / 1000f;
        myTime = time;
        
        root.update(dt);      
    }

    public SceneObject getRoot() {
        return root;
    }
   
    public Camera getCamera() {
        return myCamera;
    }

    public void setCamera(Camera camera) {
        myCamera.destroy();
        this.myCamera = camera;
    }
    
}
