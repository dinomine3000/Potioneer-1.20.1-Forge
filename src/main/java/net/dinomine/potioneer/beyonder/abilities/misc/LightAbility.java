package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

import static net.dinomine.potioneer.block.custom.MinerLightSourceBlock.WATERLOGGED;

public class LightAbility extends Ability {
    private BlockState lightBlockState;
    public LightAbility(int sequence, BlockState lightBlock){
        this.info = new AbilityInfo(5, 56, "Light", sequence, 0, this.getMaxCooldown(), "");
        lightBlockState = lightBlock;
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost()){
            HitResult block = target.pick(target.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5, 0f, false);
            if(block instanceof BlockHitResult rayTrace){
                Level level = target.level();
                BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
                if(!level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                        && level.getBlockState(rayTrace.getBlockPos()).canBeReplaced()){

                    boolean water = level.getFluidState(rayTrace.getBlockPos()).getType() == Fluids.WATER;

                    level.setBlockAndUpdate(rayTrace.getBlockPos(),
                            lightBlockState.setValue(WATERLOGGED, water));
                    cap.requestActiveSpiritualityCost(info.cost());
                    return true;

                }
                if(!level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                    && level.getBlockState(targetPos).canBeReplaced()){

                    boolean water = level.getFluidState(targetPos).getType() == Fluids.WATER;

                    level.setBlockAndUpdate(targetPos,
                            lightBlockState.setValue(WATERLOGGED, water));
                    cap.requestActiveSpiritualityCost(info.cost());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }
}
