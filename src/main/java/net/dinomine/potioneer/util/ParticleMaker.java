package net.dinomine.potioneer.util;

import com.lowdragmc.photon.Photon;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.effects.DiceEffectEntity;
import net.dinomine.potioneer.entities.custom.effects.SlotMachineEntity;
import net.dinomine.potioneer.entities.custom.effects.WaterBlockEffectEntity;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.AuraEffectMessage;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.network.messages.effects.PhotonFxMessage;
import net.dinomine.potioneer.particle.custom.GenericParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

public class ParticleMaker {

    private static Set<BlockPos> aojCache = new HashSet<>();
    private static int aojHash = 0;

    /**
     * function that gets the perimeter blocks of all of your areas of jurisdiction and draws particles in the perimeter.
     * it doesnt draw particles if a perimeter block is contained in another area of jurisdiction
     * @param level
     * @param playerYLevel
     * @param areaCenters
     * @param sideLengths
     */
    public static void createAreaOfJurisdiction(Level level, double playerYLevel, List<BlockPos> areaCenters, List<Integer> sideLengths) {
        // MUST run on client side only (Level#isClientSide returns true on Client)
        if (!level.isClientSide()) return;
        if (areaCenters.isEmpty()) return;

        int inHash = Objects.hash(areaCenters, sideLengths);
        if (inHash != aojHash) {
            aojHash = inHash;
            aojCache = computeOuterPerimeter(areaCenters, sideLengths);
        }

        // Render cached perimeter
        for (BlockPos perimeterPos : aojCache) {
            level.addParticle(
                    ParticleTypes.END_ROD,
                    true,
                    perimeterPos.getX() + 0.5,
                    playerYLevel,
                    perimeterPos.getZ() + 0.5,
                    0, 0.3, 0
            );
        }
    }


    private static void addInnerCorners(Set<BlockPos> perimeter, List<BlockPos> centers, List<Integer> sideLengths){
        Set<BlockPos> innerCorners = new HashSet<>();
        for(BlockPos pos1: perimeter){
            for(BlockPos pos2: perimeter){
                if(Math.abs(pos1.getX() - pos2.getX()) != 1 || Math.abs(pos1.getZ() - pos2.getZ()) != 1) continue;
                BlockPos corner1 = new BlockPos(pos1.getX(), 0, pos2.getZ());
                BlockPos corner2 = new BlockPos(pos2.getX(), 0, pos1.getZ());
                if(perimeter.contains(corner1)) continue;
                if(perimeter.contains(corner2)) continue;

                BlockPos diagonal1 = new BlockPos(pos2.getX(), 0, pos1.getZ());
                BlockPos diagonal2 = new BlockPos(pos1.getX(), 0, pos2.getZ());
                if(AreaOfJurisdictionAbility.isPosInAOJ(diagonal1, centers, sideLengths)) innerCorners.add(diagonal1);
                else innerCorners.add(diagonal2);
            }
        }
        perimeter.addAll(innerCorners);

    }

    private static Set<BlockPos> computeOuterPerimeter(List<BlockPos> areaCenters, List<Integer> sideLengths) {
        int defaultRadius = 32;
        Set<BlockPos> rawPerimeter = new HashSet<>();

        // 1. Generate base perimeters for all zones
        for (int i = 0; i < areaCenters.size(); i++) {
            BlockPos center = areaCenters.get(i);
            int length = (sideLengths.size() > i) ? sideLengths.get(i) : defaultRadius;
            addSquarePerimeter(rawPerimeter, center, length);
        }

        // 2. Filter out positions strictly inside the combined AOJ region
        Set<BlockPos> outerPerimeter = new HashSet<>();
        for (BlockPos pos : rawPerimeter) {
            // Keep block if it is on the outer boundary (adjacent to at least one position outside AOJ)
            if (isBoundaryPos(pos, areaCenters, sideLengths)) {
                outerPerimeter.add(pos);
            }
        }
        //add inner corners
        addInnerCorners(outerPerimeter, areaCenters, sideLengths);

        return outerPerimeter;
    }

    /**
     * Efficiently adds a square perimeter at Y=0 to the destination set.
     */
    private static void addSquarePerimeter(Set<BlockPos> dest, BlockPos center, int sideLength) {
        int minX, maxX, minZ, maxZ;

        if (sideLength % 2 == 0) {
            int radius = sideLength / 2;
            minX = center.getX() - radius;
            maxX = center.getX() + radius - 1;
            minZ = center.getZ() - radius;
            maxZ = center.getZ() + radius - 1;
        } else {
            int radius = (sideLength - 1) / 2;
            minX = center.getX() - radius;
            maxX = center.getX() + radius;
            minZ = center.getZ() - radius;
            maxZ = center.getZ() + radius;
        }

        for (int x = minX; x <= maxX; x++) {
            dest.add(new BlockPos(x, 0, minZ));
            dest.add(new BlockPos(x, 0, maxZ));
        }
        for (int z = minZ + 1; z < maxZ; z++) {
            dest.add(new BlockPos(minX, 0, z));
            dest.add(new BlockPos(maxX, 0, z));
        }
    }

