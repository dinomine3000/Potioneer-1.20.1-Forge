package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.DisabledAbilitiesManager;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class TyrantDisableAbilitiesPunishment extends BeyonderEffect {

    @Override
    public void refreshTime(BeyonderCapability cap, LivingEntity target, BeyonderEffect effect) {
        super.refreshTime(cap, target, effect);
        disable(cap, target);
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        if(fromLoading) return;
        disable(cap, target);
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
    }
    private void disable(BeyonderCapability cap, LivingEntity target){
        DisabledAbilitiesManager.DisabledAbilityProxy proxy = DisabledAbilitiesManager.DisabledAbilityProxy.all(maxLife - lifetime);
        cap.getAbilitiesManager().getDisabledAbilitiesManager().disableAbility("tyrant_punishment", proxy, cap, target);
    }
}
