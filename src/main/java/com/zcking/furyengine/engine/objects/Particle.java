package com.zcking.furyengine.engine.objects;

import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.rendering.Texture;
import org.joml.Vector3f;

/**
 * Implements a simple particle that can be used by an implemented
 * {@link com.zcking.furyengine.engine.graph.particles.IParticleEmitter} for
 * particle effects.
 */
public class Particle extends GameObject {

    private long updateTextureMillis;

    private long currentAnimTimeMillis;

    private Vector3f speed;

    /**
     * Time to live for particle in milliseconds.
     */
    private long ttl;

    private int animFrames;

    /**
     * Constructs a new particle.
     * @param mesh The mesh for the particle game object.
     * @param speed The velocity of the particle.
     * @param ttl The time-to-live for the particle (in milliseconds).
     * @param updateTextureMillis The texture update rate for the particle (in milliseconds).
     */
    public Particle(Mesh mesh, Vector3f speed, long ttl, long updateTextureMillis) {
        super(mesh);
        this.speed = new Vector3f(speed);
        this.ttl = ttl;
        this.updateTextureMillis = updateTextureMillis;
        this.currentAnimTimeMillis = 0;
        Texture texture = this.getMesh().getMaterial().getTexture();
        this.animFrames = texture.getNumCols() * texture.getNumRows();
    }

    /**
     * Constructs a new particle.
     * @param mesh The mesh for the particle game object.
     * @param speed The velocity of the particle.
     * @param ttl The time-to-live for the particle (in milliseconds).
     */
    public Particle(Mesh mesh, Vector3f speed, long ttl) {
        super(mesh);
        this.speed = new Vector3f(speed);
        this.ttl = ttl;
    }

    /**
     * Construct a copy of a particle.
     * @param baseParticle The particle to copy.
     */
    public Particle(Particle baseParticle) {
        super(baseParticle.getMesh());
        Vector3f aux = baseParticle.getPosition();
        setPosition(aux.x, aux.y, aux.z);
        aux = baseParticle.getRotation();
        setRotation(aux.x, aux.y, aux.z);
        setScale(baseParticle.getScale());
        this.speed = new Vector3f(baseParticle.speed);
        this.ttl = baseParticle.geTtl();
        this.updateTextureMillis = baseParticle.getUpdateTextureMillis();
        this.currentAnimTimeMillis = 0;
        this.animFrames = baseParticle.getAnimFrames();
    }

    public int getAnimFrames() {
        return animFrames;
    }

    public Vector3f getSpeed() {
        return speed;
    }

    public long getUpdateTextureMillis() {
        return updateTextureMillis;
    }

    public void setSpeed(Vector3f speed) {
        this.speed = speed;
    }

    public long geTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public void setUpdateTextureMills(long updateTextureMillis) {
        this.updateTextureMillis = updateTextureMillis;
    }

    /**
     * Updates the Particle's TTL
     * @param elapsedTime Elapsed Time in milliseconds
     * @return The Particle's TTL
     */
    public long updateTtl(long elapsedTime) {
        this.ttl -= elapsedTime;
        this.currentAnimTimeMillis += elapsedTime;
        if ( this.currentAnimTimeMillis >= this.getUpdateTextureMillis() && this.animFrames > 0 ) {
            this.currentAnimTimeMillis = 0;
            int pos = this.getTextPos();
            pos++;
            if ( pos < this.animFrames ) {
                this.setTextPos(pos);
            } else {
                this.setTextPos(0);
            }
        }
        return this.ttl;
    }
}
