package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

import static net.minecraft.world.level.block.Block.dropResources;

public class WaterRemoveAbility extends Ability {
    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    @Override
    protected String getDescId(int sequenceLevel) {
        return "water_sponge";
    }

    public WaterRemoveAbility(int sequence){
//        this.info = new AbilityInfo(31, 128, "Remove Water", 10 + sequence, 5, 20*2, "water_sponge");
        super(sequence);
        setCost(ignored -> 5);
        defaultMaxCooldown = 20*2;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > cost()){
            ServerLevel level = (ServerLevel) target.level();
            HitResult block = target.pick(target.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5f, 0f, false);
            if(block instanceof BlockHitResult rayTrace){
                BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
                double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequenceLevel());
                if(removeWaterBreadthFirstSearch(level, targetPos, (int) radius)){
                    cap.requestActiveSpiritualityCost(cost());
                    return true;
                }
            }
        }
        return false;
    }


    //copied from SpongeBlock class
    private boolean removeWaterBreadthFirstSearch(Level pLevel, BlockPos pPos, int radius) {
        return BlockPos.breadthFirstTraversal(pPos, radius, 65, (position, consumer) -> {
            for(Direction direction : ALL_DIRECTIONS) {
                consumer.accept(position.relative(direction));
            }

        }, (positionToEmpty) -> {
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
            return true;
        }) > 1;
    }
}
