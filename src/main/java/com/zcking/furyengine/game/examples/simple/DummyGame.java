package com.zcking.furyengine.game.examples.simple;

import com.zcking.furyengine.engine.GameObject;
import com.zcking.furyengine.engine.IGameLogic;
import com.zcking.furyengine.game.Hud;
import com.zcking.furyengine.input.MouseInput;
import com.zcking.furyengine.engine.Window;
import com.zcking.furyengine.lighting.DirectionalLight;
import com.zcking.furyengine.lighting.PointLight;
import com.zcking.furyengine.lighting.SceneLight;
import com.zcking.furyengine.lighting.SpotLight;
import com.zcking.furyengine.rendering.Camera;
import com.zcking.furyengine.rendering.Material;
import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.engine.graph.OBJLoader;
import com.zcking.furyengine.game.Renderer;
import com.zcking.furyengine.rendering.Texture;
import com.zcking.furyengine.utils.DebugUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class DummyGame implements IGameLogic {

    private final Renderer renderer;
    private GameObject[] gameObjects;
    private final Camera camera;

    private Vector3f cameraInc;
    private float lightAngle;
    private float spotAngle = 0;
    private float spotInc = 1;
    private Hud hud;
    private SceneLight sceneLight;

    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.8f;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        try {
            renderer.init(window);
        } catch (Exception e) {
            DebugUtils.listAllUniforms(renderer.getSceneShaderProgram().getProgramId());
            throw e;
        }

        float reflectance = 1f;
        //Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        //Material material = new Material(new Vector3f(0.2f, 0.5f, 0.5f), reflectance);

        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/grassblock.png");
        Material material = new Material(texture, reflectance);

        sceneLight = new SceneLight();
        mesh.setMaterial(material);
        GameObject gameObject = new GameObject(mesh);
        gameObject.setScale(0.5f);
        gameObject.setPosition(0, 0, -2);
        //gameObject.setPosition(0, 0, -2);
        //gameObject.setScale(0.1f);
        //gameObject.setPosition(0, 0, -2);
        //gameObject.setPosition(0, 0, -0.2f);
        gameObjects = new GameObject[]{gameObject};

        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));

        // Point Light
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 1.0f;
        PointLight pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        sceneLight.setPointLights(new PointLight[]{pointLight});

        // Spot Light
        lightPosition = new Vector3f(0, 0.0f, 10f);
        pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        att = new PointLight.Attenuation(0.0f, 0.0f, 0.02f);
        pointLight.setAttenuation(att);
        Vector3f coneDir = new Vector3f(0, 0, -1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
        sceneLight.setSpotLights(new SpotLight[]{spotLight, new SpotLight(spotLight)});

        lightPosition = new Vector3f(-1, 0, 0);
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity));

        // Create HUD
        hud = new Hud("DEMO");
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
        SpotLight[] spotLights = sceneLight.getSpotLights();
        float lightPos = spotLights[0].getPointLight().getPosition().z;
        if (window.isKeyPressed(GLFW_KEY_N)) {
            spotLights[0].getPointLight().getPosition().z = lightPos + 0.1f;
        } else if (window.isKeyPressed(GLFW_KEY_M)) {
            spotLights[0].getPointLight().getPosition().z = lightPos - 0.1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        // Update spot light direction
        spotAngle += spotInc * 0.05f;
        if (spotAngle > 2) {
            spotInc = -1;
        } else if (spotAngle < -2) {
            spotInc = 1;
        }
        double spotAngleRad = Math.toRadians(spotAngle);
        SpotLight[] spotLights = sceneLight.getSpotLights();
        Vector3f coneDir = spotLights[0].getConeDirection();
        coneDir.y = (float) Math.sin(spotAngleRad);

        // Update directional light direction, intensity and colour
        DirectionalLight directionalLight = sceneLight.getDirectionalLight();
        lightAngle += 1.1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        hud.updateSize(window);
        renderer.render(window, camera, gameObjects, sceneLight, hud);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        for (GameObject gameObject : gameObjects) {
            gameObject.getMesh().cleanUp();
        }
        hud.cleanUp();
    }
}
