package com.zcking.furyengine.game.examples.simple;

import com.zcking.furyengine.engine.*;
import com.zcking.furyengine.engine.objects.SkyBox;
import com.zcking.furyengine.engine.objects.Terrain;
import com.zcking.furyengine.game.Hud;
import com.zcking.furyengine.game.Renderer;
import com.zcking.furyengine.input.MouseInput;
import com.zcking.furyengine.lighting.DirectionalLight;
import com.zcking.furyengine.lighting.SceneLight;
import com.zcking.furyengine.rendering.Camera;
import com.zcking.furyengine.rendering.weather.Fog;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

public class TerrainDemo implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private Hud hud;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;

    private Terrain terrain;

    public TerrainDemo() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        scene = new Scene();

        // Fog
        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.6f), 0.15f));

        float skyBoxScale = 50.0f;
        float terrainScale = 10;
        //int terrainSize = 3;
        int terrainSize = 3;
        float minY = -0.1f;
        float maxY = 0.1f;
        int textInc = 40;
        terrain = new Terrain(terrainSize, terrainScale, minY, maxY, "/textures/heightmap.png", "/textures/terrain.png", textInc);
        //terrain = new Terrain(terrainSize, terrainScale, minY, maxY, "/textures/heightmap_test.png", "/textures/terrain.png", textInc);
        scene.setGameObjects(terrain.getGameObjects());

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
//        scene.setSkyBox(skyBox);
        window.setClearColor(0.5f, 0.5f, 0.6f, 1.0f);

        // Setup Lights
        setupLights();

        // Create HUD
        hud = new Hud("DEMO");

        camera.getPosition().x = 0.0f;
        camera.getPosition().y = 5.0f;
        camera.getPosition().z = 0.0f;
        camera.getRotation().x = 90;
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(1, 1, 0);
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
        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

            // Update HUD compass
            hud.rotateCompass(camera.getRotation().y);
        }

        // Update camera position
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        // Check if there has been a collision. If true, set the y position to
        // the maximum height
        float height = terrain.getHeight(camera.getPosition());
        if ( camera.getPosition().y <= height )  {
            camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
        }
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

    public static void main( String[] args )
    {
        try {
            IGameLogic gameLogic = new TerrainDemo();
            WindowSettings windowSettings = WindowSettings.create()
                    .withInitialTitle("Terrain Demo")
                    .withInitialWidth(600)
                    .withInitialHeight(480)
                    .withStartMaximized(true)
                    .withVSyncEnabled(true);
            GameEngine engine = new GameEngine(windowSettings, gameLogic);
            engine.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

}
