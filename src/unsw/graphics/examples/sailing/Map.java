package unsw.graphics.examples.sailing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import unsw.graphics.examples.sailing.objects.Island;
import unsw.graphics.examples.sailing.objects.Merchant;
import unsw.graphics.examples.sailing.objects.Pirate;
import unsw.graphics.scene.Scene;


/**
 * COMMENT: Comment Map 
 *
 * @author malcolmr
 */
public class Map {

    private Pirate myPlayer;
    private List<Island> myIslands;
    private List<Merchant> myMerchants;
    
    public Map() {
        myIslands = new ArrayList<Island>();
        myMerchants = new ArrayList<Merchant>();
        myPlayer = null;
    }

    public Pirate player() {
        return myPlayer;
    }
    
    public List<Island> islands() {
        return myIslands;
    }

    public void addIsland(Island island) {
        myIslands.add(island);
    }
    
    public List<Merchant> merchants() {
        return myMerchants;
    }

    public void addMerchant(Merchant merchant) {
        myMerchants.add(merchant);
    }
    
    static public Map read(Scene scene, InputStream in) {
        
        JSONTokener jtk = new JSONTokener(in);
        JSONObject jsonMap = new JSONObject(jtk);

        Map map = new Map();

        JSONObject jsonPlayer = jsonMap.getJSONObject("player");
        map.myPlayer = Pirate.fromJSON(scene.getRoot(), jsonPlayer);

        JSONArray jsonIslands = jsonMap.getJSONArray("islands");
        for (int i = 0; i < jsonIslands.length(); i++) {
            Island island = Island.fromJSON(scene.getRoot(), jsonIslands.getJSONObject(i));
            map.addIsland(island);
        }
        
        JSONArray jsonMerchants = jsonMap.getJSONArray("merchants");
        for (int i = 0; i < jsonMerchants.length(); i++) {
            Merchant merchant = Merchant.fromJSON(scene.getRoot(), jsonMerchants.getJSONObject(i));
            map.addMerchant(merchant);
        }
        
        return map;
    }

}
