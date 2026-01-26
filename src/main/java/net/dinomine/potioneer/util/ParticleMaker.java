package net.dinomine.potioneer.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class ParticleMaker {

    public static void particleExplosionRandom(Level level, double radius, double x, double y, double z){
        particleExplosionRandom(ParticleTypes.END_ROD, 9/100f, level, radius, x, y, z);
    }

    public static void particleExplosionRandom(ParticleOptions particle, float scalar, Level level, double radius, double x, double y, double z){
        RandomSource random = level.getRandom();
        int particles = random.nextInt((int) (20*radius), (int) (40*radius));
        float speedScale = (float) (radius*scalar);
        for(int i = 0; i < particles; i++){
            level.addParticle(particle, x, y, z,
                    speedScale*(1 - 2*random.nextFloat()), speedScale*(1 - 2*random.nextFloat()), speedScale*(1 - 2*random.nextFloat()));
        }
    }

    public static void particleExplosionGrid(ParticleOptions particle, float scalar, Level level, double radius, double xPos, double yPos, double zPos){
        int n = 3;
        float speedScale = (float) (scalar*radius/n);
        for(int x = -n; x < n; x++){
            for(int y = -n; y < n; y++){
                for(int z = -n; z < n; z++){
                    level.addParticle(particle, xPos, yPos, zPos, x*speedScale, y*speedScale, z*speedScale);
                }
            }
        }
    }

    public static void particleExplosionGrid(Level level, double radius, double xPos, double yPos, double zPos){
        particleExplosionGrid(ParticleTypes.END_ROD, 9/100f, level, radius, xPos, yPos, zPos);
    }
}
