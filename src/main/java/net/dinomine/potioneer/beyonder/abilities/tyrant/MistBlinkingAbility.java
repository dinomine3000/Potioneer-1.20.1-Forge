package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.*;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
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
    protected String getMainDescId(int sequenceLevel) {
        return "mist_blinking";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return cap.getSpirituality() > cost();
        BlockHitResult res = AbilityFunctionHelper.getBlockLooking(target);
        BlockPos blockPos = res.getBlockPos().relative(res.getDirection());
        ServerLevel level = (ServerLevel) target.level();
        if(AreaOfJurisdictionAbility.isPosInAOJ(blockPos, cap, 0)){
            doMistBlinkingTo(target, cap, level, cost(), blockPos, sequenceLevel);
            return true;
        }
        return false;
    }

    public static void doMistBlinkingTo(LivingEntity caster, LivingEntityBeyonderCapability cap, ServerLevel level, int cost, BlockPos blockPos, int sequenceLevel){
        cap.getAbilitiesManager().putAbilityOnCooldown(Abilities.MIST.getAblId(), sequenceLevel, 20, caster);
        cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.TYRANT_MIST_EFFECT.createInstance(sequenceLevel, 0, 10, true), cap, caster);
        Vec3 pos = caster.getEyePosition();
        level.sendParticles(ParticleTypes.FALLING_WATER, pos.x, pos.y, pos.z, 50, 1, 0, 1, 0);
        Vec3 motion = caster.getDeltaMovement();
        caster.teleportTo(blockPos.getX() + 0.5f, blockPos.getY(), blockPos.getZ() + 0.5);
        caster.setDeltaMovement(motion);
        caster.hasImpulse = true;
        cap.requestActiveSpiritualityCost(cost);
    }
}
