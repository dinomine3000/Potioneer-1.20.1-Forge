package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.WaterPrisonEffectSTC;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;

import static net.minecraft.world.level.block.Block.dropResources;

public class WaterRemoveAbility extends Ability {
    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    public WaterRemoveAbility(int sequence){
        this.info = new AbilityInfo(31, 128, "Remove Water", sequence, 10, 20*2, "water_sponge");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost()){
            ServerLevel level = (ServerLevel) target.level();
            HitResult block = target.pick(target.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5, 0f, false);
            if(block instanceof BlockHitResult rayTrace){
                BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
                double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequence());
                if(removeWaterBreadthFirstSearch(level, targetPos, (int) radius)){
                    cap.requestActiveSpiritualityCost(info.cost());
                    return true;
                }
            }
        }
        return false;
    }


    //copied from SpongeBlock class
    private boolean removeWaterBreadthFirstSearch(Level pLevel, BlockPos pPos, int radius) {
        BlockState spongeState = pLevel.getBlockState(pPos);
        return BlockPos.breadthFirstTraversal(pPos, radius, 65, (position, consumer) -> {
            for(Direction direction : ALL_DIRECTIONS) {
                consumer.accept(position.relative(direction));
            }

        }, (positionToEmpty) -> {
            if (!positionToEmpty.equals(pPos)) {
                BlockState blockstate = pLevel.getBlockState(positionToEmpty);

                Block block = blockstate.getBlock();
                if (block instanceof BucketPickup bucketpickup) {
                    if (!bucketpickup.pickupBlock(pLevel, positionToEmpty, blockstate).isEmpty()) {
                        return true;
                    }
                }

                if (blockstate.getBlock() instanceof LiquidBlock) {
                    pLevel.setBlock(positionToEmpty, Blocks.AIR.defaultBlockState(), 3);
                } else {
                    if (!blockstate.is(Blocks.KELP) && !blockstate.is(Blocks.KELP_PLANT) && !blockstate.is(Blocks.SEAGRASS) && !blockstate.is(Blocks.TALL_SEAGRASS)) {
                        return false;
                    }

                    BlockEntity blockentity = blockstate.hasBlockEntity() ? pLevel.getBlockEntity(positionToEmpty) : null;
                    dropResources(blockstate, pLevel, positionToEmpty, blockentity);
                    pLevel.setBlock(positionToEmpty, Blocks.AIR.defaultBlockState(), 3);
                }

            }
            return true;
        }) > 1;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
    }
}
