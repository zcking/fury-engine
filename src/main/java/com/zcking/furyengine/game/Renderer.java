package com.zcking.furyengine.game;

import com.zcking.furyengine.engine.GameObject;
import com.zcking.furyengine.engine.Window;
import com.zcking.furyengine.lighting.PointLight;
import com.zcking.furyengine.rendering.Camera;
import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.rendering.ShaderProgram;
import com.zcking.furyengine.engine.graph.Transformation;
import com.zcking.furyengine.utils.ResourceUtils;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Renderer {

    private int vboId;
    private int vaoId;

    private ShaderProgram shaderProgram;
    private float specularPower;

    // Field of View (in radians)
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private final Transformation transformation;

    private static final String UNIFORM_PROJECTION_MATRIX = "projectionMatrix";
    private static final String UNIFORM_MODEL_VIEW_MATRIX = "modelViewMatrix";
    private static final String UNIFORM_TEXTURE_SAMPLER = "textureSampler";
    private static final String UNIFORM_AMBIENT_LIGHT = "ambientLight";
    private static final String UNIFORM_SPECULAR_POWER = "specularPower";
    private static final String UNIFORM_POINT_LIGHT = "pointLight";
    private static final String UNIFORM_MATERIAL = "material";

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(ResourceUtils.loadResource("/shaders/vertex.glsl"));
        shaderProgram.createFragmentShader(ResourceUtils.loadResource("/shaders/fragment.glsl"));
        shaderProgram.link();

        shaderProgram.createUniform(UNIFORM_PROJECTION_MATRIX);
        shaderProgram.createUniform(UNIFORM_MODEL_VIEW_MATRIX);
        shaderProgram.createUniform(UNIFORM_TEXTURE_SAMPLER);

        // Uniform for material
        shaderProgram.createMaterialUniform(UNIFORM_MATERIAL);

        // Light-related uniforms
        shaderProgram.createUniform(UNIFORM_SPECULAR_POWER);
        shaderProgram.createUniform(UNIFORM_AMBIENT_LIGHT);
        shaderProgram.createPointLightUniform(UNIFORM_POINT_LIGHT);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameObject[] gameObjects, Vector3f ambientLight, PointLight pointLight) {
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

        // Update light uniforms
        shaderProgram.setUniform(UNIFORM_AMBIENT_LIGHT, ambientLight);
        shaderProgram.setUniform(UNIFORM_SPECULAR_POWER, specularPower);

        // Get a copy of the light object and transform its positions to view coordinates
        PointLight curPointLight = new PointLight(pointLight);
        Vector3f lightPos = curPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shaderProgram.setUniform(UNIFORM_POINT_LIGHT, curPointLight);

        // Render the game objects
        for (GameObject gameObject : gameObjects) {
            Mesh mesh = gameObject.getMesh();

            // Set model view matrix for this object
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameObject, viewMatrix);
            shaderProgram.setUniform(UNIFORM_MODEL_VIEW_MATRIX, modelViewMatrix);

            // Render the mesh for the object
            shaderProgram.setUniform(UNIFORM_MATERIAL, mesh.getMaterial());

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
