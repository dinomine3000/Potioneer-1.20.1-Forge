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
        super(sequence, BeyonderEffects.WHEEL_PATIENCE, level -> "patience");
        canFlip();
    }

    @Override
    public boolean flipEnable(LivingEntityBeyonderCapability cap, LivingEntity target) {
        boolean enabled = super.flipEnable(cap, target);
        if(enabled){
            target.level().playSound(null, target.getOnPos(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1, (float)target.getRandom().triangle(1, 0.2));
            putOnCooldown(20*60, target);
        } else {
            target.level().playSound(null, target.getOnPos(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1, (float)target.getRandom().triangle(1, 0.2));
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

    @Override
    protected BeyonderEffect createEffectInstance(LivingEntityBeyonderCapability cap, LivingEntity target) {
        BeyonderPatienceEffect effect = (BeyonderPatienceEffect) BeyonderEffects.WHEEL_PATIENCE.createInstance(getSequenceLevel(), cost(), -1, true);
        effect.setLuckQuantity(cap.getLuckManager().getLuck());
        return effect;
    }
}
