package net.dinomine.potioneer.util;

import com.lowdragmc.photon.Photon;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.effects.DiceEffectEntity;
import net.dinomine.potioneer.entities.custom.effects.SlotMachineEntity;
import net.dinomine.potioneer.entities.custom.effects.WaterBlockEffectEntity;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.AuraEffectMessage;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParticleMaker {

    /**
     * function that gets the perimeter blocks of all of your areas of jurisdiction and draws particles in the perimeter.
     * it doesnt draw particles if a perimeter block is contained in another area of jurisdiction
     * @param level
     * @param playerYLevel
     * @param areaCenters
     * @param aojRadius
     */
    public static void createAreaOfJurisdiction(Level level, int playerYLevel, List<BlockPos> areaCenters, List<Integer> aojRadius) {
        int defaultRadius = 16;
        Set<BlockPos> entirePerimeter = new HashSet<>();
        for(int i = 0; i < areaCenters.size(); i++){
            BlockPos center = areaCenters.get(i);
            int radius = aojRadius.size() > i ? aojRadius.get(i) : defaultRadius;
            entirePerimeter.addAll(getPerimeter(center, radius));
        }
        entirePerimeter.removeIf(perimeterPos -> AreaOfJurisdictionAbility.isPosInAOJ(perimeterPos, areaCenters, aojRadius, 1));
        for(BlockPos perimeterPos: entirePerimeter){
            Vec3 center = perimeterPos.getCenter();
            level.addParticle(ParticleTypes.END_ROD, true, center.x, playerYLevel, center.z, 0, 0.3, 0);
        }

    }

    public static List<BlockPos> getPerimeter(BlockPos center, int radius){
        List<BlockPos> res = new ArrayList<>(List.of(center.atY(0).offset(radius, 0, radius), center.atY(0).offset(-radius, 0, radius), center.atY(0).offset(-radius, 0, -radius), center.atY(0).offset(radius, 0, -radius)));
        for(int i = 0; i < 4; i++){
            int east = i%2 == 0 ? (i == 0 ? -1 : 1) : 0;
            int north = i%2 == 1 ? (i == 1 ? -1 : 1) : 0;
            for(int j = 1; j < 2*radius + 1; j++){
                BlockPos perimeterTest = res.get(i).offset(east*j, 0, north*j);
                if(res.contains(perimeterTest)) break;
                res.add(perimeterTest.atY(0));
            }
        }
        return res;
    }

    public static void createAuraParticles(Player enforcer, LivingEntity victim) {
        if(!(victim instanceof Player player)) return;
        if(!victim.level().isClientSide()) PacketHandler.sendMessageSTC(new AuraEffectMessage(enforcer.getUUID()), player);
        Level level = victim.level();
        RandomSource random = victim.getRandom();
        Vec3 position = enforcer.position();
        float speedScale = 0.1f;
        int particles = victim.getRandom().nextInt(1, 4);
        for(int i = 0; i < particles; i++){
            level.addParticle(ParticleTypes.POOF, position.x, position.y, position.z,
                    speedScale*(1 - 2*random.nextFloat()), speedScale*(1 - 2*random.nextFloat()), speedScale*(1 - 2*random.nextFloat()));
        }
    }

    public enum Preset{
        AOE_END_ROD,
        AOE_GRAVITY
    }

    public static void createWaterBlockEffectForPlayer(Player player, Level level){
        WaterBlockEffectEntity effect = new WaterBlockEffectEntity(ModEntities.WATER_BLOCK_EFFECT_ENTITY.get(), level);
        effect.setOffset(new Vector3f(0f, 1f, 0f));
        effect.setTarget(player.getUUID());
        level.addFreshEntity(effect);
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
