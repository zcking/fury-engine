package com.zcking.furyengine.engine;

import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.utils.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryStack.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

public class TextObject {

    private final ByteBuffer ttf;
    private final STBTTFontinfo info;

    private final String text;

    private final int ascent;
    private final int descent;
    private final int lineGap;

    public TextObject(String text, String filePath) {
        this.text = text;

        try {
            ttf = BufferUtils.ioResourceToByteBuffer(filePath, 512 * 1024);
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
}
