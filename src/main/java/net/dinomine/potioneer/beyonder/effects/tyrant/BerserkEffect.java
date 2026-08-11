package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.DisabledAbilitiesManager.DisabledAbilityProxy;
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
    private boolean scales = false;
    private boolean aura = false;
    private boolean soo = false;
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        if(fromLoading) return;
        scales = cap.getAbilitiesManager().isEnabledExactLevel(Abilities.WATER_SCALES.getAblId(), sequenceLevel);
        aura = cap.getAbilitiesManager().isEnabledExactLevel(Abilities.TYRANT_AURA.getAblId(), sequenceLevel);
        soo = cap.getAbilitiesManager().isEnabledExactLevel(Abilities.SENSE_OF_ORDER.getAblId(), sequenceLevel);
        cap.getAbilitiesManager().setAbilityEnabled(Abilities.TYRANT_AURA.getAblId(), sequenceLevel, true, cap, target);
        cap.getAbilitiesManager().setAbilityEnabled(Abilities.WATER_SCALES.getAblId(), sequenceLevel, true, cap, target);
        cap.getAbilitiesManager().setAbilityEnabled(Abilities.SENSE_OF_ORDER.getAblId(), sequenceLevel, true, cap, target);
        cap.getAbilitiesManager().setAbilityEnabled(Abilities.AOJ.getAblId(), sequenceLevel, true, cap, target);

        DisabledAbilityProxy proxyCogitation = DisabledAbilityProxy.of(
                DisabledAbilityProxy.byId(Abilities.COGITATION.getAblId(), 0, -1),
                DisabledAbilityProxy.byId(Abilities.AOJ.getAblId(), 0, -1)
        );
        cap.getAbilitiesManager().getDisabledAbilitiesManager().disableAbility("berserk", proxyCogitation, cap, target);
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
        nbt.putBoolean("soo", soo);
        nbt.putBoolean("aura", aura);
        nbt.putBoolean("scales", scales);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.soo = nbt.getBoolean("soo");
        this.aura = nbt.getBoolean("aura");
        this.scales = nbt.getBoolean("scales");
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.removeEffect(MobEffects.DARKNESS);
        cap.getAbilitiesManager().getDisabledAbilitiesManager().enableAbility("berserk", cap, target);

        cap.getAbilitiesManager().setAbilityEnabled(Abilities.TYRANT_AURA.getAblId(), sequenceLevel, aura, cap, target);
        cap.getAbilitiesManager().setAbilityEnabled(Abilities.WATER_SCALES.getAblId(), sequenceLevel, scales, cap, target);
        cap.getAbilitiesManager().setAbilityEnabled(Abilities.SENSE_OF_ORDER.getAblId(), sequenceLevel, soo, cap, target);
    }
}
