package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.function.Predicate;

public class InvisibilityAbility extends Ability {

    public InvisibilityAbility(int sequence){
        this.info = new AbilityInfo(57, 128, "Invisibility", 20 + sequence, 60 + 150*(9-sequence), 5*10 + 2*40*((9-sequence)*10 + 5), "invisibility");
        this.isActive = true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= info.cost()) return true;

        if(cap.getSpirituality() >= info.cost()){
            if(!cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_INVIS, getSequence())){

                cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_INVIS,
                        getSequence(), 0, 2*40*((9-getSequence())*10 + 5), true), cap, target);

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
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_INVIS, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_INVIS, getSequence(), cap, target);
        }
    }
}
