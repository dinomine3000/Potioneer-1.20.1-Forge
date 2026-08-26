package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.worldgen.dimension.ModDimensions;
import net.dinomine.potioneer.worldgen.portal.ModTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BlankSpiritWorldAbility extends Ability {
    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "spirit_world";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        handlePortal(target, target.position());
        return true;
    }

    private void handlePortal(Entity player, Vec3 pPos) {
        if (player.level() instanceof ServerLevel serverlevel) {
            MinecraftServer minecraftserver = serverlevel.getServer();
            ResourceKey<Level> resourcekey = player.level().dimension() == ModDimensions.SPIR_WORLD_LEVEL_KEY ?
                    Level.OVERWORLD : ModDimensions.SPIR_WORLD_LEVEL_KEY;

            ServerLevel portalDimension = minecraftserver.getLevel(resourcekey);
            if (portalDimension != null && !player.isPassenger()) {
                player.changeDimension(portalDimension, new ModTeleporter(pPos,
                        resourcekey == ModDimensions.SPIR_WORLD_LEVEL_KEY));
            }
        }
    }
}
