package net.dinomine.potioneer.beyonder.abilities.tyrant;

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

public class ElectrificationAbility extends Ability {

    public ElectrificationAbility(int sequence){
        this.info = new AbilityInfo(31, 272, "Electrification", 10 + sequence, 20, this.getCooldown(), "electrification");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        activate(cap, target);
    }

    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(isEnabled(cap.getAbilitiesManager())){
            if(!cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.TYRANT_ELECTRIFICATION, getSequence())){
                cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.TYRANT_ELECTRIFICATION,
                        getSequence(), info.cost(), -1, true), cap, target);
            }
            if(cap.getSpirituality() < 1) flipEnable(cap, target);
        }
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.TYRANT_ELECTRIFICATION, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.TYRANT_ELECTRIFICATION, getSequence(), cap, target);
        }
    }
}
