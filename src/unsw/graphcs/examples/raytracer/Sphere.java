package unsw.graphcs.examples.raytracer;

import java.awt.Color;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;

/**
 * A unit-sphere of a particular colour and shininess.
 * @author Robert Clifton-Everest
 *
 */
public class Sphere extends SceneObject {

    private Color colour;
    
    public Sphere(Point3D position, float shininess, Color colour) {
        super(position, shininess);
        this.colour = colour;
    }

    @Override
    public float intersectWithRay(Ray r) {
        // We treat E as a vector as that is how we calculate with it.
        Vector3 E = r.getPosition().asHomogenous().trim(); 
        Vector3 v = r.getDirection();
        
        //Solve via quadratic formula
        float a = v.dotp(v); // |v|^2 = v . v
        float b = 2*(E.dotp(v));
        float c = E.dotp(E) - 1;
        
        float d = b*b - 4*a*c;
        
        if (d >= 0) {
            //We have at least one solution
            float t1 = (-b + (float)Math.sqrt(d)) / (2*a);
            float t2 = (-b - (float)Math.sqrt(d)) / (2*a);
            if (t1 > 0 && t2 > 0) 
                return Math.min(t1, t2);
            else if (t1 < 0 && t2 < 0)
                return Float.POSITIVE_INFINITY;
            else
                return Math.max(t1, t2);
        } else return Float.POSITIVE_INFINITY;
    }

    @Override
    public Color getColour(Point3D pos) {
        return colour; 
    }

    @Override
    public Vector3 getNormal(Point3D p) {
        return p.minus(getPosition()).normalize();
    }



}
