package com.zcking.furyengine.engine.objects;

import com.zcking.furyengine.rendering.Mesh;
import org.joml.Vector3f;

public class GameObject {

    private Mesh[] meshes;

    private final Vector3f position;

    private float scale;

    private final Vector3f rotation;

    public GameObject() {
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public GameObject(Mesh mesh) {
        this();
        this.meshes = new Mesh[]{mesh};
    }

    public GameObject(Mesh[] meshes) {
        this();
        this.meshes = meshes;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public void setMesh(Mesh mesh) {
        if (this.meshes != null) {
            for (Mesh m : meshes) {
                m.cleanUp();
            }
        }
        this.meshes = new Mesh[]{mesh};
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public void cleanUp() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].cleanUp();
        }
    }
}
