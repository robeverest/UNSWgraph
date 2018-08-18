package unsw.graphics.examples;

import java.awt.Color;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleFan3D;
import unsw.graphics.scene.MathUtil;

/**
 * A simple example that draws a cube with flat shading.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class FlatShadedCube extends Application3D {
    
    private float rotationX, rotationY;
    
    //Global properties of the light
    private float ambientIntensity;
    private Point3D lightPos;
    private float lightIntensity;
    
    // Properties of the material
    private float ambientCoefficient;
    private float diffuseCoefficient;
    private float specularCoefficient;
    private float phongExponent;

    public FlatShadedCube() {
        super("Cube", 600, 600);
        rotationX = 0;
        rotationY = 0;
        
        lightPos = new Point3D(0,0,3);
        ambientIntensity = 0.1f;
        lightIntensity = 1f;
        
        ambientCoefficient = 1;
        diffuseCoefficient = 0.4f;
        specularCoefficient = 0.2f;
        phongExponent = 8;
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, 1, 1, 10));
    }

    public static void main(String[] args) {
        FlatShadedCube example = new FlatShadedCube();
        example.start();
    }
    
    @Override
    public void display(GL3 gl) {
        super.display(gl);
        CoordFrame3D frame = CoordFrame3D.identity()
                .translate(0, 0, -2)
                .scale(0.5f, 0.5f, 0.5f);
        drawCube(gl, frame.rotateY(rotationY).rotateX(rotationX));
//        rotationX += 1;
        rotationY += 1;
    }

    /**
     * Draw a cube centered around (0,0) with bounds of length 1 in each direction.
     * @param gl
     * @param frame
     */
    private void drawCube(GL3 gl, CoordFrame3D frame) {
        TriangleFan3D face = new TriangleFan3D(-1,-1,1, 1,-1,1, 1,1,1, -1,1,1);
        
        // Front
        Shader.setPenColor(gl, shading(frame, new Point3D(0, 0, 1), new Vector3(0, 0, 1)));
        face.draw(gl, frame);
        
        // Left
        Shader.setPenColor(gl, shading(frame, new Point3D(-1, 0, 0), new Vector3(-1, 0, 0)));
        face.draw(gl, frame.rotateY(-90));
        
        // Right
        Shader.setPenColor(gl, shading(frame, new Point3D(1, 0, 0), new Vector3(1, 0, 0)));
        face.draw(gl, frame.rotateY(90));
        
        // Back
        Shader.setPenColor(gl, shading(frame, new Point3D(0, 0, -1), new Vector3(0, 0, -1)));
        face.draw(gl, frame.rotateY(180));
        
        // Bottom
        Shader.setPenColor(gl, shading(frame, new Point3D(0, -1, 0), new Vector3(0, -1, 0)));
        face.draw(gl, frame.rotateX(-90));
        
        // Top
        Shader.setPenColor(gl, shading(frame, new Point3D(0, 1, 0), new Vector3(0, 1, 0)));
        face.draw(gl, frame.rotateX(90));
    }

    private Color shading(CoordFrame3D frame, Point3D point, Vector3 normal) {
        // Compute the point and the normal in global coordinates
        Point3D p = frame.transform(point);
        Vector3 m = frame.transform(normal).normalize();
        
        // The vector from the point to the light source. 
        Vector3 s = lightPos.minus(p).normalize();
        
        // The ambient intensity (same for all points)
        float ambient = ambientIntensity * ambientCoefficient;
        
        // The diffuse intensity at this point
        float diffuse = lightIntensity * diffuseCoefficient * s.dotp(m);
        
        // The vector from the point to the camera
        // Note: we're assuming the view transform is the identity transform
        Vector3 v = p.asHomogenous().trim().scale(-1).normalize(); //v = normalize(-p)
       
        // The reflected vector
        Vector3 r = s.negate().plus(m.scale(2*s.dotp(m)));
        
        // The specular intensity at this point
        float specular = lightIntensity * specularCoefficient 
                * (float) Math.pow(r.dotp(v), phongExponent);
        
        float intensity = MathUtil.clamp(ambient + diffuse + specular, 0, 1);
        return new Color(intensity, intensity, intensity);
    }
}