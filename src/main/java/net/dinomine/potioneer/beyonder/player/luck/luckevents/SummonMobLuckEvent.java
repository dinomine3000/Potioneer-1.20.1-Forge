package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.level.Level;

public class SummonMobLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        int num = luck.getRandomNumber(1, 6, false, target.getRandom());
        for(int i = 0; i < num; i++){
            BlockPos pos = luck.getRandomBlockPos(target.getOnPos(), 6, false, false, target.getRandom());
            BlockPos airPos = findNearestThreeTallAir(target.level(), pos, 16);
            if(airPos == null) continue;
            Mob mob = getRandomMob(target.level());
            mob.setTarget(target);
            mob.setPos(airPos.getCenter());
            target.level().addFreshEntity(mob);
        }
    }

    private Mob getRandomMob(Level level){
        return switch (level.random.nextInt(10)){
            case 0 -> new EnderMan(EntityType.ENDERMAN, level);
            case 1 -> new Creeper(EntityType.CREEPER, level);
            case 2 -> new Zombie(EntityType.ZOMBIE, level);
            case 3 -> new Husk(EntityType.HUSK, level);
            case 4 -> new Silverfish(EntityType.SILVERFISH, level);
            case 5 -> new Hoglin(EntityType.HOGLIN, level);
            case 6 -> new Blaze(EntityType.BLAZE, level);
            case 7 -> new Slime(EntityType.SLIME, level);
            case 8 -> new Skeleton(EntityType.SKELETON, level);
            case 9 -> new ChryonEntity(ModEntities.CHRYON.get(), level);
            default -> new Ghast(EntityType.GHAST, level);
        };
    }
    public static BlockPos findNearestThreeTallAir(Level level, BlockPos origin, int radius) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int originY = origin.getY();
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight() - 2;

        for (int dy = 0; dy <= radius; dy++) {
            // check same Y, then up, then down
            for (int ySign : new int[]{1, -1}) {
                int y = originY + (dy * ySign);
                if (dy == 0 && ySign == -1) continue; // avoid duplicate Y=origin

                if (y < minY || y > maxY) continue;

                BlockPos bestAtY = null;
                double bestDist = Double.MAX_VALUE;

                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        int x = origin.getX() + dx;
                        int z = origin.getZ() + dz;

                        pos.set(x, y, z);

                        if (level.isEmptyBlock(pos)
                                && level.isEmptyBlock(pos.above())
                                && level.isEmptyBlock(pos.above(2))) {

                            double dist = pos.distSqr(new Vec3i(origin.getX(), y, origin.getZ()));
                            if (dist < bestDist) {
                                bestDist = dist;
                                bestAtY = pos.immutable();
                            }
                        }
                    }
                }

                if (bestAtY != null) {
                    return bestAtY;
                }
            }
        }

        return null;
    }
}
