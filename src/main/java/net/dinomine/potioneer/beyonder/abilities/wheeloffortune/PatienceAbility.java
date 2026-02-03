package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderPatienceEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class PatienceAbility extends PassiveAbility {

    public PatienceAbility(int sequence){
//        this.info = new AbilityInfo(5, 296, "Patience", sequence, 0, getMaxCooldown(), "gambling");
        super(sequence, BeyonderEffects.WHEEL_PATIENCE, level -> level < 7 ? "patience_2" : "patience");
        canFlip();
    }

    @Override
    public boolean flipEnable(LivingEntityBeyonderCapability cap, LivingEntity target) {
        boolean enabled = super.flipEnable(cap, target);
        if(enabled){
            putOnCooldown(20*60, target);
        }
        return enabled;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        BeyonderPatienceEffect effect = (BeyonderPatienceEffect) cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_PATIENCE.getEffectId(), getSequenceLevel());
        if(effect == null) return false;
        int luckToGain = effect.getProjectedLuck(cap);
        target.sendSystemMessage(Component.translatableWithFallback("ability.potioneer.patience_test", "You are set to receive %s luck.", luckToGain));
        return true;
    }
}
