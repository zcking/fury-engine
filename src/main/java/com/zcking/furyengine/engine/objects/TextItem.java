package com.zcking.furyengine.engine.objects;

import com.zcking.furyengine.rendering.FontTexture;
import com.zcking.furyengine.rendering.Material;
import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A 2D game object for displaying text in a game. Useful for HUD elements.
 */
public class TextItem extends GameObject {

    private static final float ZPOS = 0.0f;

    private static final int VERTICES_PER_QUAD = 4;

    private String text;
    private FontTexture fontTexture;

    /**
     * Constructs a new text game object.
     * @param text The text to display.
     * @param fontTexture The texture to use for the text.
     * @throws Exception If the texture game object's construction fails.
     */
    public TextItem(String text, FontTexture fontTexture) throws Exception {
        super();
        this.text = text;
        this.fontTexture = fontTexture;
        setMesh(buildMesh());
    }

    private Mesh buildMesh() {
        List<Float> positions = new ArrayList<>();
        List<Float> textCoords = new ArrayList<>();
        float[] normals   = new float[0];
        List<Integer> indices   = new ArrayList<>();
        char[] characters = text.toCharArray();
        int numChars = characters.length;

        float startX = 0;
        for(int i=0; i<numChars; i++) {
            FontTexture.CharInfo charInfo = fontTexture.getCharInfo(characters[i]);

            // Build a character tile composed by two triangles

            // Left Top vertex
            positions.add(startX); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i*VERTICES_PER_QUAD);

            // Left Bottom vertex
            positions.add(startX); // x
            positions.add((float) fontTexture.getHeight()); //y
            positions.add(ZPOS); //z
            textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth() );
            textCoords.add(1.0f);
            indices.add(i*VERTICES_PER_QUAD + 1);

            // Right Bottom vertex
            positions.add(startX + charInfo.getWidth()); // x
            positions.add((float) fontTexture.getHeight()); //y
            positions.add(ZPOS); //z
            textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i*VERTICES_PER_QUAD + 2);

            // Right Top vertex
            positions.add(startX + charInfo.getWidth()); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i*VERTICES_PER_QUAD + 3);

            // Add indices por left top and bottom right vertices
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);

            startX += charInfo.getWidth();
        }

        float[] posArr = ArrayUtils.listToArray(positions);
        float[] textCoordsArr = ArrayUtils.listToArray(textCoords);
        int[] indicesArr = indices.stream().mapToInt(i->i).toArray();
        Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
        mesh.setMaterial(new Material(fontTexture.getTexture()));
        return mesh;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.getMesh().deleteBuffers();
        this.setMesh(buildMesh());
    }
}