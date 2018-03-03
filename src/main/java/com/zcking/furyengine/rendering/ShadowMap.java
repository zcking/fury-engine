package com.zcking.furyengine.rendering;

import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

public class ShadowMap {

    public static final int SHADOW_MAP_WIDTH = 1024;
    public static final int SHADOW_MAP_HEIGHT = 1024;

    private final int depthMapFBO;
    private final Texture depthMap;

    public ShadowMap() throws Exception {
        // Create a new FBO for rendering the depth map
        depthMapFBO = glGenFramebuffers();

        // Create the actual texture for the depth map
        depthMap = new Texture(
                SHADOW_MAP_WIDTH,
                SHADOW_MAP_HEIGHT,
                GL_DEPTH_COMPONENT
        );

        // Attach the depth map texture to the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glFramebufferTexture2D(
                GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D, depthMap.getId(), 0
        );

        // Set only depth, no color buffer
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }

        // Unbind the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getDepthMapFBO() {
        return depthMapFBO;
    }

    public Texture getDepthMapTexture() {
        return depthMap;
    }

    public void cleanUp() {
        glDeleteFramebuffers(depthMapFBO);
        depthMap.cleanUp();
    }
}
