package com.zcking.furyengine.game;

import com.zcking.furyengine.engine.GameObject;
import com.zcking.furyengine.engine.IHud;
import com.zcking.furyengine.engine.Window;
import com.zcking.furyengine.lighting.DirectionalLight;
import com.zcking.furyengine.lighting.PointLight;
import com.zcking.furyengine.lighting.SceneLight;
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

    private ShaderProgram sceneShaderProgram;
    private ShaderProgram hudShaderProgram;
    private float specularPower;

    // Field of View (in radians)
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private final Transformation transformation;

    // Scene shader uniforms
    private static final String UNIFORM_PROJECTION_MATRIX = "projectionMatrix";
    private static final String UNIFORM_MODEL_VIEW_MATRIX = "modelViewMatrix";
    private static final String UNIFORM_TEXTURE_SAMPLER = "textureSampler";
    private static final String UNIFORM_AMBIENT_LIGHT = "ambientLight";
    private static final String UNIFORM_SPECULAR_POWER = "specularPower";
    private static final String UNIFORM_POINT_LIGHTS = "pointLights";
    private static final String UNIFORM_MATERIAL = "material";
    private static final String UNIFORM_DIRECTIONAL_LIGHT = "directionalLight";
    private static final String UNIFORM_SPOT_LIGHTS = "spotLights";

    // HUD shader uniforms
    private static final String UNIFORM_HUD_PROJ_MODEL_MATRIX = "projModelMatrix";
    private static final String UNIFORM_HUD_COLOR = "color";

    private static final int MAX_SPOT_LIGHTS = 5; // make this the same as in the shader!
    private static final int MAX_POINT_LIGHTS = 5; // make this the same as in the shader!

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {
        setupSceneShader();
        setupHudShader();
    }

    private void setupSceneShader() throws Exception {
        // Create the shaders for scene
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(ResourceUtils.loadResource("/shaders/vertex.glsl"));
        sceneShaderProgram.createFragmentShader(ResourceUtils.loadResource("/shaders/fragment.glsl"));
        sceneShaderProgram.link();

        // Create the uniforms for the scene shaders
        sceneShaderProgram.createUniform(UNIFORM_PROJECTION_MATRIX);
        sceneShaderProgram.createUniform(UNIFORM_MODEL_VIEW_MATRIX);
        sceneShaderProgram.createUniform(UNIFORM_TEXTURE_SAMPLER);
        sceneShaderProgram.createMaterialUniform(UNIFORM_MATERIAL);

        // Light-related uniforms
        sceneShaderProgram.createUniform(UNIFORM_SPECULAR_POWER);
        sceneShaderProgram.createUniform(UNIFORM_AMBIENT_LIGHT);
        sceneShaderProgram.createPointLightListUniform(UNIFORM_POINT_LIGHTS, MAX_POINT_LIGHTS);
        sceneShaderProgram.createDirectionalLightUniform(UNIFORM_DIRECTIONAL_LIGHT);
        sceneShaderProgram.createSpotLightListUniform(UNIFORM_SPOT_LIGHTS, MAX_SPOT_LIGHTS);
    }

    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.createVertexShader(ResourceUtils.loadResource("/shaders/hud_vertex.glsl"));
        hudShaderProgram.createFragmentShader(ResourceUtils.loadResource("/shaders/hud_fragment.glsl"));
        hudShaderProgram.link();

        // Uniforms for HUD shaders
        hudShaderProgram.createUniform(UNIFORM_HUD_PROJ_MODEL_MATRIX);
        hudShaderProgram.createUniform(UNIFORM_HUD_COLOR);
    }

    public ShaderProgram getSceneShaderProgram() {
        return sceneShaderProgram;
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameObject[] gameObjects,
                       SceneLight sceneLight, IHud hud) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(true);
        }

        renderScene(window, camera, gameObjects, sceneLight);
        renderHud(window, hud);
    }

    public void renderScene(Window window, Camera camera, GameObject[] gameObjects, SceneLight sceneLight) {
        sceneShaderProgram.bind();

        // Update the projection matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(),
                Z_NEAR, Z_FAR);
        sceneShaderProgram.setUniform(UNIFORM_PROJECTION_MATRIX, projectionMatrix);

        // Update the View Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        // Update Light Uniforms
        renderLights(viewMatrix, sceneLight);

        // Render the game objects
        sceneShaderProgram.setUniform(UNIFORM_TEXTURE_SAMPLER, 0);
        for (GameObject gameObject : gameObjects) {
            Mesh mesh = gameObject.getMesh();

            // Set model view matrix for this object
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameObject, viewMatrix);
            sceneShaderProgram.setUniform(UNIFORM_MODEL_VIEW_MATRIX, modelViewMatrix);

            // Render the mesh for the object
            sceneShaderProgram.setUniform(UNIFORM_MATERIAL, mesh.getMaterial());
            mesh.render();
        }

        sceneShaderProgram.unbind();
    }

    private void renderHud(Window window, IHud hud) {
        hudShaderProgram.bind();

        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        for (GameObject gameObject : hud.getGameObjects()) {
            Mesh mesh = gameObject.getMesh();

            // Set orthographic and model matrix for this HUD item
            Matrix4f projModelMatrix = transformation.getOrthoProjModelMatrix(gameObject, ortho);
            hudShaderProgram.setUniform(UNIFORM_HUD_PROJ_MODEL_MATRIX, projModelMatrix);
            hudShaderProgram.setUniform(UNIFORM_HUD_COLOR, mesh.getMaterial().getAmbientColor());

            // Render the mesh for the HUD item
            mesh.render();
        }

        hudShaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {

        sceneShaderProgram.setUniform(UNIFORM_AMBIENT_LIGHT, sceneLight.getAmbientLight());
        sceneShaderProgram.setUniform(UNIFORM_SPECULAR_POWER, specularPower);

        // Process Point Lights
        PointLight[] pointLightsList = sceneLight.getPointLights();
        int numLights = pointLightsList != null ? pointLightsList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightsList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
        }

        sceneShaderProgram.setUniform(UNIFORM_POINT_LIGHTS, pointLightsList);

        // Process Spot Lights
        SpotLight[] spotLightsList = sceneLight.getSpotLights();
        numLights = spotLightsList != null ? spotLightsList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightsList[i]);
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

        sceneShaderProgram.setUniform(UNIFORM_SPOT_LIGHTS, spotLightsList);

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        sceneShaderProgram.setUniform(UNIFORM_DIRECTIONAL_LIGHT, currDirLight);

    }

    public void cleanUp() {
        if (sceneShaderProgram != null) {
            sceneShaderProgram.cleanUp();
        }

        if (hudShaderProgram != null) {
            hudShaderProgram.cleanUp();
        }
    }
}
