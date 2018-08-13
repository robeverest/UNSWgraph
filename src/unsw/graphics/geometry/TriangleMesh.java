/**
 * 
 */
package unsw.graphics.geometry;

import java.io.IOException;
import java.nio.IntBuffer;

import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;
import org.smurn.jply.util.NormalMode;
import org.smurn.jply.util.NormalizingPlyReader;
import org.smurn.jply.util.TesselationMode;
import org.smurn.jply.util.TextureMode;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;

/**
 * A triangle mesh in 3D space
 *
 * The mesh must be initialised before use. During initialisation the data will
 * be copied to graphics memory to avoid unnecessary repeated copying.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class TriangleMesh {

    /**
     * Contains the vertices of all triangles that make up the mesh.
     */
    private Point3DBuffer vertices;
    
    /**
     * Contains indices into the buffer of vertices. Each set of 3 indices forms a triangle.
     */
    private IntBuffer indices;

    /**
     * The name of the vertex buffer according to OpenGL
     */
    private int verticesName;
    
    /**
     * The name of the indices buffer according to OpenGL
     */
    private int indicesName;

    /**
     * Construct a triangle with the given PLY file.
     * 
     * @param plyFile
     * @throws IOException
     */
    public TriangleMesh(String plyFile) throws IOException {
        // Setup an initial reader
        PlyReader rawReader = new PlyReaderFile(plyFile);

        // Use a normalizing reader to get mesh only containing triangles
        NormalizingPlyReader reader = new NormalizingPlyReader(rawReader,
                TesselationMode.TRIANGLES, NormalMode.PASS_THROUGH,
                TextureMode.PASS_THROUGH);

        vertices = new Point3DBuffer(reader.getElementCount("vertex"));
        indices = GLBuffers.newDirectIntBuffer(reader.getElementCount("face") * 3);

        int verticesIndex = 0;
        int indicesIndex =  0;
        
        ElementReader elReader = reader.nextElementReader();
        while (elReader != null) {
            if (elReader.getElementType().getName().equals("vertex")) {
                verticesIndex = readVertices(verticesIndex, elReader);
            } else if (elReader.getElementType().getName().equals("face")) {
                indicesIndex = readIndices(indicesIndex, elReader);
            }
            elReader = reader.nextElementReader();
        }

    }

    private int readIndices(int indicesIndex, ElementReader elReader) throws IOException {
        Element triangle = elReader.readElement();
        while (triangle != null) {

            int[] vertIndices = triangle.getIntList("vertex_index");
            for (int index : vertIndices) {
                indices.put(indicesIndex, index);
                indicesIndex++;
            }
            triangle = elReader.readElement();
        }
        return indicesIndex;
    }

    private int readVertices(int verticesIndex, ElementReader elReader)
            throws IOException {
        Element vertex = elReader.readElement();
        while (vertex != null) {
            float x = (float) vertex.getDouble("x");
            float y = (float) vertex.getDouble("y");
            float z = (float) vertex.getDouble("z");
            vertices.put(verticesIndex, x, y, z);
            verticesIndex++;
            
            vertex = elReader.readElement();
        }
        return verticesIndex;
    }

    public void init(GL3 gl) {
        // Generate the names for the buffers.
        int[] names = new int[2];
        gl.glGenBuffers(2, names, 0);
        verticesName = names[0];
        indicesName = names[1];
        
        // Copy the data for the vertices
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                vertices.capacity() * 3 * Float.BYTES, vertices.getBuffer(),
                GL.GL_STATIC_DRAW);
        
        // Copy the data for the indices
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER,
                indices.capacity() * Integer.BYTES, indices,
                GL.GL_STATIC_DRAW);
    }

    public void draw(GL3 gl, CoordFrame3D frame) {
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawElements(GL3.GL_TRIANGLES, indices.capacity(), GL.GL_UNSIGNED_INT, 0);
    }

    public void destroy(GL3 gl) {
        gl.glDeleteBuffers(2, new int[] { verticesName, indicesName }, 0);
    }

    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
}
