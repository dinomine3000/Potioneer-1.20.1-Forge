package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class LuckBoostAbility extends Ability {

    public LuckBoostAbility(int sequence){
//        this.info = new AbilityInfo(5, 128, "Luck Boost", sequence, 30 + 10*(9-sequence), 20*60, "luck_boost");
        super(sequence);
        setCost(level -> 30 + 10*(9-level));
        defaultMaxCooldown = 20*60;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "luck_boost";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= cost()) return true;

        if(cap.getSpirituality() >= cost()){
            if(cap.getEffectsManager().addEffectNoRefresh(BeyonderEffects.byId(BeyonderEffects.WHEEL_TEMP_LUCK.getEffectId(),
                    getSequenceLevel(), cost(), defaultMaxCooldown, true), cap, target)){
                cap.requestActiveSpiritualityCost(cost());
                cap.getLuckManager().grantLuck(51);
                target.sendSystemMessage(Component.translatable("message.potioneer.luck_boost_grant"));
                target.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1, 1);
            } else {
                target.sendSystemMessage(Component.translatable("message.potioneer.effect_already_exists"));
            }
        }
        return true;
    }
}
