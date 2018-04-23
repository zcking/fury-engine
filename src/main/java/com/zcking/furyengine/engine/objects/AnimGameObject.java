package com.zcking.furyengine.engine.objects;

import com.zcking.furyengine.engine.graph.animation.AnimatedFrame;
import com.zcking.furyengine.rendering.Mesh;
import org.joml.Matrix4f;

import java.util.List;

/**
 * An animated game object, based on the MD5 specification. Supports animation via "joints" and frames.
 */
public class AnimGameObject extends GameObject {

    private int currentFrame;

    private List<AnimatedFrame> frames;

    private List<Matrix4f> invJointMatrices;

    /**
     * Construct a new animated game object.
     * @param meshes The game object's array of meshes.
     * @param frames The game object's list of {@link AnimatedFrame} (frames).
     * @param invJointMatrices The list of the game object's inverse joint matrices.
     */
    public AnimGameObject(Mesh[] meshes, List<AnimatedFrame> frames, List<Matrix4f> invJointMatrices) {
        super(meshes);
        this.frames = frames;
        this.invJointMatrices = invJointMatrices;
        currentFrame = 0;
    }

    public List<AnimatedFrame> getFrames() {
        return frames;
    }

    public void setFrames(List<AnimatedFrame> frames) {
        this.frames = frames;
    }

    public AnimatedFrame getCurrentFrame() {
        return this.frames.get(currentFrame);
    }

    /**
     * Gets the next frame of the animated game object.
     * Automatically gets the first frame again if the animation has ended.
     * @return The next {@link AnimatedFrame} of the object's animation.
     */
    public AnimatedFrame getNextFrame() {
        int nextFrame = currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            nextFrame = 0;
        }
        return this.frames.get(nextFrame);
    }

    /**
     * Animate the object to its next frame. If the animation has reached
     * the end, it will automatically start over from the beginning.
     */
    public void nextFrame() {
        int nextFrame = currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            currentFrame = 0;
        } else {
            currentFrame = nextFrame;
        }
    }

    public List<Matrix4f> getInvJointMatrices() {
        return invJointMatrices;
    }
}
