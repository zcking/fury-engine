package com.zcking.furyengine.engine.graph.particles;

import com.zcking.furyengine.engine.objects.GameObject;
import com.zcking.furyengine.engine.objects.Particle;

import java.util.List;

public interface IParticleEmitter {

    void cleanUp();

    Particle getBaseParticle();

    List<GameObject> getParticles();

}
