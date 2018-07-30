package unsw.graphics.examples.sailing.objects;

import java.awt.Color;

import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Polygon2D;
import unsw.graphics.scene.PolygonalSceneObject;
import unsw.graphics.scene.SceneObject;

/**
 * COMMENT: Comment Sail 
 *
 * @author malcolmr
 */
public class Cannon extends PolygonalSceneObject {

    static final Polygon2D POLYGON = new Polygon2D(0, 1, -0.87f, 0.5f, -0.87f, -0.5f, 0, -1,
            2, -0.67f, 2, 0.67f);
    static final Color LINE_COLOR = Color.BLACK;
    static final Color FILL_COLOR = new Color(0.1f, 0.1f, 0.1f);
    private int myButton;

    private Point2D myPrevPos;
    
    public Cannon(SceneObject parent, float x, float y, float angle,
            float scale, int button) {
        super(parent, POLYGON, FILL_COLOR, LINE_COLOR);

        myButton = button;

        setPosition(x, y);
        setRotation(angle);
        setScale(scale);
        
        myPrevPos = getGlobalPosition();
    }

    @Override
    public void updateSelf(float dt) {

        Point2D position = getGlobalPosition();

        // fire the cannon
        if (Mouse.theMouse.wasPressed(myButton)) {
            // create a ball at the cannon's origin
            CannonBall ball = new CannonBall(this, 0, 0, 0, 1);

            // detach it from the cannon
            ball.setParent(getParent().getParent());
            
            // add momentum to match the ship's speed
            float vx = (position.getX() - myPrevPos.getX()) / dt;
            float vy = (position.getY() - myPrevPos.getY()) / dt;

            ball.setMomentum(vx, vy);
        }
        
        myPrevPos = position;
    }


}
