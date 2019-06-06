package unsw.graphics.examples.sailing.objects;

import unsw.graphics.geometry.Point2D;
import unsw.graphics.scene.SceneObject;

/**
 * COMMENT: Comment CameraHarness 
 *
 * @author malcolmr
 */
public class CameraHarness extends SceneObject {

    private SceneObject myTarget;

    // don't connect to the target
    // instead track the target without rotation or scaling
    public CameraHarness(SceneObject parent, SceneObject target) {
        super(parent);
        
        myTarget = target;
    }

    @Override
    public void updateSelf(float dt) {
        Point2D p = myTarget.getGlobalPosition();
        setPosition(p.getX(), p.getY());
    }
    
}
