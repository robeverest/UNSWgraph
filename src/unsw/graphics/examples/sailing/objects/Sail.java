package unsw.graphics.examples.sailing.objects;

import java.awt.Color;

import unsw.graphics.geometry.Polygon2D;
import unsw.graphics.scene.MathUtil;
import unsw.graphics.scene.PolygonalSceneObject;
import unsw.graphics.scene.SceneObject;

/**
 * COMMENT: Comment Sail 
 *
 * @author malcolmr
 */
public class Sail extends PolygonalSceneObject {

    private static final Polygon2D POLYGON = new Polygon2D(0.5f, 0, 0, 1.5f, 0, -1.5f);
    private static final Color LINE_COLOR = new Color(1.0f, 1.0f, 1.0f);
    private static final Color FILL_COLOR = new Color(1.0f, 1.0f, 1.0f, 0.75f);

    private float myMaxAngle = 15;
    
    public Sail(SceneObject parent, float x, float y, float angle, float scale) {
        super(parent, POLYGON, FILL_COLOR, LINE_COLOR);

        setPosition(x, y);
        setRotation(angle);
        setScale(scale);
    }
    
    public void setAngle(float angle) {
        setRotation(MathUtil.clamp(angle, -myMaxAngle, myMaxAngle));
    }
    
    
}
