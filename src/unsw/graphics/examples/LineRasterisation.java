package unsw.graphics.examples;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.Application3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * This example demonstrates line rasterisation algorithms. This is for
 * demonstration purposes only.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class LineRasterisation extends Application3D {
    private static final int RGBA = 4;
    private static final int SIZE = 64;

    // The start and end points of the line.
    private static final int START_X = 20;
    private static final int START_Y = 20;
    private static final int END_X = 50;
    private static final int END_Y = 40;

    // Buffer for a fake framebuffer
    private ByteBuffer frameBuffer;

    private TriangleMesh square;
    private Texture texture;
    private Shader shader;

    public LineRasterisation() {
        super("Line Rasterisation", 800, 800);
        frameBuffer = GLBuffers.newDirectByteBuffer(SIZE * SIZE * RGBA);
        clearBuffer();
    }

    // Draws a line
    private void drawLine(int x0, int y0, int x1, int y1) {
        // Bad
//        float m = (y1 - y0) / (float) (x1 - x0);
//        float y = y0 - m * x0;
//
//        for (int x = x0; x <= x1; x++) {
//            y = y + m;
//            drawPixel(x, (int) Math.round(y));
//        }
    	
    	int y = y0;
    	int w = x1 - x0; int h = y1 - y0;
    	int F = 2*h - w;
    	
    	for (int x = x0; x <= x1; x++) {
    		drawPixel(x,y);
    		if (F < 0) F += 2*h;
    		else {
    			F += 2*(h-w); y++;
    		}
    	}

    }

    private void drawPixel(int x, int y) {
        frameBuffer.put((y * SIZE + x) * RGBA, (byte) 0x00);
        frameBuffer.put((y * SIZE + x) * RGBA + 1, (byte) 0x00);
        frameBuffer.put((y * SIZE + x) * RGBA + 2, (byte) 0x00);
        frameBuffer.put((y * SIZE + x) * RGBA + 3, (byte) 0xFF);
    }

    private void clearBuffer() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                frameBuffer.put((byte) 0xFF);
                frameBuffer.put((byte) 0xFF);
                frameBuffer.put((byte) 0xFF);
                frameBuffer.put((byte) 0xFF);
            }
        }
        frameBuffer.rewind();
    }

    public static void main(String[] args) {
        LineRasterisation example = new LineRasterisation();
        example.start();
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);

        Shader.setPenColor(gl, Color.WHITE);
        // Set current texture
        Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getId());

        square.draw(gl);
    }

    @Override
    public void destroy(GL3 gl) {
        super.destroy(gl);
        texture.destroy(gl);
        shader.destroy(gl);
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);

        drawLine(START_X, START_Y, END_X, END_Y);

        texture = new Texture(gl, frameBuffer, SIZE, false);

        // So that we can actually see the individual pixels
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                GL.GL_NEAREST);

        shader = new Shader(gl, "shaders/vertex_tex_3d.glsl",
                "shaders/fragment_tex_3d.glsl");
        shader.use(gl);

        List<Point3D> verts = new ArrayList<>();
        verts.add(new Point3D(-1, -1, -1));
        verts.add(new Point3D(1, -1, -1));
        verts.add(new Point3D(1, 1, -1));
        verts.add(new Point3D(-1, 1, -1));

        List<Integer> indices = Arrays.asList(0, 1, 3, 1, 2, 3);

        List<Point2D> texCoords = new ArrayList<>();
        texCoords.add(new Point2D(0, 0));
        texCoords.add(new Point2D(1, 0));
        texCoords.add(new Point2D(1, 1));
        texCoords.add(new Point2D(0, 1));

        square = new TriangleMesh(verts, indices, false, texCoords);
        square.init(gl);
    }

}
