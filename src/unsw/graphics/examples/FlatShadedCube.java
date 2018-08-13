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
    private float ambient;
    private Point3D lightPos;
    private float diffuseMaterial;
    private float diffuseIntensity;

    public FlatShadedCube() {
        super("Cube", 600, 600);
        rotationX = 0;
        rotationY = 0;
        
        lightPos = new Point3D(0,0,3);
        ambient = 0.1f;
        diffuseIntensity = 0.3f;
        diffuseMaterial = 1;
        
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
        rotationX += 1;
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
        Point3D p = frame.transform(point);
        Vector3 n = frame.transform(normal);
        
        Vector3 l = lightPos.minus(p);
        float diffuse = diffuseIntensity * diffuseMaterial * n.dotp(l);
        float intensity = MathUtil.clamp(ambient + diffuse, 0, 1);; // + specular;
        return new Color(intensity, intensity, intensity);
    }
}