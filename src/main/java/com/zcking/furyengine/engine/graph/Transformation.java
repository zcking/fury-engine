package com.zcking.furyengine.engine.graph;

import com.zcking.furyengine.engine.objects.GameObject;
import com.zcking.furyengine.rendering.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Helper/container for matrix transformations (e.g. model-view matrix, projection matrix, etc.).
 * Useful for passing matrix data to the shaders.
 */
public class Transformation {

    private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);

    private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);

    private final Matrix4f projectionMatrix;

    private final Matrix4f modelMatrix;

    private final Matrix4f modelViewMatrix;

    private final Matrix4f modelLightMatrix;

    private final Matrix4f modelLightViewMatrix;

    private final Matrix4f viewMatrix;

    private final Matrix4f lightViewMatrix;

    private final Matrix4f orthoProjMatrix;

    private final Matrix4f ortho2DMatrix;

    private final Matrix4f orthoModelMatrix;

    /**
     * Constructs a new, blank transformation collection.
     */
    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        modelLightMatrix = new Matrix4f();
        modelLightViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        orthoProjMatrix = new Matrix4f();
        ortho2DMatrix = new Matrix4f();
        orthoModelMatrix = new Matrix4f();
        lightViewMatrix = new Matrix4f();
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Set the projection matrix, according to the given aspect ratio and near/far planes.
     * @param fov The Field-of-View for the frustum.
     * @param width The width of the frustum.
     * @param height The height of the frustum.
     * @param zNear The near plane distance.
     * @param zFar The far plane distance.
     * @return The computed projection matrix.
     */
    public Matrix4f updateProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public final Matrix4f getOrthoProjectionMatrix() {
        return orthoProjMatrix;
    }

    /**
     * Sets the orthographic projection matrix, according to the orthographic parameters.
     * @param left The left boundary.
     * @param right The right boundary.
     * @param bottom The bottom boundary.
     * @param top The top boundary.
     * @param zNear The near plane distance.
     * @param zFar The far plane distance.
     * @return The computed orthographic projection matrix.
     */
    public Matrix4f updateOrthoProjectionMatrix(float left, float right, float bottom, float top, float zNear, float zFar) {
        orthoProjMatrix.identity();
        orthoProjMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
        return orthoProjMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    /**
     * Sets the view matrix based on the current state of the {@link Camera} instance given.
     * @param camera The camera instance to base the "view" on.
     * @return The computed view matrix.
     */
    public Matrix4f updateViewMatrix(Camera camera) {
        return updateGenericViewMatrix(camera.getPosition(), camera.getRotation(), viewMatrix);
    }

    public Matrix4f getLightViewMatrix() {
        return lightViewMatrix;
    }

    public void setLightViewMatrix(Matrix4f lightViewMatrix) {
        this.lightViewMatrix.set(lightViewMatrix);
    }

    public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {
        return updateGenericViewMatrix(position, rotation, lightViewMatrix);
    }

    private Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
        matrix.identity();
        // First do the rotation so camera rotates over its position
        matrix.rotate((float)Math.toRadians(rotation.x), X_AXIS)
                .rotate((float)Math.toRadians(rotation.y), Y_AXIS);
        // Then do the translation
        matrix.translate(-position.x, -position.y, -position.z);
        return matrix;
    }

    public final Matrix4f getOrtho2DProjectionMatrix(float left, float right, float bottom, float top) {
        ortho2DMatrix.identity();
        ortho2DMatrix.setOrtho2D(left, right, bottom, top);
        return ortho2DMatrix;
    }

    public Matrix4f buildModelMatrix(GameObject gameObject) {
        Vector3f rotation = gameObject.getRotation();
        modelMatrix.identity().translate(gameObject.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameObject.getScale());
        return modelMatrix;
    }

    public Matrix4f buildModelViewMatrix(GameObject gameObject, Matrix4f matrix) {
        Vector3f rotation = gameObject.getRotation();
        modelMatrix.identity().translate(gameObject.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameObject.getScale());
        return buildModelViewMatrix(modelMatrix, viewMatrix);
    }

    public Matrix4f buildModelViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
        modelViewMatrix.set(viewMatrix);
        return modelViewMatrix.mul(modelMatrix);
    }

    public Matrix4f buildModelLightViewMatrix(GameObject gameObject, Matrix4f matrix) {
        Vector3f rotation = gameObject.getRotation();
        modelLightMatrix.identity().translate(gameObject.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameObject.getScale());
        modelLightViewMatrix.set(matrix);
        return modelLightViewMatrix.mul(modelLightMatrix);
    }

    public Matrix4f buildOrthoProjModelMatrix(GameObject gameObject, Matrix4f orthoMatrix) {
        Vector3f rotation = gameObject.getRotation();
        modelMatrix.identity().translate(gameObject.getPosition()).
                rotateX((float) Math.toRadians(-rotation.x)).
                rotateY((float) Math.toRadians(-rotation.y)).
                rotateZ((float) Math.toRadians(-rotation.z)).
                scale(gameObject.getScale());
        orthoModelMatrix.set(orthoMatrix);
        orthoModelMatrix.mul(modelMatrix);
        return orthoModelMatrix;
    }

}
