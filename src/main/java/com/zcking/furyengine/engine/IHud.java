package com.zcking.furyengine.engine;

public interface IHud {

    GameObject[] getGameObjects();

    default void cleanUp() {
        GameObject[] gameObjects = getGameObjects();
        for (GameObject gameObject : gameObjects) {
            gameObject.getMesh().cleanUp();
        }
    }

}
