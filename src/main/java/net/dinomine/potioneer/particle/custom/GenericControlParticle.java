package net.dinomine.potioneer.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import org.joml.Vector4f;

public class GenericControlParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected GenericControlParticle(
            ClientLevel level, double x, double y, double z,
            double vx, double vy, double vz,
            GenericParticleOptions options, SpriteSet sprites
    ) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;

        // Apply explicit velocity passed from server
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;

        // Apply visual and physics options
        Vector4f col = options.getColor();
        this.setColor(col.x(), col.y(), col.z());
        this.setAlpha(col.w());

        this.quadSize *= options.getScale();
        this.friction = options.getFriction();
        this.gravity = options.getGravity();
        this.lifetime = options.getLifetime();

        this.setSpriteFromAge(sprites);
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

    public static class Provider implements ParticleProvider<GenericParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                GenericParticleOptions options, ClientLevel level,
                double x, double y, double z,
                double vx, double vy, double vz
        ) {
            return new GenericControlParticle(level, x, y, z, vx, vy, vz, options, this.sprites);
        }
    }
}