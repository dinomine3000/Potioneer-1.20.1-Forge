package net.dinomine.potioneer.particle.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class IncenseSmokeParticleType extends ParticleType<IncenseSmokeParticleOptions> {
    public IncenseSmokeParticleType() {
        super(false, IncenseSmokeParticleOptions.DESERIALIZER);
    }

    @Override
    public Codec<IncenseSmokeParticleOptions> codec() {
        return IncenseSmokeParticleOptions.CODEC;
    }
}

