package unsw.graphcs.examples.raytracer;

import java.awt.Color;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;

/**
 * An object in the ray traced scene.
 * 
 * Currently, we support translating an object but no other transformations.
 * 
 * @author Robert Clifton-Everest
 *
 */
public abstract class SceneObject {
    private Point3D position;

    /**
     * The shininess represents both the specular coefficient (rho_s) and how
     * reflective this object actually is.
     */
    private float shininess;

    public SceneObject(Point3D position, float shininess) {
        this.position = position;
        this.shininess = shininess;
    }

    /**
     * Returns the lowest value of t at which the given ray collides with this
     * object. Returns positive infinity if there are no intersections.
     * 
     * @param r
     * @return
     */
    public abstract float intersectWithRay(Ray r);

    /**
     * Get the normal at the given point. Undefined for points not on the
     * object.
     * 
     * @param p_hit
     * @return
     */
    public abstract Vector3 getNormal(Point3D p_hit);

    /**
     * Get the colour at the given point. Undefined for points not on the
     * object.
     * 
     * @param pos
     * @return
     */
    public abstract Color getColour(Point3D pos);

    /**
     * Transform the ray into this objects coordinate system. As objects in this
     * example only support translation and not rotation or scale this is
     * trivial to calculate.
     * 
     * @param r
     * @return
     */
    public Ray transformRay(Ray r) {
        return new Ray(
                r.getPosition()
                        .translate(position.asHomogenous().trim().negate()),
                r.getDirection());
    }

    public float getShininess() {
        return shininess;
    }

    public Point3D getPosition() {
        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

}
