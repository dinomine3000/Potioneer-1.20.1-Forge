package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AoJRecipientEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

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
        super(sequenceLevel, BeyonderEffects.TYRANT_AOJ_SOURCE, level -> "area_of_jurisdiction" + (level < 7 ? "_2" : ""));
        enabledOnAcquire();
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() < cost()) return false;
        BlockPos center = target.getOnPos();
        CompoundTag tag = getData();
        placeNewCenter(tag, center, target.level().getGameTime(), target);
        setNextCooldownAs(20);
        cap.requestActiveSpiritualityCost(cost());
        return true;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        super.passive(cap, target);
        if(target.level().isClientSide()) return;
        if(target.tickCount%20 == target.getId()%20){
            if(isPosInAOJ(target.getOnPos(), cap, 0))
                target.addEffect(new MobEffectInstance(ModEffects.AOJ_INFLUENCE.get(), 250, 0, false, false, true));

            CompoundTag dataTag = getData();
            List<CompoundTag> centersList = getCentersCompoundTagList(dataTag, false);
            boolean changedFlag = false;
            for(CompoundTag centerTag: centersList){
                if(centerTag.contains("aoj_enabled") && !centerTag.getBoolean("aoj_enabled") && target.level().getGameTime() - centerTag.getLong("timestamp") > 20*2){
                    changedFlag = true;
                    centerTag.putBoolean("aoj_enabled", true);
                    target.level().playSound(null, target.getOnPos(), SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1, 1);
                    target.sendSystemMessage(Component.translatableWithFallback("ability.potioneer.aoj_active", "Your area of jurisdiction is active."));
                }
            }
            if(changedFlag) setData(createDataTag(centersList, dataTag.getInt("nextIdx")), target);
        }
    }

    private void placeNewCenter(CompoundTag dataTag, BlockPos center, long timestamp, LivingEntity target){
        int idx = getNextAvailableIndex(dataTag, sequenceLevel);
        List<CompoundTag> centerTags = getCentersCompoundTagList(dataTag, false);
        CompoundTag newCenter = createAreaTag(center, timestamp, false);
        if (idx < centerTags.size()) centerTags.set(idx, newCenter);
        else centerTags.add(newCenter);
        target.level().playSound((Entity) null, target.getOnPos(), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 1F, (float) target.getRandom().triangle(1, 0.2f));
        target.sendSystemMessage(Component.translatableWithFallback("ability.potioneer.aoj_set", "Area of Jurisdiction centered on: %s, %s", center.getX(), center.getZ()));
        setData(createDataTag(centerTags, idx + 1), target);
    }

    private static int getMaxCentersAtLevel(int sequenceLevel){
        return sequenceLevel < 7 ? 2 : 1;
    }

    private static int getNextAvailableIndex(CompoundTag dataTag, int sequenceLevel){
        return dataTag.getInt("nextIdx") % getMaxCentersAtLevel(sequenceLevel);
    }

    private static List<CompoundTag> getCentersCompoundTagList(CompoundTag dataTag, boolean skipDisabled){
        List<CompoundTag> centerTags = new ArrayList<>();
        if (dataTag.contains("areas", Tag.TAG_LIST)) {
            ListTag areasList = dataTag.getList("areas", Tag.TAG_COMPOUND);
            for (int i = 0; i < areasList.size(); i++) {
                if(skipDisabled && !areasList.getCompound(i).getBoolean("aoj_enabled")) continue;
                centerTags.add(areasList.getCompound(i));
            }
        }
        return centerTags;
    }

    private static BlockPos getCenterFromCompoundTag(CompoundTag centerTag){
        return new BlockPos(centerTag.getInt("centerX"), centerTag.getInt("centerY"), centerTag.getInt("centerZ"));
    }

    private CompoundTag createDataTag(List<CompoundTag> centers, int nextIdx){
        CompoundTag tag = new CompoundTag();
        ListTag compoundList = new ListTag();
        compoundList.addAll(centers);
        tag.put("areas", compoundList);
        tag.putInt("nextIdx", nextIdx % getMaxCentersAtLevel(sequenceLevel));
        return tag;
    }

    private static CompoundTag createAreaTag(BlockPos center, long timestamp, boolean enabled){
        CompoundTag res = new CompoundTag();
        res.putInt("centerX", center.getX());
        res.putInt("centerY", center.getY());
        res.putInt("centerZ", center.getZ());
        res.putLong("timestamp", timestamp);
        res.putBoolean("aoj_enabled", enabled);
        return res;
    }

    public static List<BlockPos> getCentersOfEnforcer(Entity enforcer){
        if(!(enforcer instanceof LivingEntity lEnforcer)) return new ArrayList<>();
        LivingEntityBeyonderCapability cap = lEnforcer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
        List<BlockPos> centers = new ArrayList<>();
        for(Ability abl: cap.getAbilitiesManager().getAbilities()){
            if(abl instanceof IAreaOfJurisdiction aojAbl){
                centers.addAll(aojAbl.getCenters());
            }
        }
        return centers;
    }


    public static boolean isTargetUnderInfluenceOfEnforcer(LivingEntity target, Entity enforcer){
        if(!(enforcer instanceof LivingEntity)) return false;
        Optional<LivingEntityBeyonderCapability> optTarget = target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        if(optTarget.isEmpty()) return false;
        LivingEntityBeyonderCapability targetCap = optTarget.get();
        BeyonderEffect eff = targetCap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_AOJ_RECIPIENT.getEffectId());
        if(!(eff instanceof AoJRecipientEffect aoJInfluenceEffect)) return false;
        return aoJInfluenceEffect.isEntityEnforcer(enforcer.getUUID());
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        flipEnable(cap, target);
        defaultMaxCooldown = 20;
        return true;
    }

    @Override
    public void onRevoke(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void onUndoRevoke(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    public static boolean isPosInAOJ(BlockPos testPos, Entity enforcer){
        if(!(enforcer instanceof LivingEntity lEnforcer)) return false;
        LivingEntityBeyonderCapability cap = lEnforcer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
        return isPosInAOJ(testPos, cap, 0);
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
    public List<BlockPos> getCenters() {
        return getCentersCompoundTagList(getData(), true).stream().map(AreaOfJurisdictionAbility::getCenterFromCompoundTag).toList();
    }

    @Override
    public List<Integer> getRadius() {
        int radius = 8 + (10 - getSequenceLevel())*2;
        return getCentersCompoundTagList(getData(), true).stream().map(ign -> radius).toList();
    }
}
