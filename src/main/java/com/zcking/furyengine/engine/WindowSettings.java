package com.zcking.furyengine.engine;

import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

// Refer to http://www.glfw.org/docs/latest/window_guide.html for more on configuring GLFW windows

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

    public static WindowSettings create() {
        return new WindowSettings()
                .withGlEnableTarget(GL_DEPTH_TEST);
    }

    public WindowSettings withInitiallyVisible(boolean visible) {
        this.initiallyVisible = visible ? GLFW_TRUE : GLFW_FALSE;
        return this;
    }

    public WindowSettings withResizable(boolean resizable) {
        this.resizable = resizable ? GLFW_TRUE : GLFW_FALSE;
        return this;
    }

    public WindowSettings withGlfwContextVersionMajor(int version) {
        this.glfwContextVersionMajor = version;
        return this;
    }

    public WindowSettings withGlfwContextVersionMinor(int version) {
        this.glfwContextVersionMinor = version;
        return this;
    }

    public WindowSettings withOpenGLProfile(int profile) {
        this.openGLProfile = profile;
        return this;
    }

    public WindowSettings withOpenGLForwardCompat(boolean forwardCompat) {
        this.openGLForwardCompat = forwardCompat ? GLFW_TRUE : GLFW_FALSE;
        return this;
    }

    public WindowSettings withInitialTitle(String title) {
        this.initialTitle = title;
        return this;
    }

    public WindowSettings withInitialWidth(int width) {
        this.initialWidth = width;
        return this;
    }

    public WindowSettings withInitialHeight(int height) {
        this.initialHeight = height;
        return this;
    }

    public WindowSettings withVSyncEnabled(boolean enabled) {
        this.vSyncEnabled = enabled;
        return this;
    }

    public WindowSettings withKeyCallback(GLFWKeyCallbackI callback) {
        this.keyCallback = callback;
        return this;
    }

    public WindowSettings withStartCentered(boolean startCentered) {
        this.startCentered = startCentered;
        return this;
    }

    public WindowSettings withClearColor(Vector4f clearColor) {
        this.clearColor = clearColor;
        return this;
    }

    public WindowSettings withGlEnableTargets(List<Integer> enableTargets) {
        this.glEnableTargets = enableTargets;
        return this;
    }

    public WindowSettings withGlEnableTarget(int enableTarget) {
        this.glEnableTargets.add(enableTarget);
        return this;
    }

    public WindowSettings withAlphaEnabled(boolean enabled) {
        this.alphaEnabled = enabled;
        return this;
    }

    public WindowSettings withCullingEnabled(boolean enabled) {
        this.cullingEnabled = enabled;
        return this;
    }

    public WindowSettings withCullFace(int cullFace) {
        this.cullFace = cullFace;
        return this;
    }

    public WindowSettings withShowPolygons(boolean showPolygons) {
        this.showPolygons = showPolygons;
        return this;
    }

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
