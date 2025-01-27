package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class DurabilityRegenAbility extends Ability {

    public DurabilityRegenAbility(int sequence){
        this.info = new AbilityInfo(109, 56, "Durability Regen", 40 + sequence, 30*(10-sequence), 20*5);
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= info.cost()) return true;

        if(cap.getSpirituality() >= info.cost()){
            if(!cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.PARAGON_DURABILITY_REGEN, getSequence())){

                cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.PARAGON_DURABILITY_REGEN,
                        getSequence(), 0, 60*((9-getSequence())*6 + 3), true), cap, target);

                cap.requestActiveSpiritualityCost(info.cost());
                return true;
            }
            target.sendSystemMessage(Component.literal("Could not give effect: one already exists"));
        }
        return false;
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
