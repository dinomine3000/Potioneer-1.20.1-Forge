package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.PatienceEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class PatienceAbility extends PassiveAbility {

    public PatienceAbility(int sequence){
//        this.info = new AbilityInfo(5, 296, "Patience", sequence, 0, getMaxCooldown(), "gambling");
        super(sequence, BeyonderEffects.WHEEL_PATIENCE, level -> level < 7 ? "patience_2" : "patience_1");
        canFlip();
    }

    @Override
    public boolean flipEnable(BeyonderCapability cap, LivingEntity target) {
        boolean enabled = super.flipEnable(cap, target);
        if(enabled){
            putOnCooldown(20*60, target);
        }
        return enabled;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        PatienceEffect effect = (PatienceEffect) cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_PATIENCE.getEffectId(), getSequenceLevel());
        if(effect == null) return false;
        int luckToGain = effect.getProjectedLuck(cap);
        target.sendSystemMessage(Component.translatableWithFallback("ability.potioneer.patience_test", "You are set to receive %s luck.", luckToGain));
        return true;
    }
}
