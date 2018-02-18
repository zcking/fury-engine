package com.zcking.furyengine.game.examples.simple;

import com.zcking.furyengine.engine.GameObject;
import com.zcking.furyengine.engine.IGameLogic;
import com.zcking.furyengine.input.MouseInput;
import com.zcking.furyengine.engine.Window;
import com.zcking.furyengine.lighting.DirectionalLight;
import com.zcking.furyengine.lighting.PointLight;
import com.zcking.furyengine.rendering.Camera;
import com.zcking.furyengine.rendering.Material;
import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.engine.graph.OBJLoader;
import com.zcking.furyengine.game.Renderer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class DummyGame implements IGameLogic {

    private final Renderer renderer;
    private GameObject[] gameObjects;
    private final Camera camera;

    private Vector3f cameraInc;
    private PointLight pointLight;
    private DirectionalLight directionalLight;
    private Vector3f ambientLight;
    private float lightAngle;

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
        renderer.init(window);

        float reflectance = 1f;
        Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        Material material = new Material(new Vector4f(0.2f, 0.5f, 0.5f, 0), reflectance);
        mesh.setMaterial(material);

//        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
//        Texture texture = new Texture("/textures/grassblock.png");
//        mesh.setTexture(texture);

        GameObject gameItem1 = new GameObject(mesh);
        gameItem1.setScale(1.5f);
        gameItem1.setPosition(0, 0, -2);
        gameObjects = new GameObject[]{gameItem1};

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColor, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0, 0, 1);
        pointLight.setAttenuation(att);

        lightPosition = new Vector3f(-1, 0, 0); // "East"
        lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);
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
        float lightPos = pointLight.getPosition().z;
        if (window.isKeyPressed(GLFW_KEY_N)) {
            this.pointLight.getPosition().z = lightPos + 0.1f;
        } else if (window.isKeyPressed(GLFW_KEY_M)) {
            this.pointLight.getPosition().z = lightPos - 0.1f;
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

        // Update directional light direction, intensity and color
        // (simulating day/night cycle)
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
            directionalLight.setColor(new Vector3f(1, 1, 1));
        }

        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float)Math.sin(angRad);
        directionalLight.getDirection().y = (float)Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameObjects, ambientLight, pointLight, directionalLight);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        for (GameObject gameObject : gameObjects) {
            gameObject.getMesh().cleanUp();
        }
    }
}
