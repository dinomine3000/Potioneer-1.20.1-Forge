package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.mystery.AirBulletEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.function.Supplier;

public class AirBulletAbility extends Ability {
    private static final Supplier<Integer> EFFECT_DURATION = () -> 20*10;
    private static final int cost = 50;
    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "air_bullet";
    }

    @Override
    public void init() {
        super.init();
        defaultMaxCooldown = 20*30;
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity caster) {
        if(cap.getSpirituality() < cost) return false;
        if(caster.level().isClientSide()) return true;
        double dist = Math.max((10 - getSequenceLevel())*8 - 8, 21);
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, dist, 1);
        if(target == null) return false;

        setNextCooldownAs(20);
        Vector3f eyePos = caster.getEyePosition().toVector3f();
        float trajectoryLength = (float) Math.min(dist, target.distanceTo(caster));
        Vector3f lookAngle = caster.getLookAngle().toVector3f();
        Vector3f trajVec = lookAngle.normalize().mul(trajectoryLength);
        Vector3f endPos = new Vector3f(eyePos).add(trajVec);
        PacketHandler.sendMessageToClientsAround(caster, 16,
                new GeneralAreaEffectMessage(ParticleMaker.Preset.AIR_BULLET, eyePos, endPos, 0));

        //cap.requestActiveSpiritualityCost(cost);
        caster.level().playSound(null, caster.getOnPos().above(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1, 1);

        if(target.getMaxHealth() <= AirBulletEffect.DAMAGE) {
            target.hurt(PotioneerDamage.air_bullet((ServerLevel) caster.level(), caster), AirBulletEffect.DAMAGE);
            return true;
        }

        target.hurt(PotioneerDamage.air_bullet((ServerLevel) caster.level(), caster), 2);

        CapProvider.beyonder(target).ifPresent(targetCap -> {
            targetCap.getEffectsManager().addEffectNoCheck(BeyonderEffects.MYSTERY_AIR_BULLET.createInstance(getSequenceLevel(), 0, EFFECT_DURATION.get(), false), targetCap, target);
        });

        return true;
    }

}
