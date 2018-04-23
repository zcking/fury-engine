package com.zcking.furyengine.engine;

import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

// Refer to http://www.glfw.org/docs/latest/window_guide.html for more on configuring GLFW windows

/**
 * Fluent API for configuring {@link Window} instances. Wraps all the window settings
 * for an simple configuration.
 */
public class WindowSettings {

    private int initiallyVisible = GLFW_FALSE;

    private int resizable = GLFW_TRUE;

    private int glfwContextVersionMajor = 3;

    private int glfwContextVersionMinor = 2;

    private int openGLProfile = GLFW_OPENGL_CORE_PROFILE;

    private int openGLForwardCompat = GLFW_TRUE;

    private String initialTitle = "Fury Engine";

    private int initialWidth = 600;

    private int initialHeight = 480;

    private boolean vSyncEnabled = true;

    private GLFWKeyCallbackI keyCallback = (windowId, key, scanCode, action, mods) -> {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(windowId, true);
        }
    };

    private boolean startCentered = true;

    private Vector4f clearColor = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);

    private List<Integer> glEnableTargets = new ArrayList<>();

    private boolean alphaEnabled = true;

    private boolean cullingEnabled = true;

    private int cullFace = GL_BACK;

    private boolean showPolygons = false;

    private int startMaximized = GLFW_TRUE;


    private WindowSettings() { }

    /**
     * Creates a new, default window settings instance, which you can then
     * configure using the fluent interface.
     * @return A blank instance of the {@link WindowSettings}.
     */
    public static WindowSettings create() {
        return new WindowSettings()
                .withGlEnableTarget(GL_DEPTH_TEST);
    }

    /**
     * Should the window be visible upon creation?
     * @param visible Whether or not the window should be visible.
     * @return The modified settings.
     */
    public WindowSettings withInitiallyVisible(boolean visible) {
        this.initiallyVisible = visible ? GLFW_TRUE : GLFW_FALSE;
        return this;
    }

    /**
     * Should the window be able to be resized or not?
     * @param resizable Whether or not the window will be resizable.
     * @return The modified settings.
     */
    public WindowSettings withResizable(boolean resizable) {
        this.resizable = resizable ? GLFW_TRUE : GLFW_FALSE;
        return this;
    }

    /**
     * Change the major version of the GLFW context that is used.
     * @param version The GLFW major version.
     * @return The modified settings.
     */
    public WindowSettings withGlfwContextVersionMajor(int version) {
        this.glfwContextVersionMajor = version;
        return this;
    }

    /**
     * Change the minor version of the GLFW context that is used.
     * @param version The GLFW minor version.
     * @return The modified settings.
     */
    public WindowSettings withGlfwContextVersionMinor(int version) {
        this.glfwContextVersionMinor = version;
        return this;
    }

    /**
     * Generic OpenGL profile setter for the window.
     * @param profile The OpenGL profile to set.
     * @return The modified settings.
     */
    public WindowSettings withOpenGLProfile(int profile) {
        this.openGLProfile = profile;
        return this;
    }

    /**
     * Whether or not the window's OpenGL context should support
     * forward compatibility.
     * @param forwardCompat Whether or not forward compatibility is supported.
     * @return The modified settings.
     */
    public WindowSettings withOpenGLForwardCompat(boolean forwardCompat) {
        this.openGLForwardCompat = forwardCompat ? GLFW_TRUE : GLFW_FALSE;
        return this;
    }

    /**
     * The text to display on the window's title bar.
     * @param title The title text.
     * @return The modified settings.
     */
    public WindowSettings withInitialTitle(String title) {
        this.initialTitle = title;
        return this;
    }

    /**
     * The initial width for the window.
     * @param width The window width.
     * @return The modified settings.
     */
    public WindowSettings withInitialWidth(int width) {
        this.initialWidth = width;
        return this;
    }

    /**
     * The initial height for the window.
     * @param height The window height.
     * @return The modified settings.
     */
    public WindowSettings withInitialHeight(int height) {
        this.initialHeight = height;
        return this;
    }

    /**
     * Should V-Sync be enabled for this window? Enabling this
     * ensures the display refreshes at a rate that the monitor can
     * handle.
     * @param enabled Whether or not V-Sync is enabled.
     * @return The modified settings.
     */
    public WindowSettings withVSyncEnabled(boolean enabled) {
        this.vSyncEnabled = enabled;
        return this;
    }

    /**
     * Specify a callback for GLFW key events.
     * @param callback The callback implementation.
     * @return The modified settings.
     */
    public WindowSettings withKeyCallback(GLFWKeyCallbackI callback) {
        this.keyCallback = callback;
        return this;
    }

    /**
     * Should the window be centered in the display upon creation?
     * @param startCentered Whether or not window is centered.
     * @return The modified settings.
     */
    public WindowSettings withStartCentered(boolean startCentered) {
        this.startCentered = startCentered;
        return this;
    }

    /**
     * Specify the clear color for the window; this is the color
     * to display where nothing is rendered.
     * @param clearColor The color (r, g, b, a) to clear with.
     * @return The modified settings.
     */
    public WindowSettings withClearColor(Vector4f clearColor) {
        this.clearColor = clearColor;
        return this;
    }

    /**
     * Enable one or more OpenGL targets for further OpenGL configuration.
     * Refer to the OpenGL documentation for more information.
     * @param enableTargets A list of the targets to enable.
     * @return The modified settings.
     */
    public WindowSettings withGlEnableTargets(List<Integer> enableTargets) {
        this.glEnableTargets = enableTargets;
        return this;
    }

    /**
     * Enable one OpenGL target for further OpenGL configuration.
     * Refer to the OpenGL documentation for more information.
     * @param enableTarget The OpenGL target to enable.
     * @return The modified settings.
     */
    public WindowSettings withGlEnableTarget(int enableTarget) {
        this.glEnableTargets.add(enableTarget);
        return this;
    }

    /**
     * Should the alpha channel be enabled for this window,
     * to support rendering of transparencies?
     * @param enabled Alpha enabled or not.
     * @return The modified settings.
     */
    public WindowSettings withAlphaEnabled(boolean enabled) {
        this.alphaEnabled = enabled;
        return this;
    }

    /**
     * Should culling be enabled for this window?
     * @param enabled Culling enabled or not.
     * @return The modified settings.
     */
    public WindowSettings withCullingEnabled(boolean enabled) {
        this.cullingEnabled = enabled;
        return this;
    }

    /**
     * Specify the face to cull, if culling is enabled.
     * @param cullFace The face to cull (skip rendering for).
     * @return The modified settings.
     */
    public WindowSettings withCullFace(int cullFace) {
        this.cullFace = cullFace;
        return this;
    }

    /**
     * Should the window's rendering display the polygon outlines?
     * Mainly used for debugging.
     * @param showPolygons Whether or not should show polygons on render.
     * @return The modified settings.
     */
    public WindowSettings withShowPolygons(boolean showPolygons) {
        this.showPolygons = showPolygons;
        return this;
    }

    /**
     * Should the window be maximized upon creation?
     * @param startMaximized Maximized or not.
     * @return The modified settings.
     */
    public WindowSettings withStartMaximized(boolean startMaximized) {
        this.startMaximized = startMaximized ? GLFW_TRUE : GLFW_FALSE;
        return this;
    }

    public boolean isInitiallyVisible() {
        return initiallyVisible == GLFW_TRUE;
    }

    public boolean isResizable() {
        return resizable == GLFW_TRUE;
    }

    public int getGlfwContextVersionMajor() {
        return glfwContextVersionMajor;
    }

    public int getGlfwContextVersionMinor() {
        return glfwContextVersionMinor;
    }

    public int getOpenGLProfile() {
        return openGLProfile;
    }

    public boolean isOpenGLForwardCompat() {
        return openGLForwardCompat == GLFW_TRUE;
    }

    public String getInitialTitle() {
        return initialTitle;
    }

    public int getInitialWidth() {
        return initialWidth;
    }

    public int getInitialHeight() {
        return initialHeight;
    }

    public boolean isvSyncEnabled() {
        return vSyncEnabled;
    }

    public GLFWKeyCallbackI getKeyCallback() {
        return keyCallback;
    }

    public boolean isStartCentered() {
        return startCentered;
    }

    public Vector4f getClearColor() {
        return clearColor;
    }

    public List<Integer> getGlEnableTargets() {
        return glEnableTargets;
    }

    public boolean isAlphaEnabled() {
        return alphaEnabled;
    }

    public boolean isCullingEnabled() {
        return cullingEnabled;
    }

    public int getCullFace() {
        return cullFace;
    }

    public boolean isShowPolygons() {
        return showPolygons;
    }

    public boolean isStartMaximized() {
        return startMaximized == GLFW_TRUE;
    }
}
