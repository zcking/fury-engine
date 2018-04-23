package com.zcking.furyengine.engine.objects;

import com.zcking.furyengine.engine.loaders.obj.OBJLoader;
import com.zcking.furyengine.rendering.Material;
import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.rendering.Texture;

/**
 * A skybox game object for rendering a cube for the sky.
 */
public class SkyBox extends GameObject {

    /**
     * Constructs a new skybox with its texture and model.
     * @param objModel The skybox model file path, on the classpath.
     * @param textureFile The file path for the skybox texture, on the classpath.
     * @throws Exception If the skybox construction fails.
     */
    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxTexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }

}
