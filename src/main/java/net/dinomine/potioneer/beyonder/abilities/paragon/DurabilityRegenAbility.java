package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DurabilityRegenAbility extends Ability {

    public DurabilityRegenAbility(int sequence){
        this.info = new AbilityInfo(109, 56, "Durability Regen", 40 + sequence, 30*(10-sequence), 20*5, "durability_regen");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        disable(cap, target);
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() > info.cost()) return false;
        System.out.println("flipping enabledurabvility regen...");
        flipEnable(cap, target);


        if(!isEnabled(cap.getAbilitiesManager())){
            System.out.println("deactivating durabvility regen...");
            if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.PARAGON_DURABILITY_REGEN, getSequence())){
                cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.PARAGON_DURABILITY_REGEN, getSequence(), cap, target);
            }
            return true;
        } else if (target instanceof Player player){
            int caret = cap.getAbilitiesManager().getCaretForAbility(this);
            if(caret > -1) cap.getAbilitiesManager().putOnCooldown(player, caret, 20, 20);
        }
        return false;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
        System.out.println("activating durabvility regen...");
        if(!cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.PARAGON_DURABILITY_REGEN, getSequence())){

            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.PARAGON_DURABILITY_REGEN,
                    getSequence(), info.cost(), 60*((9-getSequence())*6 + 3), true), cap, target);
            disable(cap, target);
            return;
//                cap.requestActiveSpiritualityCost(info.cost());
        }
        target.sendSystemMessage(Component.literal("Could not give effect: one already exists"));
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
    }
}
