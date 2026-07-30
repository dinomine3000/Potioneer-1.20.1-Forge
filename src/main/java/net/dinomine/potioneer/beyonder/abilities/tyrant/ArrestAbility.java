package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
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
    public void onUpgrade(int oldLevel, int newLevel, LivingEntityBeyonderCapability cap, LivingEntity target) {
        canFlip(newLevel < 7);
        if(newLevel > 6) setEnabled(cap, target, true);
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(getSequenceLevel() >= 7) {
            if(target.level().isClientSide())
                target.sendSystemMessage(Component.translatableWithFallback("message.potioneer.outdated_secondary", "It doesn't do anything... yet"));
            return false;
        }
        return true;
    }
}
