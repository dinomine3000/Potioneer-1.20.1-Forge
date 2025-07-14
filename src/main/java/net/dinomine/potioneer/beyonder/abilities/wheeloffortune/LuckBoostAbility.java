package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class LuckBoostAbility extends Ability {

    public LuckBoostAbility(int sequence){
        this.info = new AbilityInfo(5, 128, "Luck Boost", sequence, 30*(10-sequence), 5*20, "luck_boost");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= info.cost()) return true;

        if(cap.getSpirituality() >= info.cost()){
            if(cap.getEffectsManager().addEffectNoRefresh(BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_TEMP_LUCK,
                    getSequence(), 0, 40*60, true), cap, target)){
                cap.requestActiveSpiritualityCost(info.cost());
                cap.getLuckManager().grantLuck(51);
                target.sendSystemMessage(Component.literal("The cogs of fate favor you for now..."));
                target.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1, 1);
            }
        } else {
            target.sendSystemMessage(Component.literal("Could not give effect: one already exists."));
        }
        return true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

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
