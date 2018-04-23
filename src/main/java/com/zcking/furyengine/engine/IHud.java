package com.zcking.furyengine.engine;

import com.zcking.furyengine.engine.objects.GameObject;

/**
 * An implementable HUD (Heads-Up Display) for games.
 */
public interface IHud {

    GameObject[] getGameObjects();

    /**
     * Perform any necessary garbage collection. By default, simply
     * iterates over the objects in {@link IHud#getGameObjects()} and
     * calls their cleanup interface.
     */
    default void cleanUp() {
        GameObject[] gameObjects = getGameObjects();
        for (GameObject gameObject : gameObjects) {
            gameObject.getMesh().cleanUp();
        }
    }

}
