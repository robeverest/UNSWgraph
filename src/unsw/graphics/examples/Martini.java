package unsw.graphics.examples;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * Extrude an arrow along the z-axis
 * 
 * @author Robert Clifton-Everest
 */

public class Martini extends Application3D implements MouseListener{
	

    private static final int NUM_SLICES = 32;
    private float rotateX = 0;
    private float rotateY = 0;
    private Point2D myMousePoint = null;
    private static final int ROTATION_SCALE = 1;
    
    private List<TriangleMesh> meshes =  new ArrayList<>();
    
    public Martini() {
        super("Martini example", 800, 800);
    }

    public static void main(String[] args) {
        Martini example = new Martini();
        example.start();
    }
    
    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, 1, 1, 100));
    }
    
    @Override
    public void init(GL3 gl) {
        super.init(gl);
        getWindow().addMouseListener(this);
        
        Shader shader = new Shader(gl, "shaders/vertex_phong.glsl",
                "shaders/fragment_phong.glsl");
        shader.use(gl);
        
        // Set the lighting properties
        Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
        Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
        
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
        Shader.setFloat(gl, "phongExp", 16f);
        
        makeMartini(gl);
    }   
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);

        CoordFrame3D frame = CoordFrame3D.identity()
                .translate(0, 0, -3)
                .rotateX(rotateX)
                .rotateY(rotateY);
        
        Shader.setPenColor(gl, Color.GRAY);
        for (TriangleMesh mesh : meshes)
            mesh.draw(gl, frame);
    }

    private void makeMartini(GL3 gl) {        
        float[] x = {0.26f,0.26f,0.09f,0.07f,0.04f,0.02f,0.02f,0.05f,0.14f,0.41f,0.4f,0.11f,0.04f,0};       
        float[] y = {0,0,0.02f,0.02f,0.05f,0.05f,0.38f,0.46f,0.56f,0.98f,0.99f,0.57f,0.51f,0.5f};
        
        for (int i = 0; i < x.length - 1; i++) {
            List<Point3D> points = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();
            for (int j = 0; j < NUM_SLICES; j++) {
                float theta = 360*j/(float) NUM_SLICES;
                float x1=(float) (x[i]*Math.cos(theta*2*Math.PI/360f)); 
                float x2=(float) (x[i+1]*Math.cos(theta*2*Math.PI/360f)); 
                float z1=(float) (x[i]*Math.sin(theta*2*Math.PI/360f));  
                float z2=(float) (x[i+1]*Math.sin(theta*2*Math.PI/360f));
                
                points.add(new Point3D(x1, y[i], z1));
                points.add(new Point3D(x2, y[i+1], z2));
                
                // The index of the next slice
                int k = (j + 1) % NUM_SLICES;
                
                indices.add(2*j);
                indices.add(2*j + 1);
                indices.add(2*k + 1);
                
                indices.add(2*j);
                indices.add(2*k + 1);
                indices.add(2*k);
            }
            TriangleMesh segment = new TriangleMesh(points, indices, true);
            segment.init(gl);
            meshes.add(segment);
        }
    }

   
	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D p = new Point2D(e.getX(), e.getY());

        if (myMousePoint != null) {
            float dx = p.getX() - myMousePoint.getX();
            float dy = p.getY() - myMousePoint.getY();

            // Note: dragging in the x dir rotates about y
            //       dragging in the y dir rotates about x
            rotateY += dx * ROTATION_SCALE;
            rotateX += dy * ROTATION_SCALE;

        }
        myMousePoint = p;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		 myMousePoint = new Point2D(e.getX(), e.getY());
	}

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseWheelMoved(MouseEvent e) { }
}
