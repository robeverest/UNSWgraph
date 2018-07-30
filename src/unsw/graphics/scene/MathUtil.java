package unsw.graphics.scene;

/**
 * A collection of useful math methods 
 *
 * @author malcolmr
 */
public class MathUtil {

    /**
     * Normalise an angle to the range [-180, 180)
     * 
     * @param angle 
     * @return
     */
    public static float normaliseAngle(float angle) {
        return ((angle + 180f) % 360f + 360f) % 360f - 180f;
    }

    /**
     * Clamp a value to the given range
     * 
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
       
}
