package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.mystery.BlinkAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class VoidEffect extends BeyonderEffect {

    private BlockPos safePos = null;
    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide) return;
        if(target.onGround()) safePos = target.getOnPos();
        else if(target.tickCount%5 == 0 && target.level() instanceof ServerLevel level && target.position().y < -200)
            BlinkAbility.teleport(target, level, BlinkAbility.breadthFirstSearch(safePos, 5, level), target.getXRot(), target.getYRot());
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }
}
