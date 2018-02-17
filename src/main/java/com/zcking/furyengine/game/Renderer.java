package com.zcking.furyengine.game;

import com.zcking.furyengine.engine.GameObject;
import com.zcking.furyengine.engine.Window;
import com.zcking.furyengine.engine.graph.Mesh;
import com.zcking.furyengine.engine.graph.ShaderProgram;
import com.zcking.furyengine.engine.graph.Transformation;
import com.zcking.furyengine.utils.ResourceUtils;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
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
    private static final String UNIFORM_WORLD_MATRIX = "worldMatrix";

    public Renderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(ResourceUtils.loadResource("/vertex.glsl"));
        shaderProgram.createFragmentShader(ResourceUtils.loadResource("/fragment.glsl"));
        shaderProgram.link();

        shaderProgram.createUniform(UNIFORM_PROJECTION_MATRIX);
        shaderProgram.createUniform(UNIFORM_WORLD_MATRIX);

        window.setClearColor(0, 0, 0, 0);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, GameObject[] gameObjects) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(true);
        }

        shaderProgram.bind();

        // Update the projection matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(),
                Z_NEAR, Z_FAR);
        shaderProgram.setUniform(UNIFORM_PROJECTION_MATRIX, projectionMatrix);

        // Render the game objects
        for (GameObject gameObject : gameObjects) {
            Matrix4f worldMatrix = transformation.getWorldMatrix(
                    gameObject.getPosition(),
                    gameObject.getRotation(),
                    gameObject.getScale()
            );
            shaderProgram.setUniform(UNIFORM_WORLD_MATRIX, worldMatrix);
            gameObject.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void cleanUp() {
        if (shaderProgram != null) {
            shaderProgram.cleanUp();
        }
    }
}
