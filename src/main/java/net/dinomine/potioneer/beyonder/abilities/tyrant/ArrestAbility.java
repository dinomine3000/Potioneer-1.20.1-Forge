package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AuraRecipientEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class ArrestAbility extends PassiveAbility {
    public ArrestAbility(int sequenceLevel) {
        super(sequenceLevel, BeyonderEffects.TYRANT_ARREST_SOURCE, ignored -> "arrest");
        enabledOnAcquire();
        canFlip(sequenceLevel < 7);
    }

    @Override
    public void upgradeToLevel(int level, LivingEntityBeyonderCapability cap, LivingEntity target) {
        super.upgradeToLevel(level, cap, target);
        canFlip(level < 7);
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        AuraRecipientEffect eff = (AuraRecipientEffect) BeyonderEffects.TYRANT_AURA_RECIPIENT.createInstance(getSequenceLevel(), 0, -1, true);
        eff.addSourceSilent(target.getUUID());
        cap.getEffectsManager().addOrReplaceEffect(eff, cap, target);
        if(getSequenceLevel() >= 8) {
            if(target.level().isClientSide())
                target.sendSystemMessage(Component.translatableWithFallback("message.potioneer.outdated_secondary", "It doesn't do anything... yet"));
            return false;
        }
        return true;
    }
}
