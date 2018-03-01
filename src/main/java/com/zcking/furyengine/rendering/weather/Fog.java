package com.zcking.furyengine.rendering.weather;

import org.joml.Vector3f;

public class Fog {

    private boolean enabled;
    private Vector3f color;
    private float density;

    public static Fog NOFOG = new Fog();

    public Fog() {
        enabled = false;
        color = new Vector3f(0, 0, 0);
        density = 0;
    }

    public Fog(boolean enabled, Vector3f color, float density) {
        this.enabled = enabled;
        this.color = color;
        this.density = density;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
