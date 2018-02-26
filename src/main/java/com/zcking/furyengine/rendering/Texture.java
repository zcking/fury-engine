package com.zcking.furyengine.rendering;

import com.zcking.furyengine.engine.TextObject;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import static org.lwjgl.stb.STBTruetype.*;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {

    private final int id;
    private final int width;
    private final int height;
    private final STBTTBakedChar.Buffer cData;

    public Texture(String filePath) throws Exception {
        this(Texture.class.getResourceAsStream(filePath));
    }

    public Texture(InputStream is) throws Exception {
        // Load texture file
        PNGDecoder decoder = new PNGDecoder(is);

        this.width = decoder.getWidth();
        this.height = decoder.getHeight();

        // Load texture contents into a byte buffer (4 bytes per pixel)
        ByteBuffer buffer = ByteBuffer.allocateDirect(
                4 * decoder.getWidth() * decoder.getHeight()
        );
        decoder.decode(buffer, 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
        buffer.flip();

        // Create a new OpenGL texture
        int textureId = glGenTextures();

        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell OpenGL how to unpack the bytes. Each component is 1 byte
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Generate the mip map data
        glGenerateMipmap(GL_TEXTURE_2D);

        cData = null;

        this.id = textureId;
    }

    protected Texture(int id, int width, int height, STBTTBakedChar.Buffer cData) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.cData = cData;
    }

    public static Texture loadFontTexture(TextObject textObject, int BITMAP_W, int BITMAP_H) {
        int textureId = glGenTextures();
        STBTTBakedChar.Buffer cData = STBTTBakedChar.malloc(96);

        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
        int lineOffset = 1;
        stbtt_BakeFontBitmap(
                textObject.getTtf(),
                textObject.getFontHeight() * 0.5f + 4.0f - lineOffset * textObject.getFontHeight(),
                bitmap,
                BITMAP_W, BITMAP_H,
                32, cData);

        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W,
                BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        return new Texture(textureId, BITMAP_H, BITMAP_H, cData);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    public void cleanUp() {
        glDeleteTextures(id);
        if (cData != null) {
            cData.free();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
