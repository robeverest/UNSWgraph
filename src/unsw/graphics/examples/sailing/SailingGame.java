package unsw.graphics.examples.sailing;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.examples.sailing.objects.CameraHarness;
import unsw.graphics.examples.sailing.objects.Mouse;
import unsw.graphics.examples.sailing.objects.Pirate;
import unsw.graphics.scene.Camera;
import unsw.graphics.scene.Scene;

/**
 * COMMENT: A Sailing 'Game' that uses UNSWgraph
 *          This will not work until you have implemented the relevant
 *          assignment 1 classes. 
 *
 * @author malcolmr
 */
public class SailingGame extends Application2D {

    private static final Color WATER_COLOR = new Color(0.1f, 0.3f, 1f);
    private static final String MAP_FILE = "res/sailing/map.json";

    private Map myMap;
    private Scene scene;
    
    private SailingGame() {
        super("Sailing Game", 1024, 768);
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);

        scene = new Scene();
        
        try {
            readMap(new File(MAP_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // attach the camera to the player
        Pirate player = myMap.player();
        Camera camera = new Camera(new CameraHarness(scene.getRoot(), player));
        camera.scale(20);
        
        scene.setCamera(camera);
        setBackground(WATER_COLOR);
      
        getWindow().addMouseListener(Mouse.theMouse);     
    }

    public void readMap(File mapFile) throws IOException {
        InputStream in = new FileInputStream(mapFile);
        myMap = Map.read(scene, in);
        in.close();
    }
    
    @Override
    public void reshape(GL3 gl, int width, int height) {
        scene.reshape(width, height);
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        Mouse.theMouse.update(scene.getCamera(), getWindow());
        scene.draw(gl);
    }

    public static void main(String[] args) throws IOException {
        SailingGame game = new SailingGame();
        game.start();
    }

}
