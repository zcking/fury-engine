package com.zcking.furyengine.game;

import java.util.List;
import java.util.Map;

import com.zcking.furyengine.engine.graph.animation.AnimatedFrame;
import com.zcking.furyengine.engine.graph.particles.IParticleEmitter;
import com.zcking.furyengine.engine.objects.AnimGameObject;
import com.zcking.furyengine.engine.objects.GameObject;
import com.zcking.furyengine.engine.IHud;
import com.zcking.furyengine.engine.Scene;
import com.zcking.furyengine.engine.Window;
import com.zcking.furyengine.engine.graph.Transformation;
import com.zcking.furyengine.lighting.DirectionalLight;
import com.zcking.furyengine.lighting.PointLight;
import com.zcking.furyengine.lighting.SceneLight;
import com.zcking.furyengine.lighting.SpotLight;
import com.zcking.furyengine.rendering.*;
import com.zcking.furyengine.engine.objects.SkyBox;
import com.zcking.furyengine.utils.ResourceUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * Handles all the rendering operations to OpenGL and the fury engine shaders. Used for examples
 * and demonstration purposes, but could easily be used as a drop-in for new games.
 */
public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private static final int MAX_POINT_LIGHTS = 5;

    private static final int MAX_SPOT_LIGHTS = 5;

    private final Transformation transformation;

    private ShadowMap shadowMap;

    private ShaderProgram depthShaderProgram;

    private ShaderProgram sceneShaderProgram;

    private ShaderProgram hudShaderProgram;

    private ShaderProgram skyBoxShaderProgram;

    private ShaderProgram particlesShaderProgram;

    private final float specularPower;

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
    private static final String UNIFORM_FOG = "fog";
    private static final String UNIFORM_NORMAL_MAP = "normalMap";
    private static final String UNIFORM_MODEL_LV_MAT = "modelLightViewMatrix";
    private static final String UNIFORM_ORTHO_PROJ_MAT = "orthoProjectionMatrix";
    private static final String UNIFORM_SHADOW_MAP = "shadowMap";
    private static final String UNIFORM_JOINTS_MATRIX = "jointsMatrix";

    // HUD shader uniforms
    private static final String UNIFORM_HUD_PROJ_MODEL_MATRIX = "projModelMatrix";
    private static final String UNIFORM_HUD_COLOR = "color";
    private static final String UNIFORM_HUD_HAS_TEXTURE = "hasTexture";

    // SkyBox shader uniforms
    private static final String UNIFORM_SKYBOX_PROJECTION_MATRIX = "projectionMatrix";
    private static final String UNIFORM_SKYBOX_MODEL_VIEW_MATRIX = "modelViewMatrix";
    private static final String UNIFORM_SKYBOX_TEXTURE_SAMPLER = "textureSampler";
    private static final String UNIFORM_SKYBOX_AMBIENT_LIGHT = "ambientLight";

    // Depth shader uniforms
    private static final String UNIFORM_DEPTH_ORTHO_MAT = "orthoProjectionMatrix";
    private static final String UNIFORM_DEPTH_MODEL_MAT = "modelLightViewMatrix";
    private static final String UNIFORM_DEPTH_JOINTS_MATRIX = "jointsMatrix";

    // Particle shader uniforms
    private static final String UNIFORM_PARTICLE_PROJ_MAT = "projectionMatrix";
    private static final String UNIFORM_PARTICLE_MODEL_VIEW_MAT = "modelViewMatrix";
    private static final String UNIFORM_PARTICLE_TEXTURE_SAMPLER = "textureSampler";
    private static final String UNIFORM_PARCICLE_NUM_ROWS = "numRows";
    private static final String UNIFORM_PARCICLE_NUM_COLS = "numCols";
    private static final String UNIFORM_PARTICLE_TEX_XOFFSET = "texXOffset";
    private static final String UNIFORM_PARTICLE_TEX_YOFFSET = "texYOffset";

    /**
     * Constructs the renderer, ready for initialization.
     */
    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    /**
     * Initializes the render and its shader programs.
     * @param window The window to render to.
     * @throws Exception If the initialization fails.
     */
    public void init(Window window) throws Exception {
        shadowMap = new ShadowMap();

        setupDepthShader();
        setupSkyBoxShader();
        setupSceneShader();
        setupParticlesShader();
        setupHudShader();
    }

    /**
     * Render the scene and view to the given window.
     * @param window Window to render to.
     * @param camera Camera to project a view from.
     * @param scene The scene to render.
     * @param hud Optional HUD to render on top of the display.
     */
    public void render(Window window, Camera camera, Scene scene, IHud hud) {
        clear();

        // Render depth map before viewports are set up
        renderDepthMap(window, camera, scene);
        glViewport(0, 0, window.getWidth(), window.getHeight());

        // Update projection and view matrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        renderScene(window, camera, scene);
        if (scene.getSkyBox() != null)
            renderSkyBox(window, camera, scene);
        renderParticles(window, camera, scene);
        if (hud != null)
            renderHud(window, hud);

//        renderAxes(camera); // Todo: create RendererSettings to pass to Renderer class that contains this stuff?
    }

    private void setupDepthShader() throws Exception {
        depthShaderProgram = new ShaderProgram();
        depthShaderProgram.createVertexShader(ResourceUtils.loadResource("/shaders/depth_vertex.glsl"));
        depthShaderProgram.createFragmentShader(ResourceUtils.loadResource("/shaders/depth_fragment.glsl"));
        depthShaderProgram.link();

        depthShaderProgram.createUniform(UNIFORM_DEPTH_ORTHO_MAT);
        depthShaderProgram.createUniform(UNIFORM_DEPTH_MODEL_MAT);

        // Create uniform for joint matrices
        depthShaderProgram.createUniform(UNIFORM_DEPTH_JOINTS_MATRIX);
    }

    private void setupSkyBoxShader() throws Exception {
        skyBoxShaderProgram = new ShaderProgram();
        skyBoxShaderProgram.createVertexShader(ResourceUtils.loadResource("/shaders/skybox_vertex.glsl"));
        skyBoxShaderProgram.createFragmentShader(ResourceUtils.loadResource("/shaders/skybox_fragment.glsl"));
        skyBoxShaderProgram.link();

        // Create uniforms for projection matrix
        skyBoxShaderProgram.createUniform(UNIFORM_SKYBOX_PROJECTION_MATRIX);
        skyBoxShaderProgram.createUniform(UNIFORM_SKYBOX_MODEL_VIEW_MATRIX);
        skyBoxShaderProgram.createUniform(UNIFORM_SKYBOX_TEXTURE_SAMPLER);
        skyBoxShaderProgram.createUniform(UNIFORM_SKYBOX_AMBIENT_LIGHT);
    }

    private void setupSceneShader() throws Exception {
        // Create shader
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(ResourceUtils.loadResource("/shaders/scene_vertex.glsl"));
        sceneShaderProgram.createFragmentShader(ResourceUtils.loadResource("/shaders/scene_fragment.glsl"));
        sceneShaderProgram.link();

        // Create uniforms for modelView and projection matrices
        sceneShaderProgram.createUniform(UNIFORM_PROJECTION_MATRIX);
        sceneShaderProgram.createUniform(UNIFORM_MODEL_VIEW_MATRIX);
        sceneShaderProgram.createUniform(UNIFORM_TEXTURE_SAMPLER);
        sceneShaderProgram.createUniform(UNIFORM_NORMAL_MAP);
        // Create uniform for material
        sceneShaderProgram.createMaterialUniform(UNIFORM_MATERIAL);
        // Create lighting related uniforms
        sceneShaderProgram.createUniform(UNIFORM_SPECULAR_POWER);
        sceneShaderProgram.createUniform(UNIFORM_AMBIENT_LIGHT);
        sceneShaderProgram.createPointLightListUniform(UNIFORM_POINT_LIGHTS, MAX_POINT_LIGHTS);
        sceneShaderProgram.createSpotLightListUniform(UNIFORM_SPOT_LIGHTS, MAX_SPOT_LIGHTS);
        sceneShaderProgram.createDirectionalLightUniform(UNIFORM_DIRECTIONAL_LIGHT);
        sceneShaderProgram.createFogUniform(UNIFORM_FOG);

        // Create uniforms for shadow mapping
        sceneShaderProgram.createUniform(UNIFORM_SHADOW_MAP);
        sceneShaderProgram.createUniform(UNIFORM_ORTHO_PROJ_MAT);
        sceneShaderProgram.createUniform(UNIFORM_MODEL_LV_MAT);

        // Create uniform for joint matrices
        sceneShaderProgram.createUniform(UNIFORM_JOINTS_MATRIX);
    }

    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.createVertexShader(ResourceUtils.loadResource("/shaders/hud_vertex.glsl"));
        hudShaderProgram.createFragmentShader(ResourceUtils.loadResource("/shaders/hud_fragment.glsl"));
        hudShaderProgram.link();

        // Create uniforms for Orthographic-model projection matrix and base colour
        hudShaderProgram.createUniform(UNIFORM_HUD_PROJ_MODEL_MATRIX);
        hudShaderProgram.createUniform(UNIFORM_HUD_COLOR);
        hudShaderProgram.createUniform(UNIFORM_HUD_HAS_TEXTURE);
    }

    private void setupParticlesShader() throws Exception {
        particlesShaderProgram = new ShaderProgram();
        particlesShaderProgram.createVertexShader(ResourceUtils.loadResource("/shaders/particles_vertex.glsl"));
        particlesShaderProgram.createFragmentShader(ResourceUtils.loadResource("/shaders/particles_fragment.glsl"));
        particlesShaderProgram.link();

        particlesShaderProgram.createUniform(UNIFORM_PARTICLE_PROJ_MAT);
        particlesShaderProgram.createUniform(UNIFORM_PARTICLE_MODEL_VIEW_MAT);
        particlesShaderProgram.createUniform(UNIFORM_PARTICLE_TEXTURE_SAMPLER);
        particlesShaderProgram.createUniform(UNIFORM_PARCICLE_NUM_ROWS);
        particlesShaderProgram.createUniform(UNIFORM_PARCICLE_NUM_COLS);
        particlesShaderProgram.createUniform(UNIFORM_PARTICLE_TEX_XOFFSET);
        particlesShaderProgram.createUniform(UNIFORM_PARTICLE_TEX_YOFFSET);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private void renderDepthMap(Window window, Camera camera, Scene scene) {
        // Setup view port to match the texture size
        glBindFramebuffer(GL30.GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        DirectionalLight light = scene.getSceneLight().getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depthShaderProgram.setUniform(UNIFORM_DEPTH_ORTHO_MAT, orthoProjMatrix);
        Map<Mesh, List<GameObject>> mapMeshes = scene.getMeshMap();
        for (Mesh mesh : mapMeshes.keySet()) {
            mesh.renderList(mapMeshes.get(mesh), (GameObject gameObject) -> {
                        Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(gameObject, lightViewMatrix);
                        depthShaderProgram.setUniform(UNIFORM_DEPTH_MODEL_MAT, modelLightViewMatrix);

                        if ( gameObject instanceof AnimGameObject ) {
                            AnimGameObject animGameObject = (AnimGameObject)gameObject;
                            AnimatedFrame frame = animGameObject.getCurrentFrame();
                            depthShaderProgram.setUniform(UNIFORM_DEPTH_JOINTS_MATRIX, frame.getJointMatrices());
                        }
                    }
            );
        }

        // Unbind
        depthShaderProgram.unbind();
        glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    private void renderSkyBox(Window window, Camera camera, Scene scene) {
        SkyBox skyBox = scene.getSkyBox();
        if (skyBox != null) {
            skyBoxShaderProgram.bind();

            skyBoxShaderProgram.setUniform(UNIFORM_SKYBOX_TEXTURE_SAMPLER, 0);

            Matrix4f projectionMatrix = transformation.getProjectionMatrix();
            skyBoxShaderProgram.setUniform(UNIFORM_SKYBOX_PROJECTION_MATRIX, projectionMatrix);
            Matrix4f viewMatrix = transformation.getViewMatrix();
            viewMatrix.m30(0);
            viewMatrix.m31(0);
            viewMatrix.m32(0);
            Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
            skyBoxShaderProgram.setUniform(UNIFORM_SKYBOX_MODEL_VIEW_MATRIX, modelViewMatrix);
            skyBoxShaderProgram.setUniform(UNIFORM_SKYBOX_AMBIENT_LIGHT, scene.getSceneLight().getSkyBoxLight());

            scene.getSkyBox().getMesh().render();

            skyBoxShaderProgram.unbind();
        }
    }

    private void renderScene(Window window, Camera camera, Scene scene) {
        sceneShaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        sceneShaderProgram.setUniform(UNIFORM_PROJECTION_MATRIX, projectionMatrix);
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        sceneShaderProgram.setUniform(UNIFORM_ORTHO_PROJ_MAT, orthoProjMatrix);
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        Matrix4f viewMatrix = transformation.getViewMatrix();

        SceneLight sceneLight = scene.getSceneLight();
        renderLights(viewMatrix, sceneLight);

        sceneShaderProgram.setUniform(UNIFORM_FOG, scene.getFog());
        sceneShaderProgram.setUniform(UNIFORM_TEXTURE_SAMPLER, 0);
        sceneShaderProgram.setUniform(UNIFORM_NORMAL_MAP, 1);
        sceneShaderProgram.setUniform(UNIFORM_SHADOW_MAP, 2);

        // Render each mesh with the associated game objects
        Map<Mesh, List<GameObject>> mapMeshes = scene.getMeshMap();
        for (Mesh mesh : mapMeshes.keySet()) {

            sceneShaderProgram.setUniform(UNIFORM_MATERIAL, mesh.getMaterial());
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
            mesh.renderList(mapMeshes.get(mesh), (GameObject gameObject) -> {

                        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameObject, viewMatrix);
                        sceneShaderProgram.setUniform(UNIFORM_MODEL_VIEW_MATRIX, modelViewMatrix);
                        Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(gameObject, lightViewMatrix);
                        sceneShaderProgram.setUniform(UNIFORM_MODEL_LV_MAT, modelLightViewMatrix);

                        if ( gameObject instanceof AnimGameObject ) {
                            AnimGameObject animGameObject = (AnimGameObject)gameObject;
                            AnimatedFrame frame = animGameObject.getCurrentFrame();
                            sceneShaderProgram.setUniform(UNIFORM_JOINTS_MATRIX, frame.getJointMatrices());
                        }
                    }
            );
        }

        sceneShaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {

        sceneShaderProgram.setUniform(UNIFORM_AMBIENT_LIGHT, sceneLight.getAmbientLight());
        sceneShaderProgram.setUniform(UNIFORM_SPECULAR_POWER, specularPower);

        // Process Point Lights
        PointLight[] pointLightList = sceneLight.getPointLights();
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
            sceneShaderProgram.setUniform(UNIFORM_POINT_LIGHTS, currPointLight, i);
        }

        // Process Spot Lights
        SpotLight[] spotLightList = sceneLight.getSpotLights();
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

            sceneShaderProgram.setUniform(UNIFORM_SPOT_LIGHTS, currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        sceneShaderProgram.setUniform(UNIFORM_DIRECTIONAL_LIGHT, currDirLight);
    }

    private void renderHud(Window window, IHud hud) {
        if (hud != null) {
            hudShaderProgram.bind();

            Matrix4f ortho = transformation.getOrtho2DProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
            for (GameObject gameObject : hud.getGameObjects()) {
                Mesh mesh = gameObject.getMesh();
                // Set orthographic and model matrix for this HUD item
                Matrix4f projModelMatrix = transformation.buildOrthoProjModelMatrix(gameObject, ortho);
                hudShaderProgram.setUniform(UNIFORM_HUD_PROJ_MODEL_MATRIX, projModelMatrix);
                hudShaderProgram.setUniform(UNIFORM_HUD_COLOR, gameObject.getMesh().getMaterial().getAmbientColor());
                hudShaderProgram.setUniform(UNIFORM_HUD_HAS_TEXTURE, gameObject.getMesh().getMaterial().isTextured() ? 1 : 0);

                // Render the mesh for this HUD item
                mesh.render();
            }

            hudShaderProgram.unbind();
        }
    }

    /**
     * Renders the three axis in space (For debugging purposes only
     * @param camera The Camera instance
     */
    private void renderAxes(Camera camera) {
        glPushMatrix();
        glLoadIdentity();
        float rotX = camera.getRotation().x;
        float rotY = camera.getRotation().y;
        float rotZ = 0;
        glRotatef(rotX, 1.0f, 0.0f, 0.0f);
        glRotatef(rotY, 0.0f, 1.0f, 0.0f);
        glRotatef(rotZ, 0.0f, 0.0f, 1.0f);
        glLineWidth(2.0f);

        glBegin(GL_LINES);
        // X Axis
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(1.0f, 0.0f, 0.0f);
        // Y Axis
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 1.0f, 0.0f);
        // Z Axis
        glColor3f(1.0f, 1.0f, 1.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 1.0f);
        glEnd();

        glPopMatrix();
    }

    private void renderParticles(Window window, Camera camera, Scene scene) {
        particlesShaderProgram.bind();

        particlesShaderProgram.setUniform(UNIFORM_PARTICLE_TEXTURE_SAMPLER, 0);
        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        particlesShaderProgram.setUniform(UNIFORM_PARTICLE_PROJ_MAT, projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix();
        IParticleEmitter[] emitters = scene.getParticleEmitters();
        int numEmitters = emitters != null ? emitters.length : 0;

        // Disable depth testing (so order of particles doesn't matter)
        // and configure additive blending for more realistic particles
        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        for (int i = 0; i < numEmitters; i++) {
            IParticleEmitter emitter = emitters[i];
            Mesh mesh = emitter.getBaseParticle().getMesh();

            Texture text = mesh.getMaterial().getTexture();
            particlesShaderProgram.setUniform(UNIFORM_PARCICLE_NUM_ROWS, text.getNumRows());
            particlesShaderProgram.setUniform(UNIFORM_PARCICLE_NUM_COLS, text.getNumCols());

            mesh.renderList(emitter.getParticles(), (GameObject gameObject) -> {
                int col = gameObject.getTextPos() % text.getNumCols();
                int row = gameObject.getTextPos() / text.getNumCols();
                float textXOffset = (float) col / text.getNumCols();
                float textYOffset = (float) row / text.getNumRows();
                particlesShaderProgram.setUniform(UNIFORM_PARTICLE_TEX_XOFFSET, textXOffset);
                particlesShaderProgram.setUniform(UNIFORM_PARTICLE_TEX_YOFFSET, textYOffset);

                Matrix4f modelMatrix = transformation.buildModelMatrix(gameObject);

                viewMatrix.transpose3x3(modelMatrix);
                viewMatrix.scale(gameObject.getScale());

                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                modelViewMatrix.scale(gameObject.getScale());
                particlesShaderProgram.setUniform(UNIFORM_PARTICLE_MODEL_VIEW_MAT, modelViewMatrix);
            });
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);

        particlesShaderProgram.unbind();
    }

    /**
     * Performs all the garbage collection and cleanup
     * of the shader programs and renderer resources.
     */
    public void cleanUp() {
        if (shadowMap != null) {
            shadowMap.cleanUp();
        }
        if (depthShaderProgram != null) {
            depthShaderProgram.cleanUp();
        }
        if (skyBoxShaderProgram != null) {
            skyBoxShaderProgram.cleanUp();
        }
        if (sceneShaderProgram != null) {
            sceneShaderProgram.cleanUp();
        }
        if (hudShaderProgram != null) {
            hudShaderProgram.cleanUp();
        }
        if (particlesShaderProgram != null) {
            particlesShaderProgram.cleanUp();
        }
    }
}
