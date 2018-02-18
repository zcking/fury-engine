package com.zcking.furyengine.rendering;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {

    private final int id;

    public Texture(String filePath) throws Exception {
        this(loadTexture(filePath));
    }

    public Texture(int id) {
        this.id = id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    public static int loadTexture(String filePath) throws Exception {
        // Load texture file
        PNGDecoder decoder = new PNGDecoder(Texture.class.getResourceAsStream(filePath));

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

        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Generate the mip map data
        glGenerateMipmap(GL_TEXTURE_2D);

        return textureId;
    }

    public void cleanUp() {
        glDeleteTextures(id);
    }

}
