package com.zcking.furyengine.game;

import com.zcking.furyengine.engine.GameObject;
import com.zcking.furyengine.engine.IHud;
import com.zcking.furyengine.engine.TextItem;
import com.zcking.furyengine.engine.Window;
import org.joml.Vector4f;

public class Hud implements IHud {

    private static final int FONT_COLS = 16;

    private static final int FONT_ROWS = 16;

    private static final String FONT_TEXTURE = "/textures/font_texture.png";

    private final GameObject[] gameObjects;

    private final TextItem statusTextItem;

    public Hud(String statusText) throws Exception {
        this.statusTextItem = new TextItem(statusText, FONT_TEXTURE, FONT_COLS, FONT_ROWS);
        this.statusTextItem.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
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
