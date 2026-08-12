package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.*;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

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
        withCost(PotioneerAbilityConfig.MIST_BLINK_COST.get());
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "mist_blinking";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return cap.getSpirituality() > cost();
        BlockHitResult res = AbilityFunctionHelper.getBlockLooking(target);
        BlockPos blockPos = res.getBlockPos().relative(res.getDirection());
        ServerLevel level = (ServerLevel) target.level();
        if(AreaOfJurisdictionAbility.isPosInAOJ(blockPos, target, target.level().dimension())){
            doMistBlinkingTo(target, cap, level, level, cost(), blockPos, sequenceLevel);
            return true;
        }
        return false;
    }
    public static void doMistBlinkingTo(LivingEntity caster, BeyonderCapability cap, ServerLevel fromLevel, String toDimensionId, int cost, BlockPos blockPos, int sequenceLevel){
        MinecraftServer server = caster.getServer();
        ServerLevel targetLevel = AbilityFunctionHelper.getDimensionKey(server, toDimensionId);
        doMistBlinkingTo(caster, cap, fromLevel, targetLevel, cost, blockPos, sequenceLevel);
    }

    public static void doMistBlinkingTo(LivingEntity caster, BeyonderCapability cap, ServerLevel fromLevel, ServerLevel toLevel, int cost, BlockPos blockPos, int sequenceLevel){
        if (toLevel == null) return;

        cap.getAbilitiesManager().putAbilityOnCooldown(Abilities.MIST.getAblId(), sequenceLevel, 20, caster);
        cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.TYRANT_MIST_EFFECT.createInstance(sequenceLevel, 0, 10, true), cap, caster);

        Vec3 pos = caster.getEyePosition();
        fromLevel.sendParticles(ParticleTypes.FALLING_WATER, pos.x, pos.y, pos.z, 50, 1, 0, 1, 0);

        AbilityFunctionHelper.teleportEntity(caster, fromLevel, toLevel, blockPos);

        cap.requestActiveSpiritualityCost(cost);
    }
    public static class SimpleTeleporter implements ITeleporter {
        private final Vec3 targetPos;

        public SimpleTeleporter(Vec3 targetPos) {
            this.targetPos = targetPos;
        }

        @Override
        public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
            entity.moveTo(targetPos.x, targetPos.y, targetPos.z, yaw, entity.getXRot());
            return repositionEntity.apply(false);
        }
    }
}