    /**
     * A position is part of the visual perimeter if it is INSIDE the AOJ,
     * but has at least one direct cardinal/diagonal neighbor OUTSIDE the AOJ.
     */
    private static boolean isBoundaryPos(BlockPos pos, List<BlockPos> centers, List<Integer> sideLengths) {
        if (!AreaOfJurisdictionAbility.isPosInAOJ(pos, centers, sideLengths)) {
            return false;
        }

        // Check 4 cardinal neighbors
        return !AreaOfJurisdictionAbility.isPosInAOJ(pos.west(), centers, sideLengths)  ||
                !AreaOfJurisdictionAbility.isPosInAOJ(pos.east(), centers, sideLengths)  ||
                !AreaOfJurisdictionAbility.isPosInAOJ(pos.north(), centers, sideLengths) ||
                !AreaOfJurisdictionAbility.isPosInAOJ(pos.south(), centers, sideLengths);
    }

    public static void clearCache() {
        aojCache = Collections.emptySet();
        aojHash = 0;
    }
    public static void createAuraParticles(LivingEntity enforcer, LivingEntity victim) {
        if(!(victim instanceof Player)) return;
        if(!victim.level().isClientSide()) return;
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

    public static void summonMistParticles(LivingEntity pLivingEntity) {
        if(!pLivingEntity.level().isClientSide()) return;
        Level level = pLivingEntity.level();
        RandomSource random = pLivingEntity.getRandom();
        for(int i = 0; i < random.nextInt(5, 15); i++){
            Vec3 pos = pLivingEntity.getEyePosition().offsetRandom(random, 1);
            level.addParticle(ParticleTypes.FALLING_WATER, pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }

    public enum Preset{
        AOE_END_ROD,
        AOE_GRAVITY,
        SMALL_MIST,
        WATER_TRAP,
        WATER_IMPLOSION,
        WATER_JET
    }

    public static void waterMist(Level level, Vec3 centerPos, int radius){
        RandomSource random = RandomSource.create();
        for(int i = 0; i < random.nextInt(10, 30); i++){
            Vec3 pos = centerPos.offsetRandom(random, radius);
            level.addParticle(ParticleTypes.FALLING_WATER, pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }
    public static void createWaterJet(int targetId, Level level){createWaterJet(level.getEntity(targetId));}
    public static void createWaterJet(Entity target){
        if(!target.level().isClientSide()){
            PacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> target.level().dimension()),
                    new PhotonFxMessage(Preset.WATER_JET, target));
            return;
        }
        FX fx = FXHelper.getFX(new ResourceLocation(Potioneer.MOD_ID, "water_jet_stream_weak"));
        EntityEffect jetEffect = new EntityEffect(fx, target.level(), target, EntityEffect.AutoRotate.LOOK);
        jetEffect.setOffset(0, 1, 0);
        jetEffect.setRotation(0, 90, 0);
        jetEffect.start();
    }

    public static void createWaterBlockEffectForPlayer(LivingEntity target, Level level, int duration){
        WaterBlockEffectEntity effect = new WaterBlockEffectEntity(ModEntities.WATER_BLOCK_EFFECT_ENTITY.get(), level);
        effect.setOffset(new Vector3f(-0.5f, target.getEyeHeight() - 0.5f, -0.5f));
        effect.setTarget(target);
        effect.setDuration(duration);
        level.addFreshEntity(effect);
    }

    public static void createSlotMachineForEntity(Level level, LivingEntity target, boolean success){
        SlotMachineEntity slotMachine = new SlotMachineEntity(ModEntities.SLOT_MACHINE_ENTITY.get(), level);
        slotMachine.setTarget(target);
        slotMachine.setSuccess(success);
        level.addFreshEntity(slotMachine);
    }

    public static void createDiceEffectForEntity(Level level, LivingEntity target){
        DiceEffectEntity dice = new DiceEffectEntity(ModEntities.DICE_EFFECT_ENTITY.get(), level);
        dice.setTarget(target);
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

    public static void implosion(ParticleOptions particle, float scalar, Level level, double radius, Vector3f center) {
        RandomSource random = level.getRandom();
        int particles = random.nextInt((int) (20 * radius), (int) (40 * radius));
        float speedScale = (float) (radius * scalar);

        for (int i = 0; i < particles; i++) {
            // Generate a random point on a sphere surface using normalized directional vectors
            float dirX = 1.0f - 2.0f * random.nextFloat();
            float dirY = 1.0f - 2.0f * random.nextFloat();
            float dirZ = 1.0f - 2.0f * random.nextFloat();

            Vector3f dir = new Vector3f(dirX, dirY, dirZ);
            if (dir.lengthSquared() == 0) {
                dir.set(0, 1, 0); // Guard against zero vector division
            }
            dir.normalize();

            // Spawn position: Outer edge of the sphere (Center + Direction * Radius)
            double spawnX = center.x + dir.x() * radius;
            double spawnY = center.y + dir.y() * radius;
            double spawnZ = center.z + dir.z() * radius;

            // Inward velocity vector pointing back to (x, y, z)
            double vx = -dir.x() * speedScale;
            double vy = -dir.y() * speedScale;
            double vz = -dir.z() * speedScale;

            level.addParticle(particle, spawnX, spawnY, spawnZ, vx, vy, vz);
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
