package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class RainCreateAbility extends Ability {

    public RainCreateAbility(int sequence){
        this.info = new AbilityInfo(31, 200, "Summon Rain", 10 + sequence, 70, 20*10, "summon_rain");
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost() && !target.level().isRaining()){
            ((ServerLevel) target.level()).setWeatherParameters(0, 20*60*(1 + 2*(7-getSequence())), true, false);
            cap.requestActiveSpiritualityCost(info.cost());
            return true;
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
