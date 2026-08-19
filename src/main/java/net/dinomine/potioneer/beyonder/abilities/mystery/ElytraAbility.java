package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;

public class ElytraAbility extends PassiveAbility {
    private static final Supplier<Integer> BOOST_COST = () -> 10;
    public ElytraAbility() {
        super(BeyonderEffects.MYSTERY_ELYTRA, ign -> "elytra");
        withCost(5);
        canFlip();
        defaultMaxCooldown = 20*3;
    }

    @Override
    protected boolean hasSecondary(int level) {
        return true;
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(!target.isFallFlying()) return false;
        Vec3 look = target.getLookAngle();
        double mult = 10 + 2*(6-getSequenceLevel());
        AbilityFunctionHelper.pushEntity(target, look.multiply(mult, mult, mult));
        target.level().playSound(null, target.getOnPos(), ModSounds.WHOOOOSH.get(), SoundSource.PLAYERS);
        cap.requestActiveSpiritualityCost(BOOST_COST.get());
        return true;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        return super.primary(cap, target);
    }
}
