package com.zcking.furyengine.engine;

import com.zcking.furyengine.lighting.SceneLight;
import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.rendering.SkyBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private Map<Mesh, List<GameObject>> meshMap;

    private SkyBox skyBox;

    private SceneLight sceneLight;

    public Scene() {
        meshMap = new HashMap();
    }

    public Map<Mesh, List<GameObject>> getMeshMap() {
        return meshMap;
    }

    public void setGameObjects(GameObject[] gameItems) {
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for (int i=0; i<numGameItems; i++) {
            GameObject gameItem = gameItems[i];
            Mesh mesh = gameItem.getMesh();
            List<GameObject> list = meshMap.get(mesh);
            if ( list == null ) {
                list = new ArrayList<>();
                meshMap.put(mesh, list);
            }
            list.add(gameItem);
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

}
