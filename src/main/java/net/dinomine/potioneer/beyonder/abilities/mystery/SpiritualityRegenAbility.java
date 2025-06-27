package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SpiritualityRegenAbility extends Ability {

    public SpiritualityRegenAbility(int sequence){
        this.info = new AbilityInfo(57, 32, "Regen on Damage", 20 + sequence, 0, this.getCooldown(), "spirituality_regen");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(isEnabled(cap.getAbilitiesManager()) && !cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_REGEN, getSequence())){
//            System.out.println("given regen effect");
            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_REGEN,
                    getSequence(), 0, -1, true), cap, target);
        }

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_REGEN, getSequence())){
//            System.out.println("taketh away");
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_REGEN, getSequence(), cap, target);
        }
    }
}
