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
import unsw.graphics.Vector3;

/**
 * A triangle mesh in 3D space
 *
 * The mesh must be initialised before use. During initialisation the data will
 * be copied to graphics memory to avoid unnecessary repeated copying.
 *
 * The normals computed during construction of this mesh are NOT normalised.
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
     * Contains the normals for all vertices.
     */
    private Point3DBuffer normals;

    /**
     * Contains indices into the buffer of vertices and normals. Each set of 3
     * indices forms a triangle.
     */
    private IntBuffer indices;

    /**
     * The name of the vertex buffer according to OpenGL
     */
    private int verticesName;

    /**
     * The name of the normal buffer according to OpenGL
     */
    private int normalsName;

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
        this(plyFile, false);
    }

    /**
     * Construct a triangle with the given PLY file. The third argument
     * indicates whether to generate vertex normals. If false, no normals are
     * generated.
     *
     * @param plyFile
     * @param vertexNormals
     * @throws IOException
     */
    public TriangleMesh(String plyFile, boolean vertexNormals)
            throws IOException {
        // Setup an initial reader
        PlyReader rawReader = new PlyReaderFile(plyFile);

        // Use a normalizing reader to get mesh only containing triangles
        NormalizingPlyReader reader = new NormalizingPlyReader(rawReader,
                TesselationMode.TRIANGLES, NormalMode.PASS_THROUGH,
                TextureMode.PASS_THROUGH);

        vertices = new Point3DBuffer(reader.getElementCount("vertex"));
        indices = GLBuffers
                .newDirectIntBuffer(reader.getElementCount("face") * 3);
        if (vertexNormals)
            normals = new Point3DBuffer(reader.getElementCount("vertex"));

        ElementReader elReader = reader.nextElementReader();
        while (elReader != null) {
            if (elReader.getElementType().getName().equals("vertex")) {
                readVertices(elReader);
            } else if (elReader.getElementType().getName().equals("face")) {
                readIndices(elReader);
            }
            elReader = reader.nextElementReader();
        }

        //Compute the normals
        if (vertexNormals)
            computeNormals();
    }

    /**
     * Compute normals for the mesh. Note that they are not normalised normals.
     * If a shader depends on the normals, it must normalise them internally.
     */
    private void computeNormals() {
        // Initialise the normals to the zero vector
        for (int i = 0; i < normals.capacity(); i++) {
            normals.put(i, 0, 0, 0);
        }

        // Add the face normals of all surrounding faces.
        for (int i = 0; i < indices.capacity() / 3; i++) {
            int index1 = indices.get(i*3);
            int index2 = indices.get(i*3 + 1);
            int index3 = indices.get(i*3 + 2);

            Point3D p1 = vertices.get(index1);
            Point3D p2 = vertices.get(index2);
            Point3D p3 = vertices.get(index3);

            Vector3 normal = normal(p1, p2, p3);

            normals.put(index1, normals.get(index1).translate(normal));
            normals.put(index2, normals.get(index2).translate(normal));
            normals.put(index3, normals.get(index3).translate(normal));
        }
    }

    /**
     * For the given 3 points that make up a triangle, compute the face normal.
     * This face normal is proportional in length to the size of the face. This
     * allows for the computing of a weighted average.
     *
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    private Vector3 normal(Point3D p1, Point3D p2, Point3D p3) {
        Vector3 a = p2.minus(p1);
        Vector3 b = p3.minus(p1);

        return a.cross(b);
    }

    private void readIndices(ElementReader elReader)
            throws IOException {
        int indicesIndex = 0;
        Element triangle = elReader.readElement();
        while (triangle != null) {

            int[] vertIndices = triangle.getIntList("vertex_index");
            for (int index : vertIndices) {
                indices.put(indicesIndex, index);
                indicesIndex++;
            }
            triangle = elReader.readElement();
        }
    }

    private void readVertices(ElementReader elReader)
            throws IOException {
        int verticesIndex = 0;
        Element vertex = elReader.readElement();
        while (vertex != null) {
            float x = (float) vertex.getDouble("x");
            float y = (float) vertex.getDouble("y");
            float z = (float) vertex.getDouble("z");
            vertices.put(verticesIndex, x, y, z);
            verticesIndex++;
            vertex = elReader.readElement();
        }
    }

    public void init(GL3 gl) {
        // Generate the names for the buffers.
        int[] names = new int[3];
        gl.glGenBuffers(3, names, 0);
        verticesName = names[0];
        indicesName = names[1];
        normalsName = names[2];

        // Copy the data for the vertices
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                vertices.capacity() * 3 * Float.BYTES, vertices.getBuffer(),
                GL.GL_STATIC_DRAW);

        if (normals != null) {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalsName);
            gl.glBufferData(GL.GL_ARRAY_BUFFER,
                    normals.capacity() * 3 * Float.BYTES, normals.getBuffer(),
                    GL.GL_STATIC_DRAW);
        }

        // Copy the data for the indices
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER,
                indices.capacity() * Integer.BYTES, indices, GL.GL_STATIC_DRAW);
    }

    public void draw(GL3 gl, CoordFrame3D frame) {
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        if (normals != null) {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalsName);
            gl.glVertexAttribPointer(Shader.NORMAL, 3, GL.GL_FLOAT, false, 0, 0);
        }
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawElements(GL3.GL_TRIANGLES, indices.capacity(),
                GL.GL_UNSIGNED_INT, 0);
    }

    public void destroy(GL3 gl) {
        gl.glDeleteBuffers(3, new int[] { verticesName, indicesName, normalsName }, 0);
    }

    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
}
