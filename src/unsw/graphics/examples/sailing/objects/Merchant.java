package unsw.graphics.examples.sailing.objects;

import java.awt.Color;

import org.json.JSONObject;

import unsw.graphics.geometry.Polygon2D;
import unsw.graphics.scene.PolygonalSceneObject;
import unsw.graphics.scene.SceneObject;




/**
 * COMMENT: Comment Boat 
 *
 * @author malcolmr
 */
public class Merchant extends PolygonalSceneObject {

    // 
    //  (-1.0, 0.7) +---------------+\ (1.0, 0.6)
    //              |                  \
    //              |      + (0,0)      + (1.5, 0)
    //              |                  /
    // (-1.0, -0.7) +---------------+/ (1.0, -0.6)
    //


    private static final Polygon2D POLYGON = new Polygon2D(1.5f, 0, 1, 0.6f, -1, 0.7f, -1,
            -0.7f, 1, -0.6f);
    private static final Color LINE_COLOR = new Color(0.5f, 0.3f, 0.0f, 1.0f);
    private static final Color FILL_COLOR = new Color(0.5f, 0.4f, 0.25f, 1.0f);
    
    @SuppressWarnings("unused")
    private Sail mySail;
    
    public Merchant(SceneObject parent, float x, float y, float angle) {
        super(parent, POLYGON, FILL_COLOR, LINE_COLOR);

        mySail = new Sail(this, 0, 0, 0, 1.0f);

        setPosition(x, y);
        setRotation(angle);
    }

    /**
     * COMMENT: fromJSON
     * 
     * @param jsonObject
     * @return
     */
    public static Merchant fromJSON(SceneObject parent, JSONObject json) {
        float x = (float) json.getDouble("x");
        float y = (float) json.getDouble("y");
        float angle = (float) json.getDouble("angle");
        
        return new Merchant(parent, x, y, angle);
    }



}
