package com.zcking.furyengine.game;

import com.zcking.furyengine.engine.GameObject;
import com.zcking.furyengine.engine.Window;
import com.zcking.furyengine.lighting.DirectionalLight;
import com.zcking.furyengine.lighting.PointLight;
import com.zcking.furyengine.lighting.SpotLight;
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
    private static final String UNIFORM_POINT_LIGHTS = "pointLights";
    private static final String UNIFORM_MATERIAL = "material";
    private static final String UNIFORM_DIRECTIONAL_LIGHT = "directionalLight";
    private static final String UNIFORM_SPOT_LIGHTS = "spotLights";

    private static final int MAX_SPOT_LIGHTS = 5; // make this the same as in the shader!
    private static final int MAX_POINT_LIGHTS = 5; // make this the same as in the shader!

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
        shaderProgram.createPointLightListUniform(UNIFORM_POINT_LIGHTS, MAX_POINT_LIGHTS);
        shaderProgram.createDirectionalLightUniform(UNIFORM_DIRECTIONAL_LIGHT);
        shaderProgram.createSpotLightListUniform(UNIFORM_SPOT_LIGHTS, MAX_SPOT_LIGHTS);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameObject[] gameObjects, Vector3f ambientLight,
                       PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
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

        // Update the View Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform(UNIFORM_TEXTURE_SAMPLER, 0);
        // Update Light Uniforms
        renderLights(viewMatrix, ambientLight, pointLights, spotLights, directionalLight);

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

    private void renderLights(Matrix4f viewMatrix, Vector3f ambientLight,
                              PointLight[] pointLightList, SpotLight[] spotLightList, DirectionalLight directionalLight) {

        shaderProgram.setUniform(UNIFORM_AMBIENT_LIGHT, ambientLight);
        shaderProgram.setUniform(UNIFORM_SPECULAR_POWER, specularPower);

        // Process Point Lights
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
        }

        shaderProgram.setUniform(UNIFORM_POINT_LIGHTS, pointLightList);

        // Process Spot Lights
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
            Vector3f lightPos = currSpotLight.getPointLight().getPosition();

            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
        }

        shaderProgram.setUniform(UNIFORM_SPOT_LIGHTS, spotLightList);

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform(UNIFORM_DIRECTIONAL_LIGHT, currDirLight);

    }

    public void cleanUp() {
        if (shaderProgram != null) {
            shaderProgram.cleanUp();
        }
    }
}
