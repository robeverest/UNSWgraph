package unsw.graphics.examples.sailing.objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Polygon2D;
import unsw.graphics.scene.PolygonalSceneObject;
import unsw.graphics.scene.SceneObject;

/**
 * COMMENT: Comment Sail 
 *
 * @author malcolmr
 */
public class CannonBall extends PolygonalSceneObject {

    private static final Color LINE_COLOR = Color.BLACK;
    private static final Color FILL_COLOR = new Color(0.1f, 0.1f, 0.1f, 1.0f);
    private static final int POINTS = 8;

    private static final float LIFETIME = 1.0f;
    private static final float SPEED = 10.0f;
        
    private float myLifetime;
    private Point2D myMomentum;
    
    public CannonBall(SceneObject parent, float x, float y, float angle, float scale) {
        super(parent, makePolygon(), FILL_COLOR, LINE_COLOR);
        
        setPosition(x, y);
        setRotation(angle);
        setScale(scale);
        
        myLifetime = LIFETIME;
        myMomentum = new Point2D(0,0);
    }

    /**
     * Make an approximate circle
     * 
     * @return
     */
    private static Polygon2D makePolygon() {
        List<Point2D> points = new ArrayList<Point2D>();
        
        for (int i = 0; i < POINTS; i++) {
            double a = i * Math.PI * 2.0 / POINTS;
            points.add(new Point2D((float)Math.cos(a), (float)Math.sin(a)));
        }
        
        return new Polygon2D(points);
    }

    /**
     * Use this method to give the cannonball some initial momentum.
     *  
     * This is used to match the ball's initial speed to the ship's
     * even though it is no longer attached to the ship's coordinate frame. 
     * 
     * COMMENT: setMomentum
     * 
     * @param vx
     * @param vy
     */
    public void setMomentum(float vx, float vy) {
        myMomentum = new Point2D(myMomentum.getX() + vx, myMomentum.getY() + vy);
    }

    @Override
    public void updateSelf(float dt) {
        // the ball disappears when the lifetimer runs out
        myLifetime -= dt;
        
        if (myLifetime <= 0) {
            destroy();
        }
        else {
            float d = SPEED * dt;
            double angle = Math.toRadians(getRotation());
            translate((float) (d * Math.cos(angle)), (float) (d * Math.sin(angle)));
            
            // add momentum term
            translate(dt * myMomentum.getX(), dt * myMomentum.getY());
        }
    }

    
    
}
