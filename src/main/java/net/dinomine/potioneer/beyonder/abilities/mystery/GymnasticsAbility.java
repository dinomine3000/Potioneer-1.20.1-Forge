package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.mystery.GymnasticsEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;

public class GymnasticsAbility extends PassiveAbility {
    private static final Supplier<Integer> LEAP_COST = () -> 5;
    public GymnasticsAbility(int sequenceLevel) {
        super(sequenceLevel, BeyonderEffects.MYSTERY_GYMNASTICS, ign -> "gymnastics");
        enabledOnAcquire();
        canFlip();
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        return super.primary(cap, target);
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < LEAP_COST.get()) return false;
        GymnasticsEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.MYSTERY_GYMNASTICS.getEffectId(), target);
        if(eff == null || !eff.canJump()) return false;
        if(target.level().isClientSide()) return true;
        Vec3 look = target.getLookAngle();
        float scalar = 2;
        double mult = 2 + scalar*(9-getSequenceLevel());
        AbilityFunctionHelper.pushEntity(target, look.multiply(mult, mult/2.5, mult));
        target.level().playSound(null, target.getOnPos(), ModSounds.WHOOOOSH.get(), SoundSource.PLAYERS);
        cap.requestActiveSpiritualityCost(LEAP_COST.get());
        setNextCooldownAs(20*3);
        return true;
    }
}
