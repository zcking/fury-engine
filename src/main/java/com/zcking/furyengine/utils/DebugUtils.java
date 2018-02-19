package com.zcking.furyengine.utils;

import com.zcking.furyengine.rendering.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.Locale;

import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.glGetActiveUniform;
import static org.lwjgl.opengl.GL20.glGetProgramiv;

public class DebugUtils {

    static boolean listedAllUniforms = false;

    public static void listAllUniforms(int programId) {

        if (!listedAllUniforms) {
            IntBuffer size = BufferUtils.createIntBuffer(100);
            IntBuffer count = BufferUtils.createIntBuffer(100);
            IntBuffer type = BufferUtils.createIntBuffer(100);
            IntBuffer length = BufferUtils.createIntBuffer(100);
            ByteBuffer name = BufferUtils.createByteBuffer(100);
            glGetProgramiv(programId, GL_ACTIVE_UNIFORMS, count);

            System.out.println("Active uniforms for shader program " + programId + ": ");
            for (int i = 0; i < count.get(); i++) {
                glGetActiveUniform(programId, i, length, size, type, name);
                try {
                    byte[] nameData = new byte[name.remaining()];
                    name.get(nameData);
                    System.out.printf(Locale.ENGLISH, "Uniform #%d Type: %s Name: %s\n", i, type.get(),
                            new String(nameData, "ASCII"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            listedAllUniforms = true;
        }
    }

}
