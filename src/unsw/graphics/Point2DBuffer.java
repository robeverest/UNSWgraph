/**
 * 
 */
package unsw.graphics;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.List;

import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.geometry.Point2D;

/**
 * A buffer of points. Can be passed to OpenGL commands that expect buffers by
 * using the getBuffer() method.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Point2DBuffer {

    private FloatBuffer floatBuffer;

    private int capacity;

    /**
     * Construct a new buffer with the given capacity.
     * 
     * @param capacity
     */
    public Point2DBuffer(int capacity) {
        // Buffer stores pairs of floats
        this.capacity = capacity;
        floatBuffer = GLBuffers.newDirectFloatBuffer(capacity * 2);
    }

    public Point2DBuffer(List<Point2D> points) {
        this(points.size());
        for (int i = 0; i < capacity; i++) {
            put(i, points.get(i));
        }
    }

    /**
     * Add a {@link Point2D} to the buffer at the given index.
     * 
     * @param index
     * @param p
     */
    public void put(int index, Point2D p) {
        put(index, p.getX(), p.getY());
    }

    /**
     * Add a point (given as an x-y coordinate) to the buffer at the given
     * index.
     * 
     * @param index
     * @param x
     * @param y
     */
    public void put(int index, float x, float y) {
        if (index >= 0 && index < capacity) {
            floatBuffer.put(index * 2, x);
            floatBuffer.put(index * 2 + 1, y);
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
