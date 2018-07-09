/**
 * 
 */
package unsw.graphics;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

/**
 * A shader for use with OpenGL.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Shader {

    /**
     * The vertex position attribute for use with glAttribPointer.
     */
    public static final int POSITION = 0;

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
}
