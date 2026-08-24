package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class UnseenHandAbility extends Ability {
    private static final Supplier<Integer> PUSH_COST = () -> 10;
    private static final Supplier<Integer> RAISE_COST = () -> 10;

    @Override
    public void init() {
        super.init();
        defaultMaxCooldown = 20*3;
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity caster) {
        if(cap.getSpirituality() < PUSH_COST.get()) return false;
        if(caster.level().isClientSide()) return true;
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, 0);
        if(target == null) return false;

        float scale = 2;
        Vec3 direction = target.position().subtract(caster.position()).with(Direction.Axis.Y, 0).normalize().scale(scale);
        AbilityFunctionHelper.pushEntity(target, direction);
        caster.level().playSound(null, target.getOnPos(), ModSounds.PUSH.get(), SoundSource.PLAYERS, 1, 1);
        cap.requestActiveSpiritualityCost(PUSH_COST.get());
        return true;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity caster) {
        if(cap.getSpirituality() < RAISE_COST.get()) return false;
        if(caster.level().isClientSide()) return true;
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, 0);
        if(target == null) return false;
        Vec3 direction = new Vec3(0, 1, 0);
        AbilityFunctionHelper.pushEntity(target, direction);
        target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false, false));
        caster.level().playSound(null, target.getOnPos(), ModSounds.PUSH.get(), SoundSource.PLAYERS, 1, 1);
        cap.requestActiveSpiritualityCost(PUSH_COST.get());
        return true;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "unseen_hand";
    }
}
