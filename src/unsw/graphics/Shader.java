/**
 * 
 */
package unsw.graphics;

import java.awt.Color;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

/**
 * A shader for use with OpenGL.
 * 
 * This class is used to load shaders into UNSWgraph. Note that for a shader to 
 * work in this library, there a number of required variables. In the vertex 
 * shader there must be:
 *   - "in vec2 position"
 *   - "uniform mat3 model_matrix" 
 *   - "uniform mat3 view_matrix"
 *
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Shader {

    //Vertex attributes
    
    /**
     * The vertex position attribute for use with glAttribPointer.
     */
    public static final int POSITION = 0;
    
    //Uniform variables
    
    /**
     * The name of the model matrix input variable.
     */
    public static final String MODEL_MATRIX = "model_matrix";

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
        shaderProgram.link(gl, System.err);

        gl.glEnableVertexAttribArray(POSITION);
        gl.glBindAttribLocation(id, POSITION, "position");
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
     * @param gl
     * @param color
     */
    public static void setPenColor(GL3 gl, Color color) {
        int ids[] = new int[1]; 
        gl.glGetIntegerv(GL3.GL_CURRENT_PROGRAM, ids, 0);
        int viewLoc = gl.glGetUniformLocation(ids[0], "input_color");
        gl.glUniform3f(viewLoc, color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f);
    }
}
