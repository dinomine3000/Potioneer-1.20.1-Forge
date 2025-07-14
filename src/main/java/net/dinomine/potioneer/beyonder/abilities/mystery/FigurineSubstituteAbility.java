package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderFigurineEffect;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.function.Predicate;

public class FigurineSubstituteAbility extends Ability {

    public FigurineSubstituteAbility(int sequence){
        this.info = new AbilityInfo(57, 224, "Figure Substitute", 20 + sequence, 40 + 10*(8-sequence), 1, "figure");
        this.isActive = true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        return false;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        cap.getEffectsManager().addOrReplaceEffect(new BeyonderFigurineEffect(getSequence(), info.cost(), -1, true, BeyonderEffects.EFFECT.MYSTERY_FIGURINE),
                cap, target);
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_FIGURINE))
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_FIGURINE, getSequence(), cap, target);
    }
}
