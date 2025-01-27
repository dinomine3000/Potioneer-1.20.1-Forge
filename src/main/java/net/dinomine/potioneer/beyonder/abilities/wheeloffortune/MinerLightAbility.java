package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import com.eliotlash.mclib.utils.MathHelper;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.block.ModBlocks;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class MinerLightAbility extends Ability {

    public MinerLightAbility(int sequence){
        this.info = new AbilityInfo(5, 56, "Miner Light", sequence, 0, this.getCooldown());
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > 2){
            HitResult block = target.pick(target.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5, 0f, false);
            if(block instanceof BlockHitResult rayTrace){
                Level level = target.level();
                BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
                if(!level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                    && level.getBlockState(targetPos).canBeReplaced()){

                    level.setBlockAndUpdate(targetPos,
                            ModBlocks.MINER_LIGHT.get().defaultBlockState());
                    cap.requestActiveSpiritualityCost(2);
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
