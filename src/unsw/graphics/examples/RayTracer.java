package unsw.graphics.examples;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Polygon2D;

public class RayTracer extends Application2D {

    private Shader shader;

    private int time;

    public RayTracer() {
        super("Ray tracer", 800, 800);
    }

    public static void main(String[] args) {
        RayTracer example = new RayTracer();
        example.start();
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);

        shader = new Shader(gl, "shaders/vertex_2d.glsl", "shaders/fragment_raytracer.glsl");
        shader.use(gl);

        int[] viewport = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

        Shader.setInt(gl, "windowWidth", viewport[2]);
        Shader.setInt(gl, "windowHeight", viewport[3]);
        getAnimator().setUpdateFPSFrames(1, null);
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);

        Shader.setInt(gl, "time", time);
        Polygon2D quad = new Polygon2D(-1,-1, 1,-1, 1,1, -1,1);
        quad.draw(gl);
        time++;

        System.out.println("FPS:" + getAnimator().getLastFPS());
    }

}
