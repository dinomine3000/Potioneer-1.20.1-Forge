package net.dinomine.potioneer.worldgen.portal;

import net.dinomine.potioneer.beyonder.abilities.mystery.BlinkAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class ModTeleporter implements ITeleporter {
    private final Vec3 targetPos;
    private final boolean goingToSpiritWorld;

    public ModTeleporter(Vec3 pos, boolean insideDim) {
        this.targetPos = pos;
        this.goingToSpiritWorld = insideDim;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destinationWorld,
                              float yaw, Function<Boolean, Entity> repositionEntity) {
            Entity teleportedEntity = repositionEntity.apply(false);

            // Keep double precision for coordinate scaling
            double rawX = (goingToSpiritWorld ? targetPos.x() / 32.0 : targetPos.x() * 32.0);
            double rawZ = (goingToSpiritWorld ? targetPos.z() / 32.0 : targetPos.z() * 32.0);

            BlockPos basePos = BlockPos.containing(rawX, 0, rawZ);
            double targetX = basePos.getX() + 0.5;
            double targetZ = basePos.getZ() + 0.5;
            double targetY;

            if (goingToSpiritWorld) {
                // Place entity directly at the fluid surface (Y = 90)
                targetY = 92.0;
            } else {
                // Find highest solid surface in the target world
                BlockPos topPos = destinationWorld.getHeightmapPos(
                        net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        basePos
                );
                targetY = topPos.getY() + 2;
            }

            BlockPos workingPos = BlinkAbility.breadthFirstSearch(new BlockPos((int) targetX, (int) targetY, (int) targetZ), 5, destinationWorld);

            if (teleportedEntity instanceof ServerPlayer player) {
                player.teleportTo(destinationWorld, workingPos.getX(), workingPos.getY(), workingPos.getZ(), yaw, player.getXRot());
            } else {
                teleportedEntity.moveTo(workingPos.getX(), workingPos.getY(), workingPos.getZ(), yaw, teleportedEntity.getXRot());
            }

            return teleportedEntity;
    }
}
