package com.zcking.furyengine.engine.objects;

import com.zcking.furyengine.rendering.HeightMapMesh;

public class Terrain {

    private final GameObject[] gameObjects;

    public Terrain(int blocksPerRow, float scale, float minY, float maxY, String heightMap, String textureFile, int textInc) throws Exception {
        gameObjects = new GameObject[blocksPerRow * blocksPerRow];
        HeightMapMesh heightMapMesh = new HeightMapMesh(minY, maxY, heightMap, textureFile, textInc);

        for (int row = 0; row < blocksPerRow; row++) {
            for (int col = 0; col < blocksPerRow; col++) {
                float xDisplacement = (col - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapMesh.getXLength();
                float zDisplacement = (row - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapMesh.getZLength();

                GameObject terrainBlock = new GameObject(heightMapMesh.getMesh());
                terrainBlock.setScale(scale);
                terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
                gameObjects[row * blocksPerRow + col] = terrainBlock;
            }
        }
    }

    public GameObject[] getGameObjects() {
        return gameObjects;
    }
}
