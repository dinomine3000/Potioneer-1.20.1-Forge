package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.entity.WaterTrapBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

import static net.dinomine.potioneer.block.custom.MinerLightSourceBlock.WATERLOGGED;

public class WaterTrapAbility extends Ability {

    public WaterTrapAbility(int sequence){
        this.info = new AbilityInfo(31, 80, "Water Trap", sequence, 60, 20*10, "water_trap");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(!(target instanceof Player player)) return false;
        if(cap.getSpirituality() > info.cost()){
            HitResult block = player.pick(player.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5, 0f, false);
            if(block instanceof BlockHitResult rayTrace){
                Level level = player.level();
                BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
                if(!level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                        && level.getBlockState(targetPos).canBeReplaced()
                        && level.getBlockState(targetPos.below()).isCollisionShapeFullBlock(level, targetPos))
                {
                    level.setBlockAndUpdate(targetPos,
                            ModBlocks.WATER_TRAP_BLOCK.get().defaultBlockState());
                    WaterTrapBlockEntity be = (WaterTrapBlockEntity) level.getBlockEntity(targetPos);
                    if(be != null) be.setPlacedByPlayer(player.getUUID(), getSequence());
                    cap.requestActiveSpiritualityCost(info.cost());
                    return true;
                }
            }
        }
        return false;
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
