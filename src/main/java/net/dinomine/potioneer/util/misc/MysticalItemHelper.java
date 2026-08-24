package net.dinomine.potioneer.util.misc;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.dinomine.potioneer.util.misc.ModNbtUtils.*;
import static net.dinomine.potioneer.util.misc.ModNbtUtils.ArtifactInfoTag.isArtifactCharged;
import static net.dinomine.potioneer.util.misc.ModNbtUtils.ArtifactInfoTag.saveArtifactToItem;
import static net.dinomine.potioneer.util.misc.ModNbtUtils.CharmInfoTag.*;

public class MysticalItemHelper {
    public static final String GEM_TAG_ID = "gem_ability_info";

    private static final List<MetaArtifactAbility> abilityMap = new ArrayList<>();
    private static final List<MetaArtifactAbility> downsideMap = new ArrayList<>();

    static {
        //define minimum and maximum sequences here to define what levels can generate the ability
        //min sequence is inclusive, max sequence is exclusive
        abilityMap.add(new MetaArtifactAbility(8, 10, Abilities.MINER_LIGHT.getId()));
        abilityMap.add(new MetaArtifactAbility(0, 7, Abilities.HALF_COOLDOWN.getId()));
        abilityMap.add(new MetaArtifactAbility(0, 7, Abilities.FATE.getId()));
        abilityMap.add(new MetaArtifactAbility(0, 9, Abilities.APPRAISAL.getId()));
        abilityMap.add(new MetaArtifactAbility(0, 9, Abilities.TARGET_APPRAISAL.getId()));
        abilityMap.add(new MetaArtifactAbility(0, 8, Abilities.GAMBLING.getId()));
        abilityMap.add(new MetaArtifactAbility(0, 9, Abilities.BLOCK_APPRAISAL.getId()));
        abilityMap.add(new MetaArtifactAbility(0, 10, Abilities.MINING_SPEED.getId()));
        abilityMap.add(new MetaArtifactAbility(0, 10, Abilities.VOID_VISION.getId()));
        abilityMap.add(new MetaArtifactAbility(18, 20, Abilities.WATER_AFFINITY.getId()));
        abilityMap.add(new MetaArtifactAbility(17, 20, Abilities.WATER_SCALES.getId()));
        abilityMap.add(new MetaArtifactAbility(10, 19, Abilities.TYRANT_WATER_SPELLS.getId()));
        abilityMap.add(new MetaArtifactAbility(17, 19, Abilities.TYRANT_DIVINATION.getId()));
        abilityMap.add(new MetaArtifactAbility(10, 18, Abilities.ARREST.getId()));
        abilityMap.add(new MetaArtifactAbility(10, 17, Abilities.CONTRACT.getId()));
        abilityMap.add(new MetaArtifactAbility(10, 17, Abilities.BERSERK_RAGE.getId()));
        abilityMap.add(new MetaArtifactAbility(10, 17, Abilities.TYRANT_CALAMITY.getId()));
        abilityMap.add(new MetaArtifactAbility(10, 16, Abilities.AMPLIFICATION.getId()));
        abilityMap.add(new MetaArtifactAbility(10, 16, Abilities.PROHIBITION.getId()));
        abilityMap.add(new MetaArtifactAbility(10, 16, Abilities.RULE_PYLON.getId()));
//        abilityMap.put("water_affinity", new MetaAbilityEntry(10, 20, WaterAffinityAbility::new));
        abilityMap.add(new MetaArtifactAbility(20, 30, Abilities.AIR_BULLET.getId()));

        //abilities for gems and amulets
//        abilityMap.put("lucky_trend", new MetaArtifactAbility(-1, -1, level -> Abilities.LUCK_TREND.create(level%10)));

        downsideMap.add(new MetaArtifactAbility(0, 50, Abilities.NOISES_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(0, 50, Abilities.SLOWNESS_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(0, 50, Abilities.DUMMY_DOWNSIDE.getId()));

        downsideMap.add(new MetaArtifactAbility(7, 10, Abilities.CHAOS_LUCK_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(0, 8, Abilities.COOLDOWN_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(0, 8, Abilities.FAKE_LAG_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(0, 7, Abilities.FATE_CAST_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(0, 8, Abilities.LUCK_CONSUME_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(0, 10, Abilities.LUCK_TREND_DOWNWARDS_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(0, 7, Abilities.RANDOM_VELOCITY_DOWNSIDE.getId()));

        downsideMap.add(new MetaArtifactAbility(16, 20, Abilities.WATER_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(15, 19, Abilities.AURA_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(15, 19, Abilities.AXIS_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(15, 18, Abilities.MIST_DOWNSIDE.getId()));
        downsideMap.add(new MetaArtifactAbility(15, 17, Abilities.CALAMITY_DOWNSIDE.getId()));

//        effectMap.put("silk", new MetaEffectEntry(0, 8, dur -> BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_SILK_TOUCH, 8, 0, dur, true)));
//        effectMap.put("water_affinity", new MetaEffectEntry(10, 9, dur -> BeyonderEffects.byId(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, 9, 5, dur, true)));
//        effectMap.put("life_sap", new MetaEffectEntry(20, 9, dur -> BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_REGEN, 9, 0, dur, true)));
    }

    public static BeyonderEffect getEffectFromCharm(ItemStack charm){
        if(!charm.is(ModItems.CHARM.get()) || !hasTag(TAGS.CHARM, charm)) return null;
        CompoundTag charmTag = getTagFromItem(TAGS.CHARM, charm);
        String effId = getEffectId(charmTag);
        int duration = getDuration(charmTag);
        int level = getPathwaySequenceId(charmTag)%10;
        return BeyonderEffects.byId(effId, level, 0, duration, true);
    }

    public static int getPathwayIdFromCharm(ItemStack charm){
        if(!charm.is(ModItems.CHARM.get()) || !hasTag(TAGS.CHARM, charm)) return -1;
        return Math.floorDiv(getPathwaySequenceId(getTagFromItem(TAGS.CHARM, charm)), 10);
    }

    //artifact without beyonder info or downsides. single use
    public static ItemStack makeCharm(ItemStack stack, String beyonderEffectId, int pathwaySequenceId, int duration){
        if(!stack.is(ModItems.CHARM.get()) || beyonderEffectId.isEmpty() || hasTag(TAGS.CHARM, stack) ) return stack;

        ModNbtUtils.setItemRootTag(stack, createTag(beyonderEffectId, duration, pathwaySequenceId), TAGS.CHARM);
        return stack;
    }

//    //artifact with only passive and free abilities and no downsides and no beyonder tag
//    public static void makeAmuletGem(ItemStack gemStack, String ablId, int pathwaySequenceId, int color){
//        CompoundTag ogTag = gemStack.getOrCreateTag();
//        if(ogTag.contains(ARTIFACT_TAG_ID) || ogTag.contains(GEM_TAG_ID)) return;
//        if(!gemStack.is(ModItems.GEM.get())) return;
//
//        CompoundTag gemTag = new CompoundTag();
//        ListTag abilitiesList = new ListTag();
//        abilitiesList.add(makeAbilityTag(ablId, pathwaySequenceId));
//        gemTag.put("abilities", abilitiesList);
//        gemTag.putInt("color", color);
//        ogTag.put(GEM_TAG_ID, gemTag);
//        gemStack.setTag(ogTag);
//    }

    public static ItemStack generateMysticalItem(ItemStack stack, Ability ability, float useSeconds){
        ModNbtUtils.ArtifactInfoTag.generateArtifactTag(stack, UUID.randomUUID(), List.of(ability), true, useSeconds);
        return stack;
    }

    public static boolean isChargeableArtifact(ItemStack stack){
        return isArtifact(stack) && ModNbtUtils.ArtifactInfoTag.doesArtifactNeedCharge(stack);
    }

    public static ItemStack chargeArtifact(ItemStack toCharge, float spir, Player player) {
        ArtifactHolder artifact = ArtifactInfoTag.getArtifactHolderFromItem(toCharge);
        artifact.charge(3*spir);
        saveArtifactToItem(artifact, toCharge);
        return ModNbtUtils.MysticismTag.updateOrApplyInfluenceOnItem(toCharge, spir, player);
    }

    public static void generateSealedArtifact(ItemStack stack, int pathwaySequenceId, RandomSource random){
        generateSealedArtifact(stack, List.of(pathwaySequenceId), random);
    }
    public static void generateSealedArtifact(ItemStack stack, List<Integer> pathwaySequenceId, RandomSource random){
        // quantity is 1 for sequence levels 9-7, its 2 for levels 6-4, 3 for 3 and 2, and 4 for 1 and 0
        // commented out bc we dont have enough abilities to avoid the issue of not having enough. if it asks for 4 but theres only 2 availabe, thats a problem
        //int quantity = (int) Math.floor(-0.375f * sequenceLevel + 4.375f);
        //TODO: once you have all downsides, uncomment this
        int quantity = 1;

//        CompoundTag beyonderInfo = new CompoundTag();
//        beyonderInfo.putInt("id", pathwaySequenceId);
//        root.put(BEYONDER_TAG_ID, beyonderInfo);

        //create beyonder tag
        CompoundTag beyonderTag = ModNbtUtils.BeyonderInfoTag.setTagForItem(stack, pathwaySequenceId);
        //create artifact tag
        generateAbilityTag(stack, ModNbtUtils.BeyonderInfoTag.getAssociatedPathSeqLevel(beyonderTag), random, quantity);
    }

    private static CompoundTag generateAbilityTag(ItemStack stack, int pathwaySequenceId, RandomSource random, int quantity) {
        //TODO: make this take in all the characteristics and pick abilities based on that
        int level = pathwaySequenceId%10;
        List<ResourceLocation> abilities = new ArrayList<>();
        for(int i = 0; i < quantity; i++){
            ResourceLocation downId = getRandomAbilityId(pathwaySequenceId, random, abilities, true);
            ResourceLocation ablId = getRandomAbilityId(pathwaySequenceId, random, abilities, false);
            if(downId != null) abilities.add(downId);
            if(ablId != null) abilities.add(ablId);
        }
        return ModNbtUtils.ArtifactInfoTag.generateArtifactTag(stack, abilities, level);
    }

    private static ResourceLocation getRandomAbilityId(int pathwaySequenceId, RandomSource random, List<ResourceLocation> dontRepeatAbilities, boolean downsides) {
        List<ResourceLocation> matching;
        if(downsides){
            matching = abilityMap.stream()
                    .filter(mAbl -> mAbl.isInRange(pathwaySequenceId))
                    .map(mAbl -> mAbl.ablId)
                    .filter(ablId -> !dontRepeatAbilities.contains(ablId)).toList();
        } else{
            matching = downsideMap.stream()
                    .filter(mAbl -> mAbl.isInRange(pathwaySequenceId))
                    .map(mAbl -> mAbl.ablId)
                    .filter(ablId -> !dontRepeatAbilities.contains(ablId)).toList();
        }

        if (matching.isEmpty()){
            System.out.println("No ability IDs match sequence: " + pathwaySequenceId);
            return null;
        }

        return matching.get(random.nextInt(matching.size()));
    }

    public static boolean isValidItemForArtifact(ItemStack stack){
        return !isArtifact(stack) && (stack.is(Tags.Items.TOOLS) || stack.is(ModItems.RING.get()) || stack.is(ModItems.CROWN.get())) && !stack.is(ModItems.CHARACTERISTIC.get());
    }

    public static boolean isArtifact(ItemStack stack){
        return hasTag(ModNbtUtils.TAGS.ARTIFACT, stack);
    }

    public static boolean isWorkingArtifact(ItemStack stack){
         return isArtifact(stack) && isArtifactCharged(getTagFromItem(TAGS.ARTIFACT, stack));
    }

    public static ArtifactHolder getArtifactFromItem(ItemStack itemStack) {
        if(!isArtifact(itemStack)) return null;
        CompoundTag artifactTag = getTagFromItem(TAGS.ARTIFACT, itemStack);
        return ArtifactHolder.loadFromTag(artifactTag).withStack(itemStack);
    }
    public static ArtifactHolder getWorkingArtifactFromItem(ItemStack itemStack) {
        if(!isWorkingArtifact(itemStack)) return null;
        CompoundTag artifactTag = getTagFromItem(TAGS.ARTIFACT, itemStack);
        return ArtifactHolder.loadFromTag(artifactTag).withStack(itemStack);
    }

    public static UUID getArtifactIdFromItem(ItemStack itemStack){
        return ModNbtUtils.ArtifactInfoTag.getArtifactId(itemStack);
    }

    public static void updateArtifactTagOnItem(ArtifactHolder artifactHolder, ItemStack itemStack) {
        if(!isArtifact(itemStack)) return;
        if(!ModNbtUtils.ArtifactInfoTag.getArtifactId(itemStack).equals(artifactHolder.getArtifactId())) return;
        ModNbtUtils.ArtifactInfoTag.saveArtifactToItem(artifactHolder, itemStack);
    }

//    /**
//     * should be called only if the itemstack in the artifact holder is a reference to the actual item stack instance
//     * @param artifactHolder
//     */
//    public static void updateArtifactTagOnItem(ArtifactHolder artifactHolder) {
//        updateArtifactTagOnItem(artifactHolder, artifactHolder.getStack());
//    }

    public static boolean isCharacteristic(ItemStack item) {
        return hasTag(TAGS.BEYONDER, item);
    }

//    public static ArtifactHolder getArtifactIdsFromItem(ItemStack stack){
//        if(!stack.hasTag() || !stack.getTag().contains(ARTIFACT_TAG_ID)) return new ArrayList<>();
////        if(stack.is(ModItems.AMULET.get())) return new ArrayList<>();
//        if(stack.getTag().getCompound(ARTIFACT_TAG_ID).contains("enabled")){
//            if(!stack.getTag().getCompound(ARTIFACT_TAG_ID).getBoolean("enabled")) return new ArrayList<>();
//        }
//        ArrayList<String> abilityIds = new ArrayList<>();
//        CompoundTag artifactTag = stack.getTag().getCompound(ARTIFACT_TAG_ID);
//        ListTag abilities = artifactTag.getList("abilities", ListTag.TAG_COMPOUND);
//        ListTag downsides = artifactTag.getList("downsides", ListTag.TAG_COMPOUND);
//        int biggestCd = 0;
//        for(Tag tag: abilities){
//            if(tag instanceof CompoundTag ablTag){
//                String ablId = ablTag.getString("ablId");
//                int ablSequence = ablTag.getInt("sequence");
//                abilityIds.add((ablSequence < 10 ? "0" + ablSequence : ablSequence) + "_" + ablId);
////                biggestCd = Math.max(biggestCd, getAbilityFromId(ablId, ablSequence).getCooldownPercent());
//                //System.out.println("Biggest CD: " + biggestCd);
//            }
//        }
//        for(Tag tag: downsides){
//            if(tag instanceof CompoundTag ablTag){
//                String downId = ablTag.getString("ablId");
//                int downSequence = ablTag.getInt("sequence");
//                abilityIds.add((downSequence < 10 ? "0" + downSequence : downSequence) + "_" + downId + "_" + 20);
//
//            }
//        }
//        //System.out.println("Read artifact data as: " + abilityIds);
//        return abilityIds;
//    }

    record MetaArtifactAbility(int minSequence, int maxSequence, ResourceLocation ablId) {

        boolean isInRange(int pathwaySequenceId) {
            return pathwaySequenceId >= minSequence && pathwaySequenceId < maxSequence;
        }
    }
}
