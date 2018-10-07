package unsw.graphcs.examples.raytracer;

import java.awt.Color;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;

/**
 * A hit represents the collision of a ray with an object in the scene.
 * 
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Hit {

    private Point3D position;
    private Vector3 normal;
    private Color colour;
    private float shininess;

    public Hit(Point3D position, Vector3 normal, Color colour,
            float shininess) {
        this.position = position;
        this.normal = normal;
        this.colour = colour;
        this.shininess = shininess;
    }

    public Color getColour() {
        return colour;
    }

    public Point3D getPosition() {
        return position;
    }

    public Hit scaleColour(float mag) {
        Color c = new Color(colour.getRed() * mag / 255f,
                colour.getGreen() * mag / 255f, colour.getBlue() * mag / 255f);
        return new Hit(position, normal, c, shininess);
    }

    public Hit addColour(Color colour2) {
        Color c = new Color(Math.min(colour.getRed() + colour2.getRed(), 255),
                Math.min(colour.getGreen() + colour2.getGreen(), 255), 
                Math.min(colour.getBlue() + colour2.getBlue(), 255));
        return new Hit(position, normal, c, shininess);
    }

    public Vector3 getNormal() {
        return normal;
    }

    public float getShininess() {
        return shininess;
    }

}
