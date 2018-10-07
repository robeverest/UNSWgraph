package unsw.graphcs.examples.raytracer;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.Application3D;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;

/**
 * This example ray traces a simple scene with two shiny spheres, a checkerboard and a
 * light. It is trivial to add additional objects and lights to the scene if desired.
 * 
 * As it is intended to be illustrative, this example lacks many potential optimisations and features.
 * @author Robert Clifton-Everest
 *
 */
public class RayTracerThreaded extends Application3D {
    //The background colour
    public static final Color background = Color.WHITE;
    
    //The ambient light (using achromatic light)
    public static final float ambient = 0.4f;
    
    private static final int RGBA = 4;
    private static final int FRAMEBUFFER_SIZE = 800;
    
    //Buffer for a fake framebuffer
    private ByteBuffer frameBuf = GLBuffers.newDirectByteBuffer(FRAMEBUFFER_SIZE*FRAMEBUFFER_SIZE*RGBA);
    private Texture texture;
    
    //The objects in our simple scene.
    private SceneObject sphere1;
    private SceneObject sphere2;
    private List<SceneObject> objects;
    private List<Light> lights;
    
    //The rotation of the spheres around the center
    private float theta;
    
    private static int NTHREADS = 8; 

    private Shader shader;
    
    public RayTracerThreaded() {
        super("Ray tracer", FRAMEBUFFER_SIZE, FRAMEBUFFER_SIZE);
        objects = new ArrayList<SceneObject>();
        objects.add(new Plane(new Point3D(0,-1,0), 0));
        sphere1 = new Sphere(new Point3D(1,0,-4), 0.5f, new Color(0.4f,0.4f,0));
        sphere2 = new Sphere(new Point3D(-1,0,-4), 0.5f, new Color(0.4f,0,0.4f));
        
        objects.add(sphere1);
        objects.add(sphere2);
        
        lights = new ArrayList<Light>();
        lights.add(new Light(new Point3D(1, 3, -2), 0.8f, 1f));
    }
    
    private void drawPixel(int x, int y, Color color) {
        frameBuf.put((y * FRAMEBUFFER_SIZE + x)*RGBA, (byte) color.getRed());
        frameBuf.put((y * FRAMEBUFFER_SIZE + x)*RGBA + 1, (byte) color.getGreen());
        frameBuf.put((y * FRAMEBUFFER_SIZE + x)*RGBA + 2, (byte) color.getBlue());
        frameBuf.put((y * FRAMEBUFFER_SIZE + x)*RGBA + 3, (byte) 255);
    }
    
    //Construct a ray that goes from (0,0,0) through the given pixel on the screen.
    private Ray rayThroughPixel(int x, int y) {
        float w = 1;
        float h = 1;
        float c = FRAMEBUFFER_SIZE;
        float r = FRAMEBUFFER_SIZE;
        float n = 1.73f; //This gives a FOV of approx 60 degrees
        
        float i_c = w * (2*x/c - 1);
        float j_r = h * (2*y/r - 1);
        
        Point3D pos = new Point3D(0,0,0);
        
        Vector3 dir = new Vector3(i_c, j_r, -n);
        
        return new Ray(pos,dir);
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        
        updateScene();
        
        drawScene();
        
        gl.glActiveTexture(GL.GL_TEXTURE0);
        Shader.setInt(gl, "tex", 0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, FRAMEBUFFER_SIZE, 
                FRAMEBUFFER_SIZE, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, frameBuf);
        System.out.println("FPS: " + String.format("%5.2f", getAnimator().getLastFPS()));
        
        int[] names = new int[4];
        gl.glGenBuffers(4, names, 0);

        Shader.setPenColor(gl, Color.WHITE);
        Point3DBuffer quad = new Point3DBuffer(4);
        quad.put(0, -1, -1, -1);
        quad.put(1, 1, -1, -1);
        quad.put(2, 1, 1, -1);
        quad.put(3, -1, 1, -1);

        Point2DBuffer quadTexCoords = new Point2DBuffer(4);
        quadTexCoords.put(0, 0f, 0f);
        quadTexCoords.put(1, 1f, 0f);
        quadTexCoords.put(2, 1f, 1f);
        quadTexCoords.put(3, 0f, 1f);

        // Copy across the buffer for the vertex positions
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, quad.capacity() * 3 * Float.BYTES,
                quad.getBuffer(), GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);

