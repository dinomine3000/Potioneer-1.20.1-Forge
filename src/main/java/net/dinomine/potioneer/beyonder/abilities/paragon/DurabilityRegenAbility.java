package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DurabilityRegenAbility extends Ability {
    private boolean levelUp;
    public DurabilityRegenAbility(int sequence){
        levelUp = sequence <= 7;
        this.info = new AbilityInfo(109, 56, "Durability Regen", 40 + sequence, 30*(10-sequence), levelUp ? this.getCooldown() : 20*5, "durability_regen_" + (levelUp ? "2" : ""));
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!levelUp){
            disable(cap, target);
        } else {
            //by default, every ability starts as Enabled when acquired via advancement.
            //as such, calling Enable() will not trigger activate, and we need to do it manually here
            activate(cap, target);
        }
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() > info.cost()) return false;
        flipEnable(cap, target);

        if (isEnabled(cap.getAbilitiesManager()) && !levelUp && target instanceof Player player){
            String id = getInfo().descId();
            cap.getAbilitiesManager().putOnCooldown(player, id, 20, 20);
            //return false to have custom cooldown
            return false;
        }
        return true;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        int duration = levelUp ? -1 : 60*((9-getSequence())*6 + 3);
        float cost = levelUp ? info.cost() / 10f: info.cost()/5f;
        if(cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.PARAGON_DURABILITY_REGEN,
                getSequence(), cost, duration, true), cap, target)){
            if(!levelUp) disable(cap, target);
            return;
        }
        target.sendSystemMessage(Component.literal("Could not give effect: one already exists"));
    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.PARAGON_DURABILITY_REGEN, getSequence())){
            cap.getEffectsManager().getEffect(BeyonderEffects.EFFECT.PARAGON_DURABILITY_REGEN, getSequence()).endEffectWhenPossible();
        }
    }
}
