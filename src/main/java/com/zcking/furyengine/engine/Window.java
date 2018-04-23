package com.zcking.furyengine.engine;

import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The main wrapper and control class for the window/display.
 */
public class Window {

    private final String title;

    private int width;
    private int height;
    private long windowHandle;
    private boolean resized;
    private boolean vSync;

    private final WindowSettings windowSettings;

    /**
     * Constructs a new window instance, but does not initialize it.
     * @param settings The window configuration.
     */
    public Window(WindowSettings settings) {
        this.title = settings.getInitialTitle();
        this.width = settings.getInitialWidth();
        this.height = settings.getInitialHeight();
        this.vSync = settings.isvSyncEnabled();

        this.windowSettings = settings;
    }

    /**
     * Initializes the window using the settings passed in at construction.
     * @throws IllegalStateException If GLFW has not been initialized yet (or if it fails).
     * @throws RuntimeException If fails to create the window.
     */
    public void init() {

        // Setup the error callback. The default implementation
        // will print the error message in System.err
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions won't work before this
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure the new window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, windowSettings.isInitiallyVisible() ? GLFW_TRUE : GLFW_FALSE); // window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, windowSettings.isResizable() ? GLFW_TRUE : GLFW_FALSE); // window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, windowSettings.getGlfwContextVersionMajor());
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, windowSettings.getGlfwContextVersionMinor());
        glfwWindowHint(GLFW_OPENGL_PROFILE, windowSettings.getOpenGLProfile());
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, windowSettings.isOpenGLForwardCompat() ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, windowSettings.isStartMaximized() ? GLFW_TRUE : GLFW_FALSE);

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to initialize the GLFW window");
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });

        // Setup a key callback. Called every time a key is pressed, repeated, or released
        glfwSetKeyCallback(windowHandle, windowSettings.getKeyCallback());

        if (windowSettings.isStartCentered()) {
            // Get the resolution of the primary monitor
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    windowHandle,
                    (vidMode.width() - width) / 2,
                    (vidMode.height() - height) / 2
            );
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        if (vSync) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(windowHandle);

        GL.createCapabilities();

        // Set the clear color
        Vector4f clearColor = windowSettings.getClearColor();
        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);

        for (int target : windowSettings.getGlEnableTargets()) {
            glEnable(target);
        }

        if (windowSettings.isAlphaEnabled()) {
            // Support for transparencies
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }

        if (windowSettings.isCullingEnabled()) {
            // Face culling
            glEnable(GL_CULL_FACE);
            glCullFace(windowSettings.getCullFace());
        }

        if (windowSettings.isShowPolygons()) {
            // Shows polygons (useful for demonstration/debugging)
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }
    }

    /**
     * Swaps the window's buffers and polls it for events.
     * Called by the main game engine once per frame.
     */
    public void update() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    /**
     * Sets the window's clear color.
     * @param r The red value of the clear color.
     * @param g The green value of the clear color.
     * @param b The blue value of the clear color.
     * @param alpha The alpha value of the clear color.
     */
    public void setClearColor(float r, float g, float b, float alpha) {
        glClearColor(r, g, b, alpha);
    }

    /**
     * Whether or not a given key was pressed.
     * @param keyCode The key code to check for.
     * @return true if the specified key was pressed; false otherwise.
     */
    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    /**
     * Checks if the window should close due to some window event.
     * @return true if the window should close; false otherwise.
     */
    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public boolean isResized() {
        return resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }
}
