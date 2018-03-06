package com.zcking.furyengine.game.examples;

import com.zcking.furyengine.engine.IHud;
import com.zcking.furyengine.engine.Window;
import com.zcking.furyengine.engine.objects.GameObject;
import com.zcking.furyengine.engine.objects.TextItem;
import com.zcking.furyengine.rendering.FontTexture;
import org.joml.Vector4f;

import java.awt.*;

public class SimpleHud implements IHud {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 20);

    private static final String CHARSET = "ISO-8859-1";

    private final GameObject[] gameObjects;

    private final TextItem statusTextItem;

    public SimpleHud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColor(new Vector4f(0.5f, 0.5f, 0.5f, 10f));

        // Create list that holds the objects that compose the HUD
        gameObjects = new GameObject[]{statusTextItem};
    }

    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }

    @Override
    public GameObject[] getGameObjects() {
        return gameObjects;
    }

    public void updateSize(Window window) {
        this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
    }

}
