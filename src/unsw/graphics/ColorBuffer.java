/**
 * 
 */
package unsw.graphics;

import java.awt.Color;
import java.nio.Buffer;
import java.nio.FloatBuffer;

import com.jogamp.opengl.util.GLBuffers;

/**
 * A buffer of color values. Can be passed to OpenGL commands that expect 
 * buffers by using the getBuffer() method.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class ColorBuffer {

    private FloatBuffer floatBuffer;

    private int capacity;

    /**
     * Construct a new buffer with the given capacity.
     * 
     * @param capacity
     */
    public ColorBuffer(int capacity) {
        // Buffer stores pairs of floats
        this.capacity = capacity;
        floatBuffer = GLBuffers.newDirectFloatBuffer(capacity * 4);
    }

    /**
     * Add a {@link Color} to the buffer at the given index.
     * 
     * @param index
     * @param p
     */
    public void put(int index, Color c) {
        put(index, c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f);
    }

    /**
     * Add a color (given as an r-g-b-a values) to the buffer at the given
     * index.
     * 
     * @param index
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public void put(int index, float r, float g, float b, float a) {
        if (index >= 0 && index < capacity) {
            floatBuffer.put(index * 4, r);
            floatBuffer.put(index * 4 + 1, g);
            floatBuffer.put(index * 4 + 2, b);
            floatBuffer.put(index * 4 + 3, a);
        } else {
            throw new IndexOutOfBoundsException(
                    "index: " + index + ", capacity: " + capacity);
        }
    }

    public Buffer getBuffer() {
        return floatBuffer;
    }

    public int capacity() {
        return capacity;
    }

}
