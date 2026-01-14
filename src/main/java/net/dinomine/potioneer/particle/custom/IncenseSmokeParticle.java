package net.dinomine.potioneer.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class IncenseSmokeParticle extends TextureSheetParticle {
    private final float red;
    private final float green;
    private final float blue;

    public IncenseSmokeParticle(ClientLevel level, double x, double y, double z,
                                double xd, double yd, double zd, int color) {
        super(level, x, y, z, xd, yd, zd);

        this.lifetime = 60 + this.random.nextInt(20); // about 3 seconds
        this.gravity = -0.005f;
        this.xd = xd;
        this.yd = yd + 0.02; // gentle lift
        this.zd = zd;
        this.quadSize = 0.2f + random.nextFloat() * 0.1f;
        this.alpha = 0.9f;

        // Extract RGB from integer (ARGB or RGB)
        this.red   = ((color >> 16) & 0xFF) / 255.0f;
        this.green = ((color >> 8) & 0xFF) / 255.0f;
        this.blue  = (color & 0xFF) / 255.0f;
        // Extract RGB from integer (ARGB or RGB)
        rCol   = ((color >> 16) & 0xFF) / 255.0f;
        gCol = ((color >> 8) & 0xFF) / 255.0f;
        bCol  = (color & 0xFF) / 255.0f;
    }

    @Override
    public void tick() {
        super.tick();

        this.alpha = 0.9f * ((float)(lifetime - age) / lifetime); // fade out
        //this.setColor(red, green, blue);
        // Wobble effect
        this.xd += (random.nextFloat() - 0.5f) * 0.002;
        this.zd += (random.nextFloat() - 0.5f) * 0.002;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 240; // Full brightness
    }

    public static class IncenseSmokeParticleProvider implements ParticleProvider<IncenseSmokeParticleOptions> {
        private final SpriteSet spriteSet;

        public IncenseSmokeParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(IncenseSmokeParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            IncenseSmokeParticle particle = new IncenseSmokeParticle(level, x, y, z, xd, yd, zd, options.getColor());
            particle.pickSprite(spriteSet);
            return particle;
        }
    }

}

