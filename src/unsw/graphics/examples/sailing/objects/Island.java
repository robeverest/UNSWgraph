package unsw.graphics.examples.sailing.objects;

import java.awt.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import unsw.graphics.geometry.Polygon2D;
import unsw.graphics.scene.PolygonalSceneObject;
import unsw.graphics.scene.SceneObject;



/**
 * COMMENT: Comment Sail 
 *
 * @author malcolmr
 */
public class Island extends PolygonalSceneObject {

    private static final Color LINE_COLOR = new Color(0.0f, 0.5f, 0.0f);
    private static final Color FILL_COLOR = new Color(0.0f, 0.9f, 0.0f);

    public Island(SceneObject parent, float x, float y, Polygon2D polygon) {
        super(parent, polygon, FILL_COLOR, LINE_COLOR);

        setPosition(x, y);
    }

    public static Island fromJSON(SceneObject parent, JSONObject json) {

        float x = (float) json.getDouble("x");
        float y = (float) json.getDouble("y");
        JSONArray points = json.getJSONArray("polygon");

        float[] polygon = new float[points.length()];
        for (int i = 0; i < points.length(); i++) {
            polygon[i] = (float) points.getDouble(i);
        }

        return new Island(parent, x, y, new Polygon2D(polygon));
    }

}
