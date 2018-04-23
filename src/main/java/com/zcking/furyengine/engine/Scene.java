package com.zcking.furyengine.engine;

import com.zcking.furyengine.engine.graph.particles.IParticleEmitter;
import com.zcking.furyengine.engine.objects.GameObject;
import com.zcking.furyengine.lighting.SceneLight;
import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.engine.objects.SkyBox;
import com.zcking.furyengine.rendering.weather.Fog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A container for common "scene" items (e.g. mesh collection, skybox, lighting objects, etc.).
 * Can be used to simply game implementations.
 */
public class Scene {

    private Map<Mesh, List<GameObject>> meshMap;

    private SkyBox skyBox;

    private SceneLight sceneLight;

    private Fog fog;

    private IParticleEmitter[] particleEmitters;

    /**
     * Constructs a new {@link Scene} and initializes the {@link Scene#meshMap}
     * collection and {@link Scene#fog}.
     */
    public Scene() {
        meshMap = new HashMap<>();
        fog = Fog.NOFOG;
    }

    public Map<Mesh, List<GameObject>> getMeshMap() {
        return meshMap;
    }

    /**
     * Seeds the game objects from an array. Automatically maps
     * the meshes to game objects, based on the stored meshes in the objects.
     * @param gameItems The array of {@link GameObject} to store.
     */
    public void setGameObjects(GameObject[] gameItems) {
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for (int i=0; i<numGameItems; i++) {
            GameObject gameItem = gameItems[i];
            Mesh[] meshes = gameItem.getMeshes();
            for (Mesh mesh : meshes) {
                List<GameObject> list = meshMap.get(mesh);
                if (list == null) {
                    list = new ArrayList<>();
                    meshMap.put(mesh, list);
                }
                list.add(gameItem);
            }
        }
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

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    /**
     * Perform any necessary garbage collection.
     */
    public void cleanUp() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
        for (IParticleEmitter particleEmitter : particleEmitters) {
            particleEmitter.cleanUp();
        }
    }

    public IParticleEmitter[] getParticleEmitters() {
        return particleEmitters;
    }

    public void setParticleEmitters(IParticleEmitter[] particleEmitters) {
        this.particleEmitters = particleEmitters;
    }
}
