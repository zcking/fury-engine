package com.zcking.furyengine.utils;

import com.zcking.furyengine.rendering.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.Locale;

import static org.lwjgl.opengl.GL20.*;

public class DebugUtils {

    static boolean listedAllUniforms = false;

    public static void listAllUniforms(int programId) {

        if (!listedAllUniforms) {
            System.out.println("Active uniforms for shader program " + programId + ": ");

            int len = glGetProgrami(programId, GL_ACTIVE_UNIFORMS);
            int strLen = glGetProgrami(programId, GL_ACTIVE_UNIFORM_MAX_LENGTH);

            for (int i = 0; i < len; i++) {
                // TODO: Maybe use this interfacing in the ShaderProgram to dynamically fetch the uniforms?
                String name = glGetActiveUniform(programId, i, strLen, BufferUtils.createIntBuffer(50), BufferUtils.createIntBuffer(50));
                int id = glGetUniformLocation(programId, name);
                System.out.printf("\tUniform #%d : %s\n", id, name);
            }

            listedAllUniforms = true;
        }
    }

}