        // Copy across the buffer for the texture coordinates
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[1]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                quadTexCoords.capacity() * 2 * Float.BYTES,
                quadTexCoords.getBuffer(), GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);

        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, quad.capacity());
    }

    //Just rotate the spheres for now
    private void updateScene() {
        sphere1.setPosition(new Point3D((float)Math.cos(theta), 0, -4 + (float)Math.sin(theta)));
        sphere2.setPosition(new Point3D((float)-Math.cos(theta), 0, -4 - (float)Math.sin(theta)));
        theta += 0.1;
    }
    
    //Draws the scene
    private void drawScene(){
        int segmentHeight = FRAMEBUFFER_SIZE/NTHREADS;
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < NTHREADS; i = i + 1) {
            final int i2 = i; //Java weirdness
            Thread thread = new Thread() {
            public void run() {
            for (int y = i2*segmentHeight; y < (i2+1)*segmentHeight; y++) {
                for (int x = i2; x < FRAMEBUFFER_SIZE; x++) {
                    //Make a ray
                    Ray r = rayThroughPixel(x,y);
                    
                    //Offset the ray so it now starts at t = 1 (i.e. at the screen).
                    r = r.offset(1);
                    
                    Hit hit = traceRayWithLight(r);
                    
                    //Trace reflected rays.
                    Hit hit2 = hit;
                    float shininess = 1;
                    int count = 0;
                  
                    while (hit2 != null && count < 5 && shininess >= 0.001) {
                        shininess *= hit2.getShininess();
                        //If we're no longer very shiny we're not going to be reflecting much
                        if (shininess < 0.001)
                            break;
                        r = r.reflectAround(hit2.getPosition(), hit2.getNormal());
                        hit2 = traceRayWithLight(r.offset(0.001f));
                    
                        if (hit2 != null)
                            hit = hit.addColour(hit2.scaleColour(shininess).getColour());
                     
                    }
                    if (hit == null)
                        drawPixel(x, y, background);
                    else
                        drawPixel(x, y, hit.getColour());
                }
            }
            }
            };
            thread.start();
            threads.add(thread);
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private Hit traceRay(Ray r) {
        float min_t_hit = Float.POSITIVE_INFINITY;
        SceneObject min_so = null;
        
        for (SceneObject so : objects) {
            //Transform the ray by the inverse of the objects transformation
            Ray r_t = so.transformRay(r);
            
            //See if the ray collides with the object
            float t_hit = so.intersectWithRay(r_t);
            
            //If we have a hit, check to see if it is closer than any previous hits.
            if (t_hit >= 0 && t_hit < min_t_hit) {
                min_t_hit = t_hit;
                min_so = so;
            }
        }
       
        //Get the colour, normal and shininess of the object at that point
        Point3D p_hit = r.pointAt(min_t_hit);
        Hit s = null;
        if (min_t_hit != Float.POSITIVE_INFINITY) {
            s = new Hit(p_hit, min_so.getNormal(p_hit), min_so.getColour(p_hit), min_so.getShininess());
        }
        return s;
    }

    private Hit traceRayWithLight(Ray r) {
        Hit lit = null;
        
        //Get the sample from just firing a single ray
        Hit unlit = traceRay(r);
        
        if (unlit != null) {
            //Start by just adding ambient light
            lit = unlit.scaleColour(ambient);
        
            //Send out shadow feelers to all the lights
            for (Light light : lights) {
                Ray feeler = Ray.betweenPoints(unlit.getPosition(), light.getPosition());
                // Find occluding objects, ignore objects hit at starting point of ray to avoid 
                // self collision issues.
                Hit occlusion = traceRay(feeler.offset(0.0001f));
                if (occlusion == null) {
                    //Nothing in between this point and the light so add this lights contribution
                    //to the hit.
                    lit = lit.addColour(light.light(unlit));
                }
            }
        }
        return lit;
    }
    
    public static void main(String[] args) {
        RayTracerThreaded example = new RayTracerThreaded();
        example.start();
    }
    
    @Override
    public void init(GL3 gl) {
        super.init(gl);
        getAnimator().setUpdateFPSFrames(1, null);
        texture = new Texture(gl, frameBuf, FRAMEBUFFER_SIZE, false);
        shader = new Shader(gl, "shaders/vertex_tex_3d.glsl", "shaders/fragment_tex_3d.glsl");
        shader.use(gl);
    }
}
