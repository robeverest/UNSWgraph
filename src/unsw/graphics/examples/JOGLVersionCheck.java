package unsw.graphics.examples;

import com.jogamp.opengl.GLProfile;

/**
 * Checks to make sure that OpenGL 3 is supported via JOGL.
 * @author Robert Clifton-Everest
 *
 */
public class JOGLVersionCheck {

    public static void main(String[] args) {
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        if (profile.isGL3())
            System.out.println("OpenGL version 3 is supported.");
        else 
            System.out.println("OpenGL version 3 is not supported on this computer.");
    }

}
