package unsw.graphics.examples;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix3;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.Polygon2D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * This is a simple application for viewing models.
 *
 * Different PLY models have vastly different scales, so you may need to scale
 * the model up or down to view it properly.
 *
 * High resolution models are not included with UNSWgraph due to their large
 * file sizes. They can be downloaded here:
 *
 * https://www.dropbox.com/s/tg2y5kvzbgb3pco/big.zip?dl=1
 *
 * @author Robert Clifton-Everest
 *
 */
public class ModelViewer extends Application3D {

    private static final boolean USE_LIGHTING = true;

    private static final boolean USE_TEXTURE = true;

    private static final boolean USE_CUBEMAP = false; //Lighting must also be on

    private static final boolean POSTPROCESS = false;

    private static final int NUM_FRAMEBUFFERS = 3;

    private float rotateY;

    private TriangleMesh model;

    private TriangleMesh base;

    private Texture texture;

    private Shader primaryShader;

    private Shader simple2dShader;
    private Shader sobelShader;
    private Shader blurShader;
    private Shader brightShader;

    private Texture[] frameBufferTextures;
    private int frameBufferObject;
    private int renderBufferObject;

    public ModelViewer() throws IOException {
        super("Model viewer", 600, 600);
        model = new TriangleMesh("res/models/bunny.ply", true, true);
        base = new TriangleMesh("res/models/cube_normals.ply", true, true);

        if (POSTPROCESS)
            setBackground(new Color(1f,1f,1f,0)); // Transparent white
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);
        model.init(gl);
        base.init(gl);
        if (USE_CUBEMAP) {
            texture = new Texture(gl, "res/textures/darkskies/darkskies_lf.png",
                    "res/textures/darkskies/darkskies_rt.png",
                    "res/textures/darkskies/darkskies_dn.png",
                    "res/textures/darkskies/darkskies_up.png",
                    "res/textures/darkskies/darkskies_ft.png",
                    "res/textures/darkskies/darkskies_bk.png", "png", false);
        } else if (USE_TEXTURE) {
            texture = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", false);
        }

        if (POSTPROCESS) {
            // Get the viewport size
            int[] viewport = new int[4];
            gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

            frameBufferTextures = new Texture[NUM_FRAMEBUFFERS];
            for (int i = 0; i < NUM_FRAMEBUFFERS; i++) {
                frameBufferTextures[i] = new Texture(gl);
                gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, viewport[2], viewport[3], 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
            }

            int[] fbos = new int[1];
            gl.glGenFramebuffers(1, fbos, 0);
            frameBufferObject = fbos[0];

            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, frameBufferObject);
            switchFrameBufferTexture(gl, 0);

            int[] rbos = new int[1];
            gl.glGenRenderbuffers(1, rbos, 0);
            renderBufferObject = rbos[0];

            gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, renderBufferObject);
            gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL.GL_DEPTH_COMPONENT24, viewport[2], viewport[3]);
            gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL3.GL_DEPTH_ATTACHMENT, GL.GL_RENDERBUFFER, renderBufferObject);

            gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, 0);
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
        }

        primaryShader = null;
        if (USE_CUBEMAP) {
            primaryShader = new Shader(gl, "shaders/vertex_phong.glsl",
                    "shaders/fragment_cubemap.glsl");
        } else if (USE_LIGHTING && USE_TEXTURE) {
            primaryShader = new Shader(gl, "shaders/vertex_tex_phong.glsl",
                    "shaders/fragment_tex_phong.glsl");
        } else if (USE_LIGHTING) {
            primaryShader = new Shader(gl, "shaders/vertex_phong.glsl",
                    "shaders/fragment_phong.glsl");
        } else if (USE_TEXTURE) {
            primaryShader = new Shader(gl, "shaders/vertex_tex_3d.glsl",
                    "shaders/fragment_tex_3d.glsl");
        } else {
            primaryShader = new Shader(gl, "shaders/vertex_3d.glsl", "shaders/fragment_3d.glsl");
        }

        simple2dShader = new Shader(gl, "shaders/vertex_tex_2d.glsl", "shaders/fragment_tex_2d.glsl");
        sobelShader = new Shader(gl, "shaders/vertex_tex_2d.glsl", "shaders/fragment_sobel.glsl");
        blurShader = new Shader(gl, "shaders/vertex_tex_2d.glsl", "shaders/fragment_blur.glsl");
        brightShader = new Shader(gl, "shaders/vertex_tex_2d.glsl", "shaders/fragment_extract_bright.glsl");

        primaryShader.use(gl);
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, 1, 1, 100));
    }

    public static void main(String[] args) throws IOException {
        ModelViewer example = new ModelViewer();
        example.start();

    }

    @Override
    public void display(GL3 gl) {
        // Render to the FBO if we're postprocessing
        if (POSTPROCESS) {
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, frameBufferObject);
            switchFrameBufferTexture(gl, 0);
        }

        super.display(gl);

        primaryShader.use(gl);

        // Set the projection matrix on each call to display as we dynamically
        // swap shaders.
        Shader.setProjMatrix(gl, Matrix4.perspective(60, 1, 1, 100));

        //Set the texture if we're using it.
        if (USE_CUBEMAP) {
            Shader.setInt(gl, "tex", 0);

            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, texture.getId());

            Shader.setPenColor(gl, Color.WHITE);
        } else if (USE_TEXTURE) {
            Shader.setInt(gl, "tex", 0);

            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());

            Shader.setPenColor(gl, Color.WHITE);
        } else {
            Shader.setPenColor(gl, new Color(0.5f, 0.5f, 0.5f));
        }

        // Compute the view transform
        CoordFrame3D view = CoordFrame3D.identity().translate(0, 0, -2)
                // Uncomment the line below to rotate the camera
