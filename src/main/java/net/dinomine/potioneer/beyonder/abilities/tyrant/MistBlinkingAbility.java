package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MistBlinkingAbility extends Ability {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public MistBlinkingAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "mist_blinking";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return cap.getSpirituality() > cost();
        BlockHitResult res = AbilityFunctionHelper.getBlockLooking(target);
        BlockPos blockPos = res.getBlockPos().relative(res.getDirection());
        ServerLevel level = (ServerLevel) target.level();
        if(AreaOfJurisdictionAbility.isPosInAOJ(blockPos, cap, 0)){
            cap.getAbilitiesManager().putAbilityOnCooldown(Abilities.MIST.getAblId(), getSequenceLevel(), 20, target);
            cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.TYRANT_MIST_EFFECT.createInstance(getSequenceLevel(), 0, 30, true), cap, target);
            Vec3 pos = target.getEyePosition();
            level.sendParticles(ParticleTypes.FALLING_WATER, pos.x, pos.y, pos.z, 50, 1, 0, 1, 0);
            Vec3 motion = target.getDeltaMovement();
            target.teleportTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            target.setDeltaMovement(motion);
            target.hasImpulse = true;
            cap.requestActiveSpiritualityCost(cost());
            return true;
        }
        return false;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(getSequenceLevel() >= 7) {
            if(target.level().isClientSide())
                target.sendSystemMessage(Component.translatableWithFallback("message.potioneer.outdated_secondary", "It doesn't do anything... yet"));
            return false;
        }
        return true;
    }
}
