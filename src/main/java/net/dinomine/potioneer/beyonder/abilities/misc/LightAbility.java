package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
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

import java.util.function.Function;

import static net.dinomine.potioneer.block.custom.MinerLightSourceBlock.WATERLOGGED;

public abstract class LightAbility extends Ability {
    private final BlockState lightBlockState;
    public LightAbility(int sequence, BlockState lightBlock, Function<Integer, Integer> cost){
//        this.info = new AbilityInfo(5, 56, "Light", sequence, 0, this.getMaxCooldown(), "");
        super(sequence);
        lightBlockState = lightBlock;
        if(cost != null) setCost(cost);
        this.defaultMaxCooldown = 20;
    }
    public LightAbility(int sequence, BlockState lightBlock){
        this(sequence, lightBlock, null);
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > cost()){
            HitResult block = target.pick(target.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5, 0f, false);
            if(block instanceof BlockHitResult rayTrace){
                Level level = target.level();
                BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
                if(!level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                        && level.getBlockState(rayTrace.getBlockPos()).canBeReplaced()){

                    boolean water = level.getFluidState(rayTrace.getBlockPos()).getType() == Fluids.WATER;

                    level.setBlockAndUpdate(rayTrace.getBlockPos(),
                            lightBlockState.setValue(WATERLOGGED, water));
                    cap.requestActiveSpiritualityCost(cost());
                    return true;

                }
                if(!level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                    && level.getBlockState(targetPos).canBeReplaced()){

                    boolean water = level.getFluidState(targetPos).getType() == Fluids.WATER;

                    level.setBlockAndUpdate(targetPos,
                            lightBlockState.setValue(WATERLOGGED, water));
                    cap.requestActiveSpiritualityCost(cost());
                    return true;
                }
            }
        }
        return false;
    }
}
