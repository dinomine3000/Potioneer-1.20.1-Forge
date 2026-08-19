package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AoJRecipientEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.*;

public class AreaOfJurisdictionAbility extends PassiveAbility implements IAreaOfJurisdiction {
    private static final int cost = 75;
    public AreaOfJurisdictionAbility() {
        super(BeyonderEffects.TYRANT_AOJ_VIEWER, level -> "area_of_jurisdiction" + (level < 7 ? (level < 6 ? "_3" : "_2") : ""));
    }

    @Override
    public void init() {
        enabledOnAcquire();
    }

    @Override
    protected boolean hasSecondary(int level) {
        return true;
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() < cost) return false;
        BlockPos center = target.getOnPos();
        CompoundTag tag = getData();
        placeNewCenter(tag, center, target.level().getGameTime(), target);
        int cd = PotioneerAbilityConfig.AOJ_COOLDOWN.get();
        setNextCooldownAs(getSequenceLevel() < 6 ? (int) (cd * 0.7f) : cd);
        cap.requestActiveSpiritualityCost(cost);
        return true;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        super.passive(cap, target);
        if(target.level().isClientSide()) return;
        cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.TYRANT_AOJ_SOURCE.createInstance(getSequenceLevel(), 0, 20, true), cap, target);
        if(isRevoked()) return;
        if(target.tickCount%20 == target.getId()%20){
            if(isEntityInAOJ(target, target))
                target.addEffect(new MobEffectInstance(ModEffects.AOJ_INFLUENCE.get(), 250, 0, false, false, true));

            CompoundTag dataTag = getData();
            List<CompoundTag> centersList = new ArrayList<>(getCentersCompoundTagList(dataTag, false, ""));
            boolean changedFlag = false;
            int setupTime = getSequenceLevel() < 6 ? 20*2 : 20*30;
            for(CompoundTag centerTag: centersList){
                if(centerTag.contains("aoj_enabled") && !centerTag.getBoolean("aoj_enabled") && target.level().getGameTime() - centerTag.getLong("timestamp") > setupTime){
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
        int idx = getNextAvailableIndex(dataTag, getSequenceLevel());
        List<CompoundTag> centerTags = getCentersCompoundTagList(dataTag, false, "");
        String dimension = target.level().dimension().location().toString();
        CompoundTag newCenter = createAreaTag(center, dimension, timestamp, false);
        if (idx < centerTags.size()) centerTags.set(idx, newCenter);
        else centerTags.add(newCenter);
        target.level().playSound((Entity) null, target.getOnPos(), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 1F, (float) target.getRandom().triangle(1, 0.2f));
        target.sendSystemMessage(Component.translatableWithFallback("ability.potioneer.aoj_set", "Area of Jurisdiction centered on: %s, %s", center.getX(), center.getZ()));
        setData(createDataTag(centerTags, idx + 1), target);
    }

    private static int getMaxCentersAtLevel(int sequenceLevel){
        return sequenceLevel < 7 ? (sequenceLevel < 6 ? 3 : 2) : 1;
    }

    private static int getNextAvailableIndex(CompoundTag dataTag, int sequenceLevel){
        return dataTag.getInt("nextIdx") % getMaxCentersAtLevel(sequenceLevel);
    }

    private static List<CompoundTag> getCentersCompoundTagList(CompoundTag dataTag, boolean skipDisabled, String dimensionId){
        List<CompoundTag> centerTags = new ArrayList<>();
        if (dataTag.contains("areas", Tag.TAG_LIST)) {
            ListTag areasList = dataTag.getList("areas", Tag.TAG_COMPOUND);
            for (int i = 0; i < areasList.size(); i++) {
                if(skipDisabled && !areasList.getCompound(i).getBoolean("aoj_enabled")) continue;
                if(!dimensionId.isEmpty() && !dimensionId.equalsIgnoreCase(areasList.getCompound(i).getString("dimension"))) continue;
                centerTags.add(areasList.getCompound(i).copy());
            }
        }
        return centerTags;
    }

    private static BlockPos getCenterFromCompoundTag(CompoundTag centerTag){
        return new BlockPos(centerTag.getInt("centerX"), centerTag.getInt("centerY"), centerTag.getInt("centerZ"));
    }

    public static String getDimensionFromCompoundTag(CompoundTag centerTag){
        return centerTag.getString("dimension");
    }

    private CompoundTag createDataTag(List<CompoundTag> centers, int nextIdx){
        CompoundTag tag = new CompoundTag();
        ListTag compoundList = new ListTag();
        compoundList.addAll(centers);
        tag.put("areas", compoundList);
        tag.putInt("nextIdx", nextIdx % getMaxCentersAtLevel(getSequenceLevel()));
        return tag;
    }

    private static CompoundTag createAreaTag(BlockPos center, String dimension, long timestamp, boolean enabled){
        CompoundTag res = new CompoundTag();
        res.putInt("centerX", center.getX());
        res.putInt("centerY", center.getY());
        res.putInt("centerZ", center.getZ());
        res.putString("dimension", dimension);
        res.putLong("timestamp", timestamp);
        res.putBoolean("aoj_enabled", enabled);
        return res;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        flipEnable(cap, target);
        defaultMaxCooldown = 20;
        return true;
    }

    @Override
    public void onRevoke(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void onUndoRevoke(BeyonderCapability cap, LivingEntity target) {
    }

    public static List<BlockPos> getCentersOfEnforcer(Entity enforcer, ResourceKey<Level> dimension){
        if(!(enforcer instanceof LivingEntity lEnforcer)) return new ArrayList<>();
        BeyonderCapability cap = lEnforcer.getCapability(CapProvider.BEYONDER_STATS).resolve().get();
        List<BlockPos> centers = new ArrayList<>();
        for(Ability abl: cap.getAbilitiesManager().getAllAbilities()){
            if(abl instanceof IAreaOfJurisdiction aojAbl){
                centers.addAll(aojAbl.getCenters(dimension.location().toString()).stream().toList());
            }
        }
        return centers;
    }

    public static boolean isTargetUnderInfluenceOfEnforcer(LivingEntity target, Entity enforcer){
        if(!(enforcer instanceof LivingEntity)) return false;
        Optional<BeyonderCapability> optTarget = target.getCapability(CapProvider.BEYONDER_STATS).resolve();
        if(optTarget.isEmpty()) return false;
        BeyonderCapability targetCap = optTarget.get();
        BeyonderEffect eff = targetCap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_AOJ_RECIPIENT.getEffectId());
        if(!(eff instanceof AoJRecipientEffect aoJInfluenceEffect)) return false;
        return aoJInfluenceEffect.isEntityEnforcer(enforcer.getUUID());
    }

    public static boolean isPosInAOJ(BlockPos testPos, Entity enforcer, String dimensionLocation){
        if(!(enforcer instanceof LivingEntity lEnforcer)) return false;
        Optional<BeyonderCapability> optCap = lEnforcer.getCapability(CapProvider.BEYONDER_STATS).resolve();
        if(optCap.isEmpty()) return false;
        return isPosInAOJ(testPos, optCap.get(), dimensionLocation);
    }

    public static boolean isPosInAOJ(BlockPos testPos, Entity enforcer, ResourceKey<Level> dimensionLocation){
        return isPosInAOJ(testPos, enforcer, dimensionLocation.location().toString());
    }

    public static boolean isEntityInAOJ(Entity target, Entity enforcer){
        return isPosInAOJ(target.getOnPos(), enforcer, target.level().dimension().location().toString());
    }

    private static boolean isPosInAOJ(BlockPos testPos, BeyonderCapability enforcerCap, String dimensionLocation){
        List<BlockPos> centers = new ArrayList<>();
        List<Integer> sides = new ArrayList<>();
        for(Ability abl: enforcerCap.getAbilitiesManager().getAllAbilities()){
            if(abl instanceof IAreaOfJurisdiction aojAbl){
                centers.addAll(aojAbl.getCenters(dimensionLocation));
                sides.addAll(aojAbl.getSides(dimensionLocation));
            }
        }
        return isPosInAOJ(testPos, centers, sides);
    }

    public static boolean isPosInAOJ(BlockPos testPos, List<BlockPos> centers, List<Integer> sideLengths){
        return isPosInAOJ(testPos, centers, sideLengths, 0);
    }

    public static boolean isPosInAOJ(BlockPos testPos, List<BlockPos> centers, List<Integer> sideLengths, int remove){
        for(int k = 0; k < centers.size(); k++){
            BlockPos testCenter = centers.get(k);
            int testSide = sideLengths.get(k);
            if(isPosContainedInArea(testPos, testCenter, testSide, remove)) return true;
        }
        return false;
    }

    public static boolean isPosContainedInArea(BlockPos test, BlockPos center, int sideLengths, int remove){
        if(sideLengths%2==0){
            int radius = sideLengths/2;
            return Math.max(Math.abs(test.getX() - center.getX() + 0.5), Math.abs(test.getZ() - center.getZ() + 0.5)) <= radius - remove;
        } else {
            int radius = (sideLengths-1)/2;
            return Math.max(Math.abs(test.getX() - center.getX()), Math.abs(test.getZ() - center.getZ())) <= radius - remove;
        }
    }

    @Override
    public List<BlockPos> getCenters(String dimensionLocation) {
        return getCentersCompoundTagList(getData(), true, dimensionLocation).stream().map(AreaOfJurisdictionAbility::getCenterFromCompoundTag).toList();
    }

    @Override
    public List<Integer> getSides(String dimensionLocation) {
        int sideLength = 16 + (10 - getSequenceLevel())*4;
        return getCentersCompoundTagList(getData(), true, dimensionLocation).stream().map(ign -> sideLength).toList();
    }
}