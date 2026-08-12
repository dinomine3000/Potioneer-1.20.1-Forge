package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class WaterJetEffect extends BeyonderEffect {
    private static final float MAGNITUDE = 0.1f;
    private static final float RANGE = 10f;
    public static final int DURATION = 20*5;

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()) ParticleMaker.createWaterJet(target);
        target.playSound(ModSounds.WATER_JET.get(), 1, (float) target.getRandom().triangle(1, 0.2));
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        Vec3 look = target.getLookAngle();
        Vec3 pushAngle = look.normalize().scale(MAGNITUDE);
        AbilityFunctionHelper.getLivingEntitiesLooking(target, RANGE, 0, false).forEach(ent -> {
            if(AbilityFunctionHelper.areEntitiesAllies(target, ent)) return;
            AbilityFunctionHelper.pushEntity(ent, pushAngle);
        });
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
    }
}
