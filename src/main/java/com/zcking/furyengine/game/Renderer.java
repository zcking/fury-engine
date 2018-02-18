package com.zcking.furyengine.game;

import com.zcking.furyengine.engine.GameObject;
import com.zcking.furyengine.engine.Window;
import com.zcking.furyengine.engine.graph.Camera;
import com.zcking.furyengine.engine.graph.Mesh;
import com.zcking.furyengine.engine.graph.ShaderProgram;
import com.zcking.furyengine.engine.graph.Transformation;
import com.zcking.furyengine.utils.ResourceUtils;
import org.joml.Math;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Renderer {

    private int vboId;
    private int vaoId;

    private ShaderProgram shaderProgram;

    // Field of View (in radians)
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private final Transformation transformation;

    private static final String UNIFORM_PROJECTION_MATRIX = "projectionMatrix";
    private static final String UNIFORM_MODEL_VIEW_MATRIX = "modelViewMatrix";
    private static final String UNIFORM_TEXTURE_SAMPLER = "textureSampler";
    private static final String UNIFORM_COLOR = "color";
    private static final String UNIFORM_USE_COLOR = "useColor";

    public Renderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(ResourceUtils.loadResource("/shaders/vertex.glsl"));
        shaderProgram.createFragmentShader(ResourceUtils.loadResource("/shaders/fragment.glsl"));
        shaderProgram.link();

        shaderProgram.createUniform(UNIFORM_PROJECTION_MATRIX);
        shaderProgram.createUniform(UNIFORM_MODEL_VIEW_MATRIX);
        shaderProgram.createUniform(UNIFORM_TEXTURE_SAMPLER);

        // Default color and flag to control it
        shaderProgram.createUniform(UNIFORM_COLOR);
        shaderProgram.createUniform(UNIFORM_USE_COLOR);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameObject[] gameObjects) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(true);
        }

        shaderProgram.bind();

        // Update the projection matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(),
                Z_NEAR, Z_FAR);

        // Update the View Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform(UNIFORM_PROJECTION_MATRIX, projectionMatrix);
        shaderProgram.setUniform(UNIFORM_TEXTURE_SAMPLER, 0);

        // Render the game objects
        for (GameObject gameObject : gameObjects) {
            Mesh mesh = gameObject.getMesh();

            // Set model view matrix for this object
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameObject, viewMatrix);
            shaderProgram.setUniform(UNIFORM_MODEL_VIEW_MATRIX, modelViewMatrix);

            // Render the mesh for this game object
            shaderProgram.setUniform(UNIFORM_COLOR, mesh.getColor());
            shaderProgram.setUniform(UNIFORM_USE_COLOR, mesh.isTextured() ? 0 : 1);

            mesh.render();
        }

        shaderProgram.unbind();
    }

    public void cleanUp() {
        if (shaderProgram != null) {
            shaderProgram.cleanUp();
        }
    }
}
