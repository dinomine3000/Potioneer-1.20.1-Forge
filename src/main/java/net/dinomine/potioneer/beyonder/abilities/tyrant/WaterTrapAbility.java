package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.entity.WaterTrapBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import static net.dinomine.potioneer.block.custom.MinerLightSourceBlock.WATERLOGGED;

public class WaterTrapAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "water_trap";
    }

    public WaterTrapAbility(int sequence){
//        this.info = new AbilityInfo(31, 80, "Water Trap", 10 + sequence, 40+40*(9-sequence), 20*10, "water_trap");
        super(sequence);
        defaultMaxCooldown = 20*10;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(!(target instanceof Player player)) return false;
        HitResult block = player.pick(player.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5, 0f, false);
        Level level = player.level();
        if(block instanceof BlockHitResult rayTrace){
            BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());

            //if the block youre targeting is a water trap and its yours
            if(level.getBlockState(rayTrace.getBlockPos()).is(ModBlocks.WATER_TRAP_BLOCK.get())){
                BlockEntity be = level.getBlockEntity(rayTrace.getBlockPos());
                if(be instanceof WaterTrapBlockEntity waterBe && waterBe.isOwner(player.getUUID())){
                    waterBe.markForAbsorption();
                    level.destroyBlock(rayTrace.getBlockPos(), false, target);
                    cap.requestActiveSpiritualityCost(-cost()/2f);
                    return true;
                }
            }

            //otherwise, if the block you are targeting can be replaced
            else if(cap.getSpirituality() > cost()
                    && !level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                    && !level.getBlockState(rayTrace.getBlockPos()).is(Blocks.WATER)
                    && level.getBlockState(rayTrace.getBlockPos()).canBeReplaced()){
                placeBlock(level, rayTrace.getBlockPos(), cap, cost(), player);
                return true;

            }
            //otherwise, if the block on the side you are targeting can be replaced
            else if(cap.getSpirituality() > cost()
                    && !level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                    && level.getBlockState(targetPos).canBeReplaced())
            {
                placeBlock(level, targetPos, cap, cost(), player);
                return true;
            }
        }

        //by this point, normal placement failed, so we use a custom algorithm
        Vec3 eyePos = target.getEyePosition();
        BlockPos headPos = BlockPos.containing(eyePos);
        Vec3 lookDir = target.getLookAngle().normalize();
        float jump = 0.05f;
        BlockPos found = null;
        while(found == null){
            eyePos = eyePos.add(lookDir.scale(jump));
            BlockPos testPos = BlockPos.containing(eyePos);
            if(Math.abs(testPos.getX() - headPos.getX()) <= 1 &&
                    Math.abs(testPos.getY() - headPos.getY()) <= 1 &&
                    Math.abs(testPos.getZ() - headPos.getZ()) <= 1) continue;
            found = testPos;
        }

        placeBlock(level, found, cap, cost(), player);
        return false;
    }

    private static void placeBlock(Level level, BlockPos positionToPlace, LivingEntityBeyonderCapability cap, int cost, Player player){
        boolean water = level.getFluidState(positionToPlace).getType() == Fluids.WATER;
        level.setBlockAndUpdate(positionToPlace,
                ModBlocks.WATER_TRAP_BLOCK.get().defaultBlockState().setValue(WATERLOGGED, water));
        WaterTrapBlockEntity be = (WaterTrapBlockEntity) level.getBlockEntity(positionToPlace);
        if(be != null) be.setPlacedByPlayer(player.getUUID(), cap.getSequenceLevel());
        cap.requestActiveSpiritualityCost(cost);
    }
}
