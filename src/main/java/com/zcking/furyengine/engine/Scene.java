package com.zcking.furyengine.engine;

import com.zcking.furyengine.lighting.SceneLight;
import com.zcking.furyengine.rendering.SkyBox;

public class Scene {

    private GameObject[] gameObjects;
    private SkyBox skyBox;
    private SceneLight sceneLight;

    public GameObject[] getGameObjects() {
        return gameObjects;
    }

    public void setGameObjects(GameObject[] gameObjects) {
        this.gameObjects = gameObjects;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    public void cleanUp() {
        for (GameObject gameObject : gameObjects) {
            gameObject.getMesh().cleanUp();
        }
    }

}
