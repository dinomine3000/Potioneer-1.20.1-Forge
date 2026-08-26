package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Function;

public class BlinkAbility extends PassiveAbility {
    private static final int cost = 20;
    public BlinkAbility() {
        super(BeyonderEffects.MYSTERY_AUTO_BLINK, ign -> "blink");
    }

    @Override
    public void init() {
        enabledOnAcquire();
        canFlip();
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity caster) {
        if (cap.getSpirituality() < cost) return false;
        if (caster.level().isClientSide()) return true;

        ServerLevel level = (ServerLevel) caster.level();
        int maxDistance = 10;
        Optional<LivingEntity> optTarget = AbilityFunctionHelper.getTargetEntityClosestToCrosshair(caster, maxDistance, 0, true);

        float xRot = caster.getXRot();
        float yRot = caster.getYRot();
        BlockPos toPos = caster.getOnPos();

        if (optTarget.isPresent()) {
            //teleport behind targeted entity
            LivingEntity target = optTarget.get();
            toPos = BlockPos.containing(target.position().add(caster.getLookAngle().normalize().scale(2))).atY(target.getBlockY());

        } else {
            BlockHitResult res = AbilityFunctionHelper.getBlockLooking(caster);
            toPos = res.getBlockPos().relative(res.getDirection());;
        }

        //cap.requestActiveSpiritualityCost(cost());
        BlockPos validPos = breadthFirstSearch(toPos, 2, level);
        if(validPos == null) return false;
        if(teleport(caster, level, validPos, xRot, yRot)){
            if(optTarget.isEmpty()) return true;
            caster.lookAt(EntityAnchorArgument.Anchor.FEET, optTarget.get().position());
            return true;
        }
        return false;
    }

    public static BlockPos breadthFirstSearch(BlockPos initialPos, int radiusToCheck, Level level) {
        if (DoorOpeningAbility.isValidBlockposToTeleportTo(initialPos, level)) {
            return initialPos;
        }

        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(initialPos);
        visited.add(initialPos);

        // Standard 6-cardinal directional offsets
        Direction[] directions = Direction.values();

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction dir : directions) {
                BlockPos neighbor = current.relative(dir);

                // Skip if already checked or exceeds search radius
                if (visited.contains(neighbor) || initialPos.distManhattan(neighbor) > radiusToCheck) {
                    continue;
                }

                if (DoorOpeningAbility.isValidBlockposToTeleportTo(neighbor, level)) {
                    return neighbor; // First valid destination found
                }

                visited.add(neighbor);
                queue.add(neighbor);
            }
        }

        return null;
    }
    public static boolean teleport(LivingEntity target, ServerLevel level, BlockPos toPos, float xRot, float yRot){
        PacketHandler.sendMessageToClientsAround(target, 16, new GeneralAreaEffectMessage(ParticleMaker.Preset.ENDERMAN, target.getEyePosition().toVector3f(), 0));
        level.playSound(null,
                toPos, ModSounds.BLINK.get(),
                SoundSource.PLAYERS, 1, 1);
        boolean flag = AbilityFunctionHelper.teleportEntity(target, level, level, toPos, xRot, yRot, true);
        if(flag){
            PacketHandler.sendMessageToClientsAround(target, 16, new GeneralAreaEffectMessage(ParticleMaker.Preset.ENDERMAN, target.getEyePosition().toVector3f(), 0));
            level.playSound(null,
                    toPos, ModSounds.BLINK.get(),
                    SoundSource.PLAYERS, 1, 1);
        }
        return flag;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        return super.primary(cap, target);
    }
}
