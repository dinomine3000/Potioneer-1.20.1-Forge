package net.dinomine.potioneer.beyonder.abilities.redpriest;

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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;
import java.util.function.Supplier;

public class WeaponProficiencyAbility extends Ability {

    public WeaponProficiencyAbility(int sequence){
        this.info = new AbilityInfo(83, 32, "Weapon Proficiency", 30 + sequence, 0, this.getCooldown());
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
        if(isEnabled(cap.getAbilitiesManager()) && !cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY, getSequence())){
            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY,
                    getSequence(), 0, -1, true), cap, target);
        }

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY, getSequence(), cap, target);
        }
    }
}
