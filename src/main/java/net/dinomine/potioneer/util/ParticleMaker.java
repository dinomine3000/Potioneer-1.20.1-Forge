package net.dinomine.potioneer.util;

import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.DiceEffectEntity;
import net.dinomine.potioneer.entities.custom.SlotMachineEntity;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleMaker {

    public enum Preset{
        AOE_END_ROD,
        AOE_GRAVITY
    }

    public static void createSlotMachineForEntity(Level level, LivingEntity target, boolean success){
        SlotMachineEntity slotMachine = new SlotMachineEntity(ModEntities.SLOT_MACHINE_ENTITY.get(), level);
        slotMachine.setInvulnerable(true);
        slotMachine.setTarget(target.getUUID());
        slotMachine.setSuccess(success);
        level.addFreshEntity(slotMachine);
    }

    public static void createDiceEffectForEntity(Level level, LivingEntity target){
        DiceEffectEntity dice = new DiceEffectEntity(ModEntities.DICE_EFFECT_ENTITY.get(), level);
        dice.setInvulnerable(true);
        dice.setTarget(target.getUUID());
        level.addFreshEntity(dice);
    }

    public static void summonAOEParticles(Level level, Vec3 center, int messageRadius, double effectRadius, Preset preset){
        switch (preset){
            case AOE_END_ROD -> PacketHandler.sendMessageToClientsAround(BlockPos.containing(center), level, messageRadius, new GeneralAreaEffectMessage(Preset.AOE_END_ROD, center.toVector3f(), effectRadius));
            case AOE_GRAVITY -> PacketHandler.sendMessageToClientsAround(BlockPos.containing(center), level, messageRadius, new GeneralAreaEffectMessage(Preset.AOE_GRAVITY, center.toVector3f(), effectRadius));
        }
    }

    public static void fallingGlow(Level level, Vec3 eyePos, double radius){
        RandomSource randomSource = level.random;
        for(int i = 0; i < randomSource.nextInt(15, 30); i++){
            Vec3 diff = new Vec3(randomSource.triangle(0, 1), 0, randomSource.triangle(0, 1));
            Vec3 pPos = eyePos.add(diff.normalize().scale(radius));
            level.addParticle(ParticleTypes.FIREWORK, pPos.x, pPos.y, pPos.z, 0, -0.2, 0);
        }
    }

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
