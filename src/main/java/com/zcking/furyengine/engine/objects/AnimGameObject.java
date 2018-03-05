package com.zcking.furyengine.engine.objects;

import com.zcking.furyengine.engine.graph.animation.AnimatedFrame;
import com.zcking.furyengine.rendering.Mesh;
import org.joml.Matrix4f;

import java.util.List;

public class AnimGameObject extends GameObject {

    private int currentFrame;

    private List<AnimatedFrame> frames;

    private List<Matrix4f> invJointMatrices;

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

    public AnimatedFrame getNextFrame() {
        int nextFrame = currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            nextFrame = 0;
        }
        return this.frames.get(nextFrame);
    }

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
