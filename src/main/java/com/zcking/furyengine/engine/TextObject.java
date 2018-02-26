package com.zcking.furyengine.engine;

import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.rendering.Texture;
import com.zcking.furyengine.utils.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryStack.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public class TextObject extends GameObject {

    private final ByteBuffer ttf;
    private final STBTTFontinfo info;

    private final String text;
    private final int lineCount;

    private final int ascent;
    private final int descent;
    private final int lineGap;
    private int lineOffset;
    private float lineHeight;
    private int fontHeight;
    private int scale;

    public TextObject(String text, String fontFilePath, int fontHeight, int width) {
        this.fontHeight = fontHeight;
        this.lineHeight = fontHeight;

        String t;
        int lc;
        try {
            ByteBuffer source = BufferUtils.ioResourceToByteBuffer(fontFilePath, 4 * 1024);
            t = memUTF8(source).replaceAll("\t", "    ");

            lc = 0;
            Matcher m = Pattern.compile("^.*$", Pattern.MULTILINE).matcher(t);
            while (m.find()) {
                lc++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            t = "Failed to load text.";
            lc = 1;
        }

        this.text = t;
        lineCount = lc;

        try {
            ttf = BufferUtils.ioResourceToByteBuffer(fontFilePath, 512 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, ttf)) {
            throw new IllegalStateException("Failed to initialize font information");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pAscent = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);
        }

        Texture texture = Texture.loadFontTexture(this, width, fontHeight * 32);
        this.setMesh(new Mesh(
                new float[] {0f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, -1f, 1f, 0f},
                new float[] {0f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, -1f, 1f, 0f},
                new float[] {0, 0, 0, 1, 0, 0, 1, 1, 0, -1, 1, 0},
                new int[] {0, 1, 2, 2, 3, 1}
        ));
    }

    public String getText() {
        return text;
    }

    public ByteBuffer getTtf() {
        return ttf;
    }

    public STBTTFontinfo getInfo() {
        return info;
    }

    public int getAscent() {
        return ascent;
    }

    public int getDescent() {
        return descent;
    }

    public int getLineGap() {
        return lineGap;
    }

    public float getFontHeight() {
        return fontHeight;
    }
}
