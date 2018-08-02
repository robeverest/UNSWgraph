package unsw.graphics.examples;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.CoordFrame2D;
import unsw.graphics.Matrix3;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Polygon2D;

public class Mandelbrot extends Application2D {

    private float aspectRatio;
    
    private Shader shader;

    private float zoom;

    private Point2D cameraPos;

    public Mandelbrot() {
        super("Mandelbrot", 1024, 768);
        zoom = 0.5f;
        cameraPos = new Point2D(0, 0);
    }

    public static void main(String[] args) {
        Mandelbrot example = new Mandelbrot();
        example.start();
    }
    
    @Override
    public void init(GL3 gl) {
        super.init(gl);
      
        // Shaders take time to load and compile, so do this on initialisation.
        shader = new Shader(gl, "shaders/vertex_mandelbrot.glsl", "shaders/fragment_mandelbrot.glsl");
        
        getWindow().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ev) {
                if (ev.getKeyCode() == KeyEvent.VK_LEFT)
                    cameraPos = cameraPos.translate(-0.02f/zoom, 0);
                else if (ev.getKeyCode() == KeyEvent.VK_RIGHT)
                    cameraPos = cameraPos.translate(0.02f/zoom, 0);
                else if (ev.getKeyCode() == KeyEvent.VK_UP)
                    cameraPos = cameraPos.translate(0, 0.02f/zoom);
                else if (ev.getKeyCode() == KeyEvent.VK_DOWN)
                    cameraPos = cameraPos.translate(0, -0.02f/zoom);
            }
        });
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);
        CoordFrame2D view = CoordFrame2D.identity()
                .scale(1/aspectRatio, 1)
                .scale(zoom, zoom)
                .translate(-cameraPos.getX(), -cameraPos.getY());
        Polygon2D quad = new Polygon2D(-2,-1, 1,-1, 1,1, -2,1);
           
        shader.use(gl);
        
        //Using a new shader invalidates all the values we've previously provided as uniform inputs.
        //Need to supply them again.
        Shader.setViewMatrix(gl, view.getMatrix());
        
        //Just drawing a standard 2x1 quad.
        quad.draw(gl);
        getDefaultShader().use(gl);
        
        zoom *= 1.01;
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        aspectRatio = (float) width / height;
    }

}
