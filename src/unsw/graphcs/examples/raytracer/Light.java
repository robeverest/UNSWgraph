package unsw.graphcs.examples.raytracer;

import java.awt.Color;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;

/**
 * Lights in the scene are simple achromatic point lights.
 * @author Robert Clifton-Everest
 *
 */
public class Light {
    private Point3D position;
    
    //Treating all light as achromatic.
    private float diffuse;
    private float specular;

    // The phone exponent is fixed for all materials
    static final int phong = 8;
    
    public Light(Point3D position, float intensity, float specular) {
        this.position = position;
        this.diffuse = intensity;
        this.specular = specular;
    }

    public Point3D getPosition() {
        return position;
    }

    public float getDiffuse() {
        return diffuse;
    }

    public float getSpecular() {
        return specular;
    }

    /**
     * Calculates the new colour of a given Hit after being directly lit with this light. 
     * @param hit
     * @return
     */
    public Color light(Hit hit) {
        Vector3 s = position.minus(hit.getPosition()).normalize();
        Vector3 r = hit.getNormal().scale(2*s.dotp(hit.getNormal())).minus(s);
        Vector3 v = new Point3D(0, 0, 0).minus(position);
        
        //The diffuse magnitude
        float diff_mag = getDiffuse() * s.dotp(hit.getNormal());
        
        //The specular magnitude
        float spec_mag = r.normalize().dotp(v.normalize());
        
        //If surface is facing us
        if (diff_mag > 0) 
            spec_mag = getSpecular() * hit.getShininess() * (float) Math.pow(spec_mag, Light.phong);
        else 
            spec_mag = diff_mag = 0;
        
        //The final colour. We assume that everything has white specular highlights.
        hit = hit.scaleColour(diff_mag).addColour(new Color(spec_mag, spec_mag, spec_mag));        
        return hit.getColour();
    }
  
}
