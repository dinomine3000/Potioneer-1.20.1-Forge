package net.dinomine.potioneer.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class PotionCauldronParticles extends TextureSheetParticle {
    protected PotionCauldronParticles(ClientLevel pLevel,
                                      double pX, double pY, double pZ,
                                      double v1, double v2, double v3,
                                      SpriteSet spriteSet) {
        super(pLevel, pX, pY, pZ);

        this.friction = 0.9f;
        this.quadSize *= 4.5f;
        this.lifetime = 70;
        this.setSpriteFromAge(spriteSet);

        this.xd = v1;
        this.yd = v2;
        this.zd = v3;

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;

    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut(){
        float x = (float) this.age /this.lifetime;
        float a = 0.14f;
        this.alpha = (x*x/12.25f - 7*x/12.25f + 1)/(x/a+1);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites){
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel,
                                                 double v, double v1, double v2,
                                                 double v3, double v4, double v5) {
            return new PotionCauldronParticles(clientLevel, v, v1, v2, v3, v4, v5, this.sprites);
        }
    }
}
