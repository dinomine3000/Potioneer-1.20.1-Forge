package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AoJRecipientEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AreaOfJurisdictionAbility extends PassiveAbility implements IAreaOfJurisdiction {
    public static final int DEFAULT_RADIUS = 16;
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public AreaOfJurisdictionAbility(int sequenceLevel) {
        super(sequenceLevel, BeyonderEffects.TYRANT_AOJ_SOURCE, ignored -> "area_of_jurisdiction");
        enabledOnAcquire();
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() < cost()) return false;
        BlockPos center = target.getOnPos();
        CompoundTag tag = getData();
        tag.putInt("centerX", center.getX());
        tag.putInt("centerY", center.getY());
        tag.putInt("centerZ", center.getZ());
        tag.putLong("timestamp", target.level().getGameTime());
        tag.putBoolean("aoj_enabled", false);
        setData(tag, target);
        target.level().playSound((Entity) null, target.getOnPos(), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 1F, (float) target.getRandom().triangle(1, 0.2f));
        target.sendSystemMessage(Component.translatableWithFallback("ability.potioneer.aoj_set", "Area of Jurisdiction centered on: %s, %s", center.getX(), center.getZ()));
        defaultMaxCooldown = 20*30;
        cap.requestActiveSpiritualityCost(cost());
        return true;
    }

    public static boolean isTargetUnderInfluenceOfEnforcer(LivingEntity target, LivingEntity enforcer){
        Optional<LivingEntityBeyonderCapability> optTarget = target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        if(optTarget.isEmpty()) return false;
        LivingEntityBeyonderCapability targetCap = optTarget.get();
        BeyonderEffect eff = targetCap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_AOJ_RECIPIENT.getEffectId());
        if(!(eff instanceof AoJRecipientEffect aoJInfluenceEffect)) return false;
        return aoJInfluenceEffect.isEntityEnforcer(enforcer.getUUID());
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        super.passive(cap, target);
        if(getData().contains("aoj_enabled") && !getData().getBoolean("aoj_enabled") && target.level().getGameTime() - getData().getLong("timestamp") > 20*30){
            CompoundTag tag = getData();
            tag.putBoolean("aoj_enabled", true);
            setData(tag, target);
            target.level().playSound(null, target.getOnPos(), SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1, 1);
            target.sendSystemMessage(Component.translatableWithFallback("ability.potioneer.aoj_active", "Your area of jurisdiction is active."));
        }
        if(!target.level().isClientSide() && target.tickCount%20 == target.getId()%20){
            if(isPosInAOJ(target.getOnPos(), cap, 0))
                target.addEffect(new MobEffectInstance(ModEffects.AOJ_INFLUENCE.get(), 250, 0, false, false, true));
        }
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        flipEnable(cap, target);
        defaultMaxCooldown = 20;
        return true;
    }

    public static boolean isPosInAOJ(BlockPos testPos, LivingEntityBeyonderCapability enforcerCap, int remove){
        List<BlockPos> centers = new ArrayList<>();
        List<Integer> radii = new ArrayList<>();
        for(Ability abl: enforcerCap.getAbilitiesManager().getAbilities()){
            if(abl instanceof IAreaOfJurisdiction aojAbl){
                centers.addAll(aojAbl.getCenters());
                radii.addAll(aojAbl.getRadius());
            }
        }
        return isPosInAOJ(testPos, centers, radii, remove);
    }

    public static boolean isPosInAOJ(BlockPos testPos, List<BlockPos> centers, List<Integer> radii, int remove){
        for(int k = 0; k < centers.size(); k++){
            BlockPos testCenter = centers.get(k);
            int testRadius = radii.size() > k ? radii.get(k) : DEFAULT_RADIUS;
            if(isPosContainedInArea(testPos, testCenter, testRadius - remove)) return true;
        }
        return false;
    }

    public static boolean isPosContainedInArea(BlockPos test, BlockPos center, int radius){
        return Math.max(Math.abs(test.getX() - center.getX()), Math.abs(test.getZ() - center.getZ())) <= radius;
    }


    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public List<BlockPos> getCenters() {
        if(!getData().contains("centerX") || !getData().getBoolean("aoj_enabled")) return List.of();
        BlockPos center = new BlockPos(getData().getInt("centerX"), getData().getInt("centerY"), getData().getInt("centerZ"));
        return List.of(center);
    }

    @Override
    public List<Integer> getRadius() {
        return List.of(8 + (10 - getSequenceLevel())*2);
    }
}
