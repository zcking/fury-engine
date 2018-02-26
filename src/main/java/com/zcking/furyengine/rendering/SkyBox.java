package com.zcking.furyengine.rendering;

import com.zcking.furyengine.engine.GameObject;
import com.zcking.furyengine.engine.graph.OBJLoader;

public class SkyBox extends GameObject {

    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxTexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }

}
