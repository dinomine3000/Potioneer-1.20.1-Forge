package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class RandomTeleportAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "random_teleport";
    }

    public RandomTeleportAbility(int sequence){
//        this.info = new AbilityInfo(57, 56, "Random Teleport", 20 + sequence, 75, getMaxCooldown(), "random_teleport");
        super(sequence);
        setCost(ignored -> 75);
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost()) return false;
        Level pLevel = target.level();
        pLevel.playSound(target, target.getOnPos().above(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1, 1);
        if(!pLevel.isClientSide()) {
            cap.requestActiveSpiritualityCost(cost());
            double d3 = target.getX() + (target.getRandom().nextDouble() - (double) 0.5F) * (double) 16.0F;
            double d4 = Mth.clamp(target.getY() + (double) (target.getRandom().nextInt(16)), (double) pLevel.getMinBuildHeight(), (double) (pLevel.getMinBuildHeight() + ((ServerLevel) pLevel).getLogicalHeight() - 1));
            double d5 = target.getZ() + (target.getRandom().nextDouble() - (double) 0.5F) * (double) 16.0F;
            target.fallDistance = 0;
            target.randomTeleport(d3, d4, d5, false);
        }
        return true;
    }
}
