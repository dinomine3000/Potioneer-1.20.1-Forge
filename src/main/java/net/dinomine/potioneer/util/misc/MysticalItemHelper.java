package net.dinomine.potioneer.util.misc;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.dinomine.potioneer.util.misc.ModNbtUtils.*;
import static net.dinomine.potioneer.util.misc.ModNbtUtils.ArtifactInfoTag.generateArtifactTag;
import static net.dinomine.potioneer.util.misc.ModNbtUtils.CharmInfoTag.*;

public class MysticalItemHelper {
    public static final String GEM_TAG_ID = "gem_ability_info";

    private static final List<MetaArtifactAbility> abilityMap = new ArrayList<>();
    private static final List<MetaArtifactAbility> downsideMap = new ArrayList<>();

    static {
        //define minimum and maximum sequences here to define what levels can generate the ability
        //min sequence is inclusive, max sequence is exclusive
        abilityMap.add(new MetaArtifactAbility(8, 10, Abilities.MINER_LIGHT.getAblId()));
        abilityMap.add(new MetaArtifactAbility(0, 7, Abilities.HALF_COOLDOWN.getAblId()));
        abilityMap.add(new MetaArtifactAbility(0, 7, Abilities.FATE.getAblId()));
        abilityMap.add(new MetaArtifactAbility(0, 9, Abilities.APPRAISAL.getAblId()));
        abilityMap.add(new MetaArtifactAbility(0, 9, Abilities.TARGET_APPRAISAL.getAblId()));
        abilityMap.add(new MetaArtifactAbility(0, 8, Abilities.GAMBLING.getAblId()));
        abilityMap.add(new MetaArtifactAbility(0, 9, Abilities.BLOCK_APPRAISAL.getAblId()));
        abilityMap.add(new MetaArtifactAbility(0, 10, Abilities.MINING_SPEED.getAblId()));
        abilityMap.add(new MetaArtifactAbility(0, 10, Abilities.VOID_VISION.getAblId()));
        abilityMap.add(new MetaArtifactAbility(18, 20, Abilities.WATER_AFFINITY.getAblId()));
        abilityMap.add(new MetaArtifactAbility(17, 20, Abilities.WATER_SCALES.getAblId()));
        abilityMap.add(new MetaArtifactAbility(10, 19, Abilities.TYRANT_WATER_SPELLS.getAblId()));
        abilityMap.add(new MetaArtifactAbility(17, 19, Abilities.TYRANT_DIVINATION.getAblId()));
        abilityMap.add(new MetaArtifactAbility(10, 18, Abilities.ARREST.getAblId()));
        abilityMap.add(new MetaArtifactAbility(10, 17, Abilities.CONTRACT.getAblId()));
        abilityMap.add(new MetaArtifactAbility(10, 17, Abilities.BERSERK_RAGE.getAblId()));
        abilityMap.add(new MetaArtifactAbility(10, 17, Abilities.TYRANT_CALAMITY.getAblId()));
        abilityMap.add(new MetaArtifactAbility(10, 16, Abilities.AMPLIFICATION.getAblId()));
        abilityMap.add(new MetaArtifactAbility(10, 16, Abilities.PROHIBITION.getAblId()));
        abilityMap.add(new MetaArtifactAbility(10, 16, Abilities.RULE_PYLON.getAblId()));
//        abilityMap.put("water_affinity", new MetaAbilityEntry(10, 20, WaterAffinityAbility::new));
        abilityMap.add(new MetaArtifactAbility(20, 30, Abilities.AIR_BULLET.getAblId()));
        abilityMap.add(new MetaArtifactAbility(30, 40, Abilities.MELT_ABILITY.getAblId()));
        abilityMap.add(new MetaArtifactAbility(40, 50, Abilities.CRAFTING_GUI.getAblId()));

        //abilities for gems and amulets
//        abilityMap.put("lucky_trend", new MetaArtifactAbility(-1, -1, level -> Abilities.LUCK_TREND.create(level%10)));

        downsideMap.add(new MetaArtifactAbility(0, 50, Abilities.NOISES_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(0, 50, Abilities.SLOWNESS_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(0, 50, Abilities.DUMMY_DOWNSIDE.getAblId()));

        downsideMap.add(new MetaArtifactAbility(7, 10, Abilities.CHAOS_LUCK_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(0, 8, Abilities.COOLDOWN_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(0, 8, Abilities.FAKE_LAG_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(0, 7, Abilities.FATE_CAST_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(0, 8, Abilities.LUCK_CONSUME_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(0, 10, Abilities.LUCK_TREND_DOWNWARDS_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(0, 7, Abilities.RANDOM_VELOCITY_DOWNSIDE.getAblId()));

        downsideMap.add(new MetaArtifactAbility(16, 20, Abilities.WATER_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(15, 19, Abilities.AURA_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(15, 19, Abilities.AXIS_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(15, 18, Abilities.MIST_DOWNSIDE.getAblId()));
        downsideMap.add(new MetaArtifactAbility(15, 17, Abilities.CALAMITY_DOWNSIDE.getAblId()));

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

    //artifact weapons without downsides and without beyonder tag
//    public static void makeBeyonderWeapon(){}

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
        List<String> abilities = new ArrayList<>();
        for(int i = 0; i < quantity; i++){
            String downId = getRandomAbilityId(pathwaySequenceId, random, abilities, true);
            String ablId = getRandomAbilityId(pathwaySequenceId, random, abilities, false);
            if(!downId.isEmpty()) abilities.add(downId);
            if(!ablId.isEmpty()) abilities.add(ablId);
        }
        return generateArtifactTag(stack, abilities, level);
    }

    private static String getRandomAbilityId(int pathwaySequenceId, RandomSource random, List<String> dontRepeatAbilities, boolean downsides) {
        List<String> matching;
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
            return "";
        }

        return matching.get(random.nextInt(matching.size()));
    }

    public static boolean isValidItemForArtifact(ItemStack stack){
        return !isWorkingArtifact(stack) && (stack.is(Tags.Items.TOOLS) || stack.is(ModItems.RING.get()) || stack.is(ModItems.CROWN.get())) && !stack.is(ModItems.CHARACTERISTIC.get());
    }

    public static boolean isWorkingArtifact(ItemStack stack){
        return hasTag(ModNbtUtils.TAGS.ARTIFACT, stack);
    }

    public static ArtifactHolder getArtifactFromItem(ItemStack itemStack) {
        if(!isWorkingArtifact(itemStack)) return null;
        CompoundTag artifactTag = getTagFromItem(TAGS.ARTIFACT, itemStack);
        return ArtifactHolder.loadFromTag(artifactTag).withStack(itemStack);
    }

    public static UUID getArtifactIdFromItem(ItemStack itemStack){
        return ModNbtUtils.ArtifactInfoTag.getArtifactId(itemStack);
    }

    public static void updateArtifactTagOnItem(ArtifactHolder artifactHolder, ItemStack itemStack) {
        if(!isWorkingArtifact(itemStack)) return;
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

    record MetaArtifactAbility(int minSequence, int maxSequence, String ablId) {

        boolean isInRange(int pathwaySequenceId) {
            return pathwaySequenceId >= minSequence && pathwaySequenceId < maxSequence;
        }
    }
}
