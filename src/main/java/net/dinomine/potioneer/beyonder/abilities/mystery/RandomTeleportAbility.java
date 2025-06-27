package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RandomTeleportAbility extends Ability {

    public RandomTeleportAbility(int sequence){
        this.info = new AbilityInfo(57, 56, "Random Teleport", 20 + sequence, 75, getCooldown(), "random_teleport");
        this.isActive = true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getSpirituality() < getInfo().cost()) return false;
        Level pLevel = target.level();
        pLevel.playSound(target, target.getOnPos().above(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1, 1);
        if(!pLevel.isClientSide()) {
            cap.requestActiveSpiritualityCost(info.cost());
            double d3 = target.getX() + (target.getRandom().nextDouble() - (double) 0.5F) * (double) 16.0F;
            double d4 = Mth.clamp(target.getY() + (double) (target.getRandom().nextInt(16)), (double) pLevel.getMinBuildHeight(), (double) (pLevel.getMinBuildHeight() + ((ServerLevel) pLevel).getLogicalHeight() - 1));
            double d5 = target.getZ() + (target.getRandom().nextDouble() - (double) 0.5F) * (double) 16.0F;
            target.fallDistance = 0;
            target.randomTeleport(d3, d4, d5, false);
        }
        return true;
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
