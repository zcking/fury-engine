package com.zcking.furyengine.game.examples.simple;

import com.zcking.furyengine.engine.GameObject;
import com.zcking.furyengine.engine.IGameLogic;
import com.zcking.furyengine.engine.Scene;
import com.zcking.furyengine.game.Hud;
import com.zcking.furyengine.input.MouseInput;
import com.zcking.furyengine.engine.Window;
import com.zcking.furyengine.lighting.DirectionalLight;
import com.zcking.furyengine.lighting.PointLight;
import com.zcking.furyengine.lighting.SceneLight;
import com.zcking.furyengine.lighting.SpotLight;
import com.zcking.furyengine.rendering.*;
import com.zcking.furyengine.engine.graph.OBJLoader;
import com.zcking.furyengine.game.Renderer;
import com.zcking.furyengine.utils.DebugUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class DummyGame implements IGameLogic {

    private final Renderer renderer;
    private final Camera camera;

    private Vector3f cameraInc;
    private float lightAngle;
    private float spotAngle = 0;
    private float spotInc = 1;
    private Hud hud;
    private Scene scene;

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

        scene = new Scene();

        // Game Objects setup
        float reflectance = 1f;
        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/grassblock.png");
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);

        float blockScale = 0.5f;
        float skyBoxScale = 10.0f;
        float extension = 2.0f;

        float startX = extension * (-skyBoxScale + blockScale);
        float startY = -1;
        float startZ = extension * (skyBoxScale - blockScale);
        float inc = blockScale * 2;

        float posX = startX;
        float posZ = startZ;
        float incY = 0;
        int NUM_ROWS = (int) (extension * skyBoxScale * 2 / inc);
        int NUM_COLS = (int) (extension * skyBoxScale * 2 / inc);
        GameObject[] gameObjects = new GameObject[NUM_ROWS * NUM_COLS];
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                GameObject gameObject = new GameObject(mesh);
                gameObject.setScale(blockScale);
                incY = Math.random() > 0.9f ? blockScale * 2 : 0f;
                gameObject.setPosition(posX, startY + incY, posZ);
                gameObjects[i * NUM_COLS + j] = gameObject;

                posX += inc;
            }

            posX = startX;
            posZ -= inc;
        }
        scene.setGameObjects(gameObjects);

        // SkyBox
        SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup lights
        setupLights();

        // Create HUD
        hud = new Hud("DEMO");

        camera.getPosition().x = 0.65f;
        camera.getPosition().y = 1.15f;
        camera.getPosition().z = 4.34f;
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient light
        sceneLight.setAmbientLight(new Vector3f(1, 1, 1));

        // Directional light
        float lightIntensity = 1;
        Vector3f lightPosition = new Vector3f(-1, 0, 0);
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity));
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
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

            // Update HUD compass
            hud.rotateCompass(camera.getRotation().y);
        }

        SceneLight sceneLight = scene.getSceneLight();

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
        renderer.render(window, camera, scene, hud);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        scene.cleanUp();
        hud.cleanUp();
    }
}
