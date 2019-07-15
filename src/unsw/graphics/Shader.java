/**
 * 
 */
package unsw.graphics;

import java.awt.Color;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import unsw.graphics.geometry.Point3D;

/**
 * A shader for use with OpenGL.
 * 
 * This class is used to load shaders into UNSWgraph. Note that for a shader to
 * work in this library, there a number of required variables. For 2D
 * applications, in the vertex shader there must be: 
 *   - "in vec2 position" OR "in vec2 velocity" 
 *   - "uniform mat3 model_matrix" 
 *   - "uniform mat3 view_matrix"
 * For 3D applications, there must be:
 *   - "in vec3 position"
 *   - "uniform mat4 model_matrix"
 *   - "uniform mat4 view_matrix"
 *   - "uniform mat4 proj_matrix"
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Shader {

    // Vertex attributes

    /**
     * The vertex position attribute for use with glAttribPointer.
     */
    public static final int POSITION = 0;
    
    /**
     * The vertex normal attribute for use with glAttribPointer.
     */
    public static final int NORMAL = 1;
    
    /**
     * The vertex texture coordinate attribute for use with glAttribPointer.
     */
    public static final int TEX_COORD = 2;
    
    /**
     * The color attribute for use with glAttribPointer.
     */
    public static final int COLOR = 3;
    
    /**
     * The velocity attribute (NOTE: Can't be used in conjunction with POSITION)
     */
    public static final int VELOCITY = 0;

    private int id;

    /**
     * Construct a shader in the given OpenGL context.
     * 
     * @param gl
     * @param vertex The file containing the vertex shader code.
     * @param fragment The file containing the fragment shader code.
     */
    public Shader(GL3 gl, String vertex, String fragment) {

        ShaderCode vertShader = ShaderCode.create(gl, GL3.GL_VERTEX_SHADER, 1,
                this.getClass(), new String[] { vertex }, true);
        ShaderCode fragShader = ShaderCode.create(gl, GL3.GL_FRAGMENT_SHADER, 1,
                this.getClass(), new String[] { fragment }, true);

        // We unfortunately have to do this for this library to be compatible
        // with the older lab machines and the newer Macs
        vertShader.addGLSLVersion(gl);
        fragShader.addGLSLVersion(gl);

        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.add(vertShader);
        shaderProgram.add(fragShader);

        if (!shaderProgram.init(gl))
            throw new RuntimeException("Invalid shader program");

        id = shaderProgram.program();
        
        gl.glBindAttribLocation(id, POSITION, "position");
        gl.glBindAttribLocation(id, NORMAL, "normal");
        gl.glBindAttribLocation(id, TEX_COORD, "texCoord");
        gl.glBindAttribLocation(id, COLOR, "color");
        gl.glBindAttribLocation(id, VELOCITY, "velocity");
        
        shaderProgram.link(gl, System.err);
        
        if (gl.glGetAttribLocation(id, "position") != -1)
            gl.glEnableVertexAttribArray(POSITION);
        if (gl.glGetAttribLocation(id, "normal") != -1)
            gl.glEnableVertexAttribArray(NORMAL);
        if (gl.glGetAttribLocation(id, "texCoord") != -1)
            gl.glEnableVertexAttribArray(TEX_COORD);
        if (gl.glGetAttribLocation(id, "color") != -1)
            gl.glEnableVertexAttribArray(COLOR);
        if (gl.glGetAttribLocation(id, "velocity") != -1)
            gl.glEnableVertexAttribArray(VELOCITY);
        
    }

    /**
     * "Use" this shader in the given context.
     * 
     * This just calls glUseProgram() with this shader.
     * 
     * @param gl
     */
    public void use(GL3 gl) {
        gl.glUseProgram(id);
    }

    /**
     * Destroy this shader, releasing its resources.
     * 
     * This just calls glDeleteProgram().
     *
     * @param gl
     */
    public void destroy(GL3 gl) {
        gl.glDeleteProgram(id);
    }

    /**
     * Get the ID OpenGL associates with this shader.
     * 
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the model matrix of the currently loaded shader.
     * 
     * @param gl
     * @param mat
     */
    public static void setModelMatrix(GL3 gl, Matrix3 mat) {
        int ids[] = new int[1];
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int modelLoc = gl.glGetUniformLocation(ids[0], "model_matrix");
        gl.glUniformMatrix3fv(modelLoc, 1, false, mat.getValues(), 0);
    }

    /**
     * Sets the model matrix of the currently loaded shader.
     * 
     * @param gl
     * @param mat
     */
    public static void setModelMatrix(GL3 gl, Matrix4 mat) {
        int ids[] = new int[1];
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int modelLoc = gl.glGetUniformLocation(ids[0], "model_matrix");
        gl.glUniformMatrix4fv(modelLoc, 1, false, mat.getValues(), 0);
    }

    /**
     * Sets the view matrix of the currently loaded shader.
     * 
     * @param gl
     * @param mat
     */
    public static void setViewMatrix(GL3 gl, Matrix3 mat) {
        int ids[] = new int[1];
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int viewLoc = gl.glGetUniformLocation(ids[0], "view_matrix");
        gl.glUniformMatrix3fv(viewLoc, 1, false, mat.getValues(), 0);
    }

    /**
     * Sets the view matrix of the currently loaded shader.
     * 
     * @param gl
     * @param mat
     */
    public static void setViewMatrix(GL3 gl, Matrix4 mat) {
        int ids[] = new int[1];
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int viewLoc = gl.glGetUniformLocation(ids[0], "view_matrix");
        gl.glUniformMatrix4fv(viewLoc, 1, false, mat.getValues(), 0);
    }

    /**
     * Sets the projection matrix of the currently loaded shader.
     * 
     * @param gl
     * @param mat
     */
    public static void setProjMatrix(GL3 gl, Matrix4 mat) {
        int ids[] = new int[1];
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int viewLoc = gl.glGetUniformLocation(ids[0], "proj_matrix");
        gl.glUniformMatrix4fv(viewLoc, 1, false, mat.getValues(), 0);
    }

    /**
     * Sets the pen color of the currently loaded shader.
     * 
     * @param gl
     * @param color
     */
    public static void setPenColor(GL3 gl, Color color) {
        setColorWithAlpha(gl, "input_color", color);
    }
    
    /**
     * Set an arbitrary uniform variable of type 'vec3' with the given
     * Point3D
     * @param gl
     * @param var
     * @param point3d
     */
    public static void setPoint3D(GL3 gl, String var, Point3D point3d) {
        int ids[] = new int[1];
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int loc = gl.glGetUniformLocation(ids[0], var);
        gl.glUniform3f(loc, point3d.getX(), point3d.getY(), point3d.getZ());
    }
    
    /**
     * Set an arbitrary uniform variable of type 'vec3' with the given
     * Color.
     * @param gl
     * @param var
     * @param color
     */
    public static void setColor(GL3 gl, String var, Color color) {
        int ids[] = new int[1];
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int loc = gl.glGetUniformLocation(ids[0], var);
        gl.glUniform3f(loc, color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f);
    }
    
    /**
     * Set an arbitrary uniform variable of type 'vec4' with the given
     * Color.
     * @param gl
     * @param var
     * @param color
     */
    public static void setColorWithAlpha(GL3 gl, String var, Color color) {
        int ids[] = new int[1];
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int loc = gl.glGetUniformLocation(ids[0], var);
        gl.glUniform4f(loc, color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
    }
    
    /**
     * Set an arbitrary uniform variable of type 'float' with the given
     * float.
     * @param gl
     * @param var
     * @param f
     */
    public static void setFloat(GL3 gl, String var, float f) {
        int ids[] = new int[1];
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int loc = gl.glGetUniformLocation(ids[0], var);
        gl.glUniform1f(loc, f);
    }

    public static void setInt(GL3 gl, String var, int i) {
        int ids[] = new int[1];
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int loc = gl.glGetUniformLocation(ids[0], var);
        gl.glUniform1i(loc, i);
    }
}
