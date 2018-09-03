package unsw.graphics.examples;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

public class Arrow extends Application3D implements MouseListener{
	

    private float rotateX = 0;
    private float rotateY = 0;
    private Point2D myMousePoint = null;
    private static final int ROTATION_SCALE = 1;
    
    private List<TriangleMesh> meshes =  new ArrayList<>();
    
    public Arrow() {
        super("Arrow example", 800, 800);
    }

    public static void main(String[] args) {
        Arrow example = new Arrow();
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
        
        makeExtrusion(gl);
    }   
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);

        CoordFrame3D frame = CoordFrame3D.identity()
                .translate(0, 0, -6)
                .rotateX(rotateX)
                .rotateY(rotateY);
        
        Shader.setPenColor(gl, Color.GRAY);
        for (TriangleMesh mesh : meshes)
            mesh.draw(gl, frame);
    }

    private void makeExtrusion(GL3 gl) {        
        //The initial shape we're extruding
        List<Point3D> shape = Arrays.asList(new Point3D(0,0,0),
            new Point3D(0.5f,0.3f,0), 
            new Point3D(0.2f,0.3f,0),
            new Point3D(0.2f,1,0), 
            new Point3D(-0.2f,1,0),
            new Point3D(-0.2f,0.3f,0),
            new Point3D(-0.5f,0.3f,0)
            );
        
        // Indices to draw the shape
        List<Integer> shapeIndices = Arrays.asList(0,1,6, //indices of the points for the triangle part
                2,3,4,4,5,2); //indices for the quad part
        
        // The initial shape as its own mesh
        TriangleMesh front = new TriangleMesh(shape, shapeIndices, true);
        
        Matrix4 m = Matrix4.translation(0, 0, -1).multiply(Matrix4.scale(2, 2, 1)
        		.multiply(Matrix4.rotationZ(90)));
        
        // The extruded shape
        List<Point3D> shapeExt= new ArrayList<>();
        for (Point3D p : shape)
            shapeExt.add(m.multiply(p.asHomogenous()).asPoint3D());
        
        // Indices for the extruded shape
        List<Integer> shapeExtIndices = new ArrayList<>(shapeIndices);
        Collections.reverse(shapeExtIndices);
        
        // The extruded shape as its own mesh
        TriangleMesh back = new TriangleMesh(shapeExt, shapeExtIndices, true);
        
        // We want the sides to have their own normals, so we copy the front and
        // back faces
        List<Point3D> sides = new ArrayList<>();        
        List<Integer> sideIndices = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            //The corners of the quad we will draw as triangles
            Point3D bl = shape.get(i);
            Point3D br = shapeExt.get(i);
            Point3D tl = shape.get((i+1) % 7);
            Point3D tr = shapeExt.get((i+1) % 7);
            
            //First triangle
            sides.add(bl);
            sides.add(br);
            sides.add(tl);
            
            // Second triangle
            sides.add(tl);
            sides.add(br);
            sides.add(tr);
            
            //Indices
//            sideIndices.add(6*i);
//            sideIndices.add(6*i + 1);
//            sideIndices.add(6*i + 2);
//            sideIndices.add(6*i + 3);
//            sideIndices.add(6*i + 4);
//            sideIndices.add(6*i + 5);
        }
        
        TriangleMesh sidesMesh = new TriangleMesh(sides, true);
        
        front.init(gl);
        back.init(gl);
        sidesMesh.init(gl);
        meshes.add(front);
        meshes.add(back);
        meshes.add(sidesMesh);
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
