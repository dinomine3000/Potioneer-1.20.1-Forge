package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PhasingEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            player.setForcedPose(Pose.STANDING);
        }
        if(target instanceof ServerPlayer player){
            if(player.connection == null) return;
        }
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 1, false, false, true));
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            if(!hasNearbySolidSurface(target.level(), player) && (target.getY() > -64 || target.getY() < -68)){
                cap.getAbilitiesManager().setAbilityEnabled(Abilities.PHASING.getAblId(), getSequenceLevel(), false, cap, target);
                return;
            }
            target.noPhysics = true;
            player.setNoGravity(true);
            player.setArrowCount(0);
            player.getAbilities().flying = true;
        }
        cap.requestPassiveSpiritualityCost(cost);

    }

    private static boolean hasNearbySolidSurface(Level level, Player player) {
        BlockPos base = player.blockPosition();

        for (Direction dir : Direction.values()) {
            BlockPos pos = base.relative(dir);
            BlockState state = level.getBlockState(pos);

            if (!state.isAir() && !state.getCollisionShape(level, pos).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            target.noPhysics = false;
            player.getAbilities().flying = player.getAbilities().mayfly;
            player.setNoGravity(false);
            player.setForcedPose(null);
        }
        target.removeEffect(MobEffects.GLOWING);
    }
}
