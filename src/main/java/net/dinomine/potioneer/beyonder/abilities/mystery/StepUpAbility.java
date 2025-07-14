package net.dinomine.potioneer.beyonder.abilities.mystery;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;
import java.util.function.Supplier;

public class StepUpAbility extends Ability {

    public StepUpAbility(int sequence){
        this.info = new AbilityInfo(57, 152, "Step Assist", 20 + sequence, 0, this.getCooldown(), "step_up");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        activate(cap, target);
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide() || !isEnabled(cap.getAbilitiesManager())) return;
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_STEP, getSequence(), info.cost(), -1, true),
                cap, target);
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_STEP, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_STEP, getSequence(), cap, target);
        }
    }

}
