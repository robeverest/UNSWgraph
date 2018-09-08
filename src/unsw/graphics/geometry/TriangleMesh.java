/**
 * 
 */
package unsw.graphics.geometry;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
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
import unsw.graphics.Point2DBuffer;
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
     * Contains the texture coordinates for all vertices.
     */
    private Point2DBuffer texCoords;

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
     * The name of the normal buffer according to OpenGL
     */
    private int texCoordsName;

    /**
     * The name of the indices buffer according to OpenGL
     */
    private int indicesName;
    
    /**
     * Create a triangle mesh with the given lists of vertices, normals, and 
     * indices
     * @param vertices
     * @param normals
     * @param indices
     */
    public TriangleMesh(List<Point3D> vertices, List<Vector3> normals, List<Integer> indices) {
        this(vertices, indices, false);
        this.normals = new Point3DBuffer(normals.size());
        for (int i = 0; i < normals.size(); i++) {
            Vector3 n = normals.get(i);
            this.normals.put(i, n.getX(), n.getY(), n.getZ());
        }
    }
    
    /**
     * Create a triangle mesh with the given list of vertices and indices. The 
     * third argument indicates whether to generate vertex normals. If false, 
     * no normals are generated.
     * @param vertices
     * @param indices
     * @param vertexNormals
     */
    public TriangleMesh(List<Point3D> vertices, List<Integer> indices, boolean vertexNormals) {
        this.vertices = new Point3DBuffer(vertices);
        this.indices = GLBuffers.newDirectIntBuffer(ArrayUtils.toPrimitive(indices.toArray(new Integer[0])));
        if (vertexNormals) {
            normals = new Point3DBuffer(vertices.size());
            computeVertexNormals();
        }
    }
    
    /**
     * Create a triangle mesh with the given list of vertices (assumed to be in 
     * the desired order for a triangle mesh). The second argument indicates 
     * whether to generate face normals. If false, no normals are generated.
     * @param vertices
     * @param indices
     * @param vertexNormals
     */
    public TriangleMesh(List<Point3D> vertices, boolean faceNormals) {
        this.vertices = new Point3DBuffer(vertices);
        if (faceNormals) {
            normals = new Point3DBuffer(vertices.size());
            computeFaceNormals();
        }
    }
    
    /**
     * Create a triangle mesh with the given lists of vertices, normals,
     * indices and texture coordinates.
     * @param vertices
     * @param normals
     * @param indices
     * @param texCoords
     */
    public TriangleMesh(List<Point3D> vertices, List<Vector3> normals, 
            List<Integer> indices, List<Point2D> texCoords) {
        this(vertices, indices, false, texCoords);
        this.normals = new Point3DBuffer(normals.size());
        for (int i = 0; i < normals.size(); i++) {
            Vector3 n = normals.get(i);
            this.normals.put(i, n.getX(), n.getY(), n.getZ());
        }
    }
    
    /**
     * Create a triangle mesh with the given list of vertices, indices and 
     * texture coordinates. The third argument indicates whether to generate 
     * vertex normals. If false, no normals are generated.
     * @param vertices
     * @param indices
     * @param vertexNormals
     * @param texCoords
     */
    public TriangleMesh(List<Point3D> vertices, List<Integer> indices, 
            boolean vertexNormals, List<Point2D> texCoords) {
        this.vertices = new Point3DBuffer(vertices);
        this.indices = GLBuffers.newDirectIntBuffer(ArrayUtils.toPrimitive(indices.toArray(new Integer[0])));
        if (vertexNormals) {
            normals = new Point3DBuffer(vertices.size());
            computeVertexNormals();
        }
        this.texCoords = new Point2DBuffer(texCoords);
    }
    
    /**
     * Create a triangle mesh with the given list of vertices (assumed to be in 
     * the desired order for a triangle mesh) and texture coordinates. The 
     * second argument indicates whether to generate face normals. If false, no 
     * normals are generated.
     * @param vertices
     * @param indices
     * @param vertexNormals
     * @param texCoords
     */
    public TriangleMesh(List<Point3D> vertices, boolean faceNormals, List<Point2D> texCoords) {
        this.vertices = new Point3DBuffer(vertices);
        if (faceNormals) {
            normals = new Point3DBuffer(vertices.size());
            computeFaceNormals();
        }
        this.texCoords = new Point2DBuffer(texCoords);
    }

    /**
     * Construct a triangle with the given PLY file.
     * 
     * @param plyFile
     * @throws IOException
     */
    public TriangleMesh(String plyFile) throws IOException {
        this(plyFile, false, false);
    }

    /**
     * Construct a triangle with the given PLY file. The second argument
     * indicates whether to generate vertex normals. If false, no normals are
     * generated. Similarly, the third argument indicates whether to generate
     * texture coordinates. If they are generated, each vertex will have a
     * texture coordinate equal to its x and y position relative to the center 
     * of base of the model and divided by the width and the height 
     * respectively.
     * 
     * @param plyFile
     * @param vertexNormals
     * @param texCoords
     * @throws IOException
     */
    public TriangleMesh(String plyFile, boolean vertexNormals, boolean texCoords)
            throws IOException {
        // Setup an initial reader
        PlyReader rawReader = new PlyReaderFile(plyFile);

        // Use a normalizing reader to get mesh only containing triangles
        NormalizingPlyReader reader = new NormalizingPlyReader(rawReader,
                TesselationMode.TRIANGLES, NormalMode.PASS_THROUGH,
                texCoords ? TextureMode.XY : TextureMode.PASS_THROUGH);

        vertices = new Point3DBuffer(reader.getElementCount("vertex"));
        indices = GLBuffers
                .newDirectIntBuffer(reader.getElementCount("face") * 3);
        if (vertexNormals)
            normals = new Point3DBuffer(reader.getElementCount("vertex"));
        
        if (texCoords)
            this.texCoords = new Point2DBuffer(reader.getElementCount("vertex"));

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
            computeVertexNormals();
    }
    
    /**
     * Compute face normals for the mesh assuming it is not indexed.
     */
    private void computeFaceNormals() {
        for (int i = 0; i < vertices.capacity() / 3; i++) {
            Vector3 n = normal(vertices.get(i*3), vertices.get(i*3 + 1),
                    vertices.get(i*3 + 2));
            normals.put(i*3, n.asPoint3D());
            normals.put(i*3 + 1, n.asPoint3D());
            normals.put(i*3 + 2, n.asPoint3D());
        }
    }


    /**
     * Compute normals for the mesh. Note that they are not normalised normals. 
     * If a shader depends on the normals, it must normalise them internally.
     */
    private void computeVertexNormals() {
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
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    private Vector3 normal(Point3D p1, Point3D p2, Point3D p3) {
        Vector3 a = p2.minus(p1);
        Vector3 b = p3.minus(p1);
        
        return a.cross(b).normalize();
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
            
            if (texCoords != null) {
                float s = (float) vertex.getDouble("u");
                float t = (float) vertex.getDouble("v");
                texCoords.put(verticesIndex, s, t);
            }
            verticesIndex++;
            vertex = elReader.readElement();
        }
    }

    public void init(GL3 gl) {
        // Generate the names for the buffers.
        int[] names = new int[4];
        gl.glGenBuffers(4, names, 0);
        verticesName = names[0];
        indicesName = names[1];
        normalsName = names[2];
        texCoordsName = names[3];

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
        
        if (texCoords != null) {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
            gl.glBufferData(GL.GL_ARRAY_BUFFER,
                    texCoords.capacity() * 2 * Float.BYTES, texCoords.getBuffer(),
                    GL.GL_STATIC_DRAW);
        }

        if (indices != null) {
            // Copy the data for the indices
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
            gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER,
                    indices.capacity() * Integer.BYTES, indices, GL.GL_STATIC_DRAW);
        }
    }

    public void draw(GL3 gl, CoordFrame3D frame) {
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        if (normals != null) {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalsName);
            gl.glVertexAttribPointer(Shader.NORMAL, 3, GL.GL_FLOAT, false, 0, 0);
        }
        if (texCoords != null) {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
            gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);
        }
        Shader.setModelMatrix(gl, frame.getMatrix());
        if (indices != null) {
            gl.glDrawElements(GL3.GL_TRIANGLES, indices.capacity(),
                    GL.GL_UNSIGNED_INT, 0);
        } else {
            gl.glDrawArrays(GL3.GL_TRIANGLES, 0, vertices.capacity());
        }
    }

    public void destroy(GL3 gl) {
        gl.glDeleteBuffers(4, new int[] { verticesName, indicesName, normalsName, texCoordsName }, 0);
    }

    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
}
