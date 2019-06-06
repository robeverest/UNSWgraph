package unsw.graphics.examples.sailing.objects;

import java.awt.Color;

import org.json.JSONObject;

import com.jogamp.newt.event.MouseEvent;

import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Polygon2D;
import unsw.graphics.scene.MathUtil;
import unsw.graphics.scene.PolygonalSceneObject;
import unsw.graphics.scene.SceneObject;


/**
 * COMMENT: Comment Boat 
 *
 * @author malcolmr
 */
public class Pirate extends PolygonalSceneObject {

    // 
    //  (-1.0, 0.7) +---------------+\ (1.0, 0.6)
    //              |                  \
    //              |      + (0,0)      + (1.5, 0)
    //              |                  /
    // (-1.0, -0.7) +---------------+/ (1.0, -0.6)
    //


    private static final Polygon2D POLYGON = new Polygon2D(1.5f, 0, 1, 0.6f, -1, 0.7f, -1,
            -0.7f, 1, -0.6f);
    private static final Color LINE_COLOR = new Color(0.5f, 0.3f, 0.0f);
    private static final Color FILL_COLOR = new Color(0.5f, 0.4f, 0.25f);
    
    private static final int LEFT_BUTTON = MouseEvent.BUTTON1;
    private static final int RIGHT_BUTTON = MouseEvent.BUTTON3;

    private final Sail myFrontSail;
    private final Sail myBackSail;

    private float myTurningSpeed = 90;
    private float mySpeed = 5;
    private float myRadius = 1;

    private final Cannon[] myPortCannon;
    private final Cannon[] myStarboardCannon;
    
    public Pirate(SceneObject parent, float x, float y, float angle) {
        super(parent, POLYGON, FILL_COLOR, LINE_COLOR);

        myPortCannon = new Cannon[2];
        myStarboardCannon = new Cannon[2];
        
        myPortCannon[0] = new Cannon(this, 0.5f, 0.625f, 90, 0.2f, LEFT_BUTTON);
        myPortCannon[1] = new Cannon(this, -0.5f, 0.675f, 90, 0.2f, LEFT_BUTTON);
        myStarboardCannon[0] = new Cannon(this, 0.5f, -0.625f, -90f, 0.2f, RIGHT_BUTTON);
        myStarboardCannon[1] = new Cannon(this, -0.5f, -0.675f, -90f, 0.2f, RIGHT_BUTTON);

        

        myFrontSail = new Sail(this, 0.5f, 0, 0, 0.75f);
        myBackSail = new Sail(this, -0.5f, 0, 0, 1.0f);               

        setPosition(x, y);
        setRotation(angle);
    }

    /**
     * COMMENT: update
     * 
     */
    @Override
    public void updateSelf(float dt) {
        move(dt);
    }

    private void move(float dt) {
        Point2D target = Mouse.theMouse.getPosition();
        Point2D pos = getGlobalPosition();
        
        float dx = target.getX() - pos.getX();
        float dy = target.getY() - pos.getY();
        float dd = (float) Math.sqrt(dx * dx + dy * dy);

        // only move if the mouse is outside our close vicinty (no turning on the spot)
        
        if (dd > myRadius) {

            float heading = getGlobalRotation();
            float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
 
            angle = MathUtil.normaliseAngle(angle - heading);

            myFrontSail.setAngle(angle);
            myBackSail.setAngle(angle);
            
            // turn towards the mouse at a fixed rate

            float maxTurn = dt * myTurningSpeed;
            angle = MathUtil.clamp(angle, -maxTurn, maxTurn);
            rotate(angle);
           
            float distance = dt * mySpeed;
            distance = MathUtil.clamp(distance, 0, dd);

            double theta = Math.toRadians(getRotation());
            translate(distance * (float) Math.cos(theta), distance * (float) Math.sin(theta));                 
        }
    }
    
    /**
     * COMMENT: fromJSON
     * 
     * @param jsonPlayer
     * @return
     */
    public static Pirate fromJSON(SceneObject parent, JSONObject json) {
        float x = (float) json.getDouble("x");
        float y = (float) json.getDouble("y");
        float angle = (float) json.getDouble("angle");
        
        return new Pirate(parent, x, y, angle);
    }


}
