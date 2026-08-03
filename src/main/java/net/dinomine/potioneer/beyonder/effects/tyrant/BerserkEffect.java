package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class BerserkEffect extends BeyonderEffect {

    private Set<UUID> disabledInstances = new HashSet<>();

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        if(fromLoading) return;
        cap.getAbilitiesManager().setAbilityEnabled(Abilities.TYRANT_AURA.getAblId(), sequenceLevel, true, cap, target);
        cap.getAbilitiesManager().setAbilityEnabled(Abilities.WATER_SCALES.getAblId(), sequenceLevel, true, cap, target);
        cap.getAbilitiesManager().setAbilityEnabled(Abilities.SENSE_OF_ORDER.getAblId(), sequenceLevel, true, cap, target);
        cap.getAbilitiesManager().setAbilityEnabled(Abilities.AOJ.getAblId(), sequenceLevel, true, cap, target);

        disabledInstances = new HashSet<>();
        disabledInstances.addAll(cap.getAbilitiesManager().revokeAll(Abilities.COGITATION.getAblId(), cap, target));
        disabledInstances.addAll(cap.getAbilitiesManager().revokeAll(Abilities.AOJ.getAblId(), cap, target));
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.hasEffect(MobEffects.DARKNESS))
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, -1, 2, false, true));

        cap.getEffectsManager().statsHolder.addHealth(10);
        cap.getEffectsManager().statsHolder.addDamage(10);
        cap.getEffectsManager().statsHolder.addRegeneration(5);
        if(target.tickCount%20 == 0){
            cap.changeSanity(-1);
        }
        if(cap.getSanity() < LivingEntityBeyonderCapability.SANITY_FOR_DROP && target.tickCount%10==0)
            target.level().playSound(target, target.getOnPos(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.NEUTRAL, 2, 1);
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);

        ListTag instancesList = new ListTag();
        for (UUID id : disabledInstances) {
            instancesList.add(NbtUtils.createUUID(id));
        }
        nbt.put("disabled", instancesList);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        disabledInstances = new HashSet<>();
        if (nbt.contains("disabled", Tag.TAG_LIST)) {
            ListTag argsList = nbt.getList("disabled", Tag.TAG_INT_ARRAY);
            for (Tag tag : argsList) {
                disabledInstances.add(NbtUtils.loadUUID(tag));
            }
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.removeEffect(MobEffects.DARKNESS);
        cap.getAbilitiesManager().unrevokeAll(disabledInstances, cap, target);
    }
}
