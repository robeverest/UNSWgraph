package unsw.graphics.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;

/**
 * COMMENT: Comment LevelIO 
 *
 * @author malcolmr
 */
public class LevelIO {

    /**
     * Load a terrain object from a JSON file
     * 
     * @param mapFile
     * @return
     * @throws FileNotFoundException 
     */
    public static Terrain load(File mapFile) throws FileNotFoundException {

        Reader in = new FileReader(mapFile);
        JSONTokener jtk = new JSONTokener(in);
        JSONObject jsonTerrain = new JSONObject(jtk);

        int width = jsonTerrain.getInt("width");
        int depth = jsonTerrain.getInt("depth");
        

        JSONArray jsonSun = jsonTerrain.getJSONArray("sunlight");
        float dx = (float)jsonSun.getDouble(0);
        float dy = (float)jsonSun.getDouble(1);
        float dz = (float)jsonSun.getDouble(2);
        
        Terrain terrain = new Terrain(width, depth, new Vector3(dx, dy, dz));
       
        JSONArray jsonAltitude = jsonTerrain.getJSONArray("altitude");
        for (int i = 0; i < jsonAltitude.length(); i++) {
            int x = i % width;
            int z = i / width;

            float h = (float) jsonAltitude.getDouble(i);
            terrain.setGridAltitude(x, z, h);
        }

        if (jsonTerrain.has("trees")) {
            JSONArray jsonTrees = jsonTerrain.getJSONArray("trees");
            for (int i = 0; i < jsonTrees.length(); i++) {
                JSONObject jsonTree = jsonTrees.getJSONObject(i);
                float x = (float) jsonTree.getDouble("x");
                float z = (float) jsonTree.getDouble("z");
                terrain.addTree(x, z);
            }
        }
        
        if (jsonTerrain.has("roads")) {
            JSONArray jsonRoads = jsonTerrain.getJSONArray("roads");
            for (int i = 0; i < jsonRoads.length(); i++) {
                JSONObject jsonRoad = jsonRoads.getJSONObject(i);
                float w = (float) jsonRoad.getDouble("width");
                
                JSONArray jsonSpine = jsonRoad.getJSONArray("spine");
                List<Point2D> spine = new ArrayList<Point2D>();
                
                
                for (int j = 0; j < jsonSpine.length()/2; j++) {
                    spine.add(new Point2D((float)jsonSpine.getDouble(2*j), (float)jsonSpine.getDouble(2*j+1)));
                }
                terrain.addRoad(w, spine);
            }
        }
        return terrain;
    }

}
