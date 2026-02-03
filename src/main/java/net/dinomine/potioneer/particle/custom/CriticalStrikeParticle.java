package net.dinomine.potioneer.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class CriticalStrikeParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected CriticalStrikeParticle(
            ClientLevel level,
            double x, double y, double z,
            double dx, double dy, double dz,
            SpriteSet sprites
    ) {
        super(level, x, y, z, dx, dy, dz);
        this.lifetime = 10;
        this.gravity = 0.5F;
        this.scale(3);
        this.setSpriteFromAge(sprites);
        this.sprites = sprites;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double dx, double dy, double dz
        ) {
            return new CriticalStrikeParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