//                 .rotateY(rotateY)
                .translate(0, 0, 2);
        Shader.setViewMatrix(gl, view.getMatrix());

        // Set the lighting properties
        if (USE_LIGHTING) {
            Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
            Shader.setColor(gl, "lightIntensity", Color.WHITE);
            Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));

            // Set the material properties
            Shader.setColor(gl, "ambientCoeff", Color.WHITE);
            Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
            Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
            Shader.setFloat(gl, "phongExp", 16f);
        }

        // The coordinate frame for both objects
        CoordFrame3D frame = CoordFrame3D.identity().translate(0, -0.5f, -2);

        // The coordinate frame for the model we're viewing.
        CoordFrame3D modelFrame = frame
                // Uncomment the line below to rotate the model
                .rotateY(rotateY)

                // This translation and scale works well for the bunny and
                // dragon1
                .translate(0, -0.2f, 0).scale(5, 5, 5);
        // This scale works well for the apple
//         .scale(5, 5, 5);
        // This translation and scale works well for dragon2
//         .translate(0,0.33f,0).scale(0.008f, 0.008f, 0.008f);
        // This translation and scale works well for the tree
//           .translate(0,0.5f,0).scale(0.1f,0.1f,0.1f);

        model.draw(gl, modelFrame);

        // A blue base for the model to sit on.
        CoordFrame3D baseFrame =
                frame.translate(0, -0.5f, 0).scale(0.5f, 0.5f, 0.5f);
        if (!USE_TEXTURE && !USE_CUBEMAP)
            Shader.setPenColor(gl, Color.BLUE);
        base.draw(gl, baseFrame);

        rotateY += 1;

        if (POSTPROCESS) {
            gl.glDisable(GL.GL_DEPTH_TEST);

            // Draw outline with a sobel filter
            gl.glBindTexture(GL.GL_TEXTURE_2D, frameBufferTextures[0].getId());

            setPostProcessShader(gl, sobelShader);
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0); // The default framebuffer
            clearFrameBuffer(gl);
            drawFullScreenQuad(gl);

            // Apply a gaussian blur
//            setPostProcessShader(gl, blurShader);
//
//            gl.glBindTexture(GL.GL_TEXTURE_2D, frameBufferTextures[0].getId());
//            Shader.setInt(gl, "horizontal", 1);
//            switchFrameBufferTexture(gl, 1);
//            clearFrameBuffer(gl);
//            drawFullScreenQuad(gl);
//
//            gl.glBindTexture(GL.GL_TEXTURE_2D, frameBufferTextures[1].getId());
//            Shader.setInt(gl, "horizontal", 0);
//            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
//            clearFrameBuffer(gl);
//            drawFullScreenQuad(gl);

            // Apply a simple bloom filter
//            // Step 1. Extract bright regions
//            setPostProcessShader(gl, brightShader);
//            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//
//            gl.glBindTexture(GL.GL_TEXTURE_2D, frameBufferTextures[0].getId());
//            switchFrameBufferTexture(gl, 1);
//            clearFrameBuffer(gl);
//            drawFullScreenQuad(gl);
//
//            // Step 2. Blur bright regions
//            setPostProcessShader(gl, blurShader);
//
//            for (int i = 0; i < 10; i++) {
//                Shader.setInt(gl, "horizontal", 0);
//                gl.glBindTexture(GL.GL_TEXTURE_2D, frameBufferTextures[1].getId());
//                switchFrameBufferTexture(gl, 2);
//                clearFrameBuffer(gl);
//                drawFullScreenQuad(gl);
//
//                Shader.setInt(gl, "horizontal", 1);
//                gl.glBindTexture(GL.GL_TEXTURE_2D, frameBufferTextures[2].getId());
//                switchFrameBufferTexture(gl, 1);
//                clearFrameBuffer(gl);
//                drawFullScreenQuad(gl);
//            }
//
//            // Step 3. Draw original scene
//            setPostProcessShader(gl, simple2dShader);
//
//            gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
//            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
//            gl.glBindTexture(GL.GL_TEXTURE_2D, frameBufferTextures[0].getId());
//            clearFrameBuffer(gl);
//            drawFullScreenQuad(gl);
//
//            // Step 4. Draw blurred brightness on top
//            gl.glBindTexture(GL.GL_TEXTURE_2D, frameBufferTextures[1].getId());
//            gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
//            drawFullScreenQuad(gl);

            // Turn these back on again for the next frame
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    private void switchFrameBufferTexture(GL3 gl, int fb) {
        gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, frameBufferTextures[fb].getId(), 0);
    }

    private void clearFrameBuffer(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    }

    private void drawFullScreenQuad(GL3 gl) {
        //Draw a fullscreen quad
        Polygon2D quad = new Polygon2D(-1,-1, 1,-1, 1,1, -1,1);
        quad.draw(gl);
    }

    private void setPostProcessShader(GL3 gl, Shader shader) {
        shader.use(gl);
        Shader.setViewMatrix(gl, Matrix3.identity());
        Shader.setModelMatrix(gl, Matrix3.identity());
        Shader.setPenColor(gl, Color.WHITE);
        Shader.setInt(gl, "tex", 0);
    }

    @Override
    public void destroy(GL3 gl) {
        super.destroy(gl);
        model.destroy(gl);
        base.destroy(gl);
        texture.destroy(gl);
        for (Texture tex : frameBufferTextures)
            tex.destroy(gl);
        gl.glDeleteFramebuffers(1, new int[] { frameBufferObject }, 0);
        gl.glDeleteRenderbuffers(1, new int[] { renderBufferObject }, 0);
    }

}