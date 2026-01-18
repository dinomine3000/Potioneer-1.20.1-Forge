package net.dinomine.potioneer.util.misc;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.abilities.ArtifactHolder;
import net.dinomine.potioneer.beyonder.abilities.mystery.AirBulletAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.CraftingGuiAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.MeltAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.MinerLightAbility;
import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.downsides.DummyDownside;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

import java.util.*;
import java.util.function.Function;

public class MysticalItemHelper {
    public static final String ARTIFACT_TAG_ID = "artifact_info";
    public static final String GEM_TAG_ID = "gem_ability_info";
    public static final String BEYONDER_TAG_ID = "beyonder_info";
    public static final String CHARM_TAG_ID = "mystical_charm_info";

    private static final List<MetaArtifactAbility> abilityMap = new ArrayList<>();
    private static final List<MetaArtifactAbility> downsideMap = new ArrayList<>();

    static {
        //define minimum and maximum sequences here to define what levels can generate the ability
        //min sequence is inclusive, max sequence is exclusive
        abilityMap.add(new MetaArtifactAbility(8, 10, Abilities.MINER_LIGHT.getAblId()));
        abilityMap.add(new MetaArtifactAbility(5, 10, Abilities.MINING_SPEED.getAblId()));
        abilityMap.add(new MetaArtifactAbility(10, 20, Abilities.WATER_AFFINITY.getAblId()));
//        abilityMap.put("water_affinity", new MetaAbilityEntry(10, 20, WaterAffinityAbility::new));
        abilityMap.add(new MetaArtifactAbility(20, 30, Abilities.AIR_BULLET.getAblId()));
        abilityMap.add(new MetaArtifactAbility(30, 40, Abilities.MELT_ABILITY.getAblId()));
        abilityMap.add(new MetaArtifactAbility(40, 50, Abilities.CRAFTING_GUI.getAblId()));

        //abilities for gems and amulets
//        abilityMap.put("lucky_trend", new MetaArtifactAbility(-1, -1, level -> Abilities.LUCK_TREND.create(level%10)));

        downsideMap.add(new MetaArtifactAbility(0, 50, Abilities.DUMMY_DOWNSIDE.getAblId()));

//        effectMap.put("silk", new MetaEffectEntry(0, 8, dur -> BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_SILK_TOUCH, 8, 0, dur, true)));
//        effectMap.put("water_affinity", new MetaEffectEntry(10, 9, dur -> BeyonderEffects.byId(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, 9, 5, dur, true)));
//        effectMap.put("life_sap", new MetaEffectEntry(20, 9, dur -> BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_REGEN, 9, 0, dur, true)));
    }

    public static BeyonderEffect getEffectFromCharm(ItemStack charm){
        if(!charm.is(ModItems.CHARM.get()) || !charm.hasTag() || !charm.getTag().contains(CHARM_TAG_ID)) return null;
        String effId = charm.getTag().getCompound(CHARM_TAG_ID).getString("effectId");
        int duration = charm.getTag().getCompound(CHARM_TAG_ID).getInt("duration");
        int level = charm.getTag().getCompound(CHARM_TAG_ID).getInt("pathwaySequenceId")%10;
        return BeyonderEffects.byId(effId, level, 0, duration, true);
    }

    public static int getPathwayIdFromCharm(ItemStack charm){
        if(!charm.is(ModItems.CHARM.get()) || !charm.hasTag() || !charm.getTag().contains(CHARM_TAG_ID)) return -1;
        return Math.floorDiv(charm.getTag().getCompound(CHARM_TAG_ID).getInt("pathwaySequenceId"), 10);
    }

    //artifact without beyonder info or downsides. single use
    public static void makeCharm(ItemStack stack, String beyonderEffectId, int pathwaySequenceId, int duration){
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(CHARM_TAG_ID) || !stack.is(ModItems.CHARM.get()) || beyonderEffectId.isEmpty()) return;

        CompoundTag charmTag = new CompoundTag();
        charmTag.putString("effectId", beyonderEffectId);
        charmTag.putInt("duration", duration);
        charmTag.putInt("pathwaySequenceId", pathwaySequenceId);
        tag.put(CHARM_TAG_ID, charmTag);
    }

//    //artifact with only passive and free abilities and no downsides and no beyonder tag
//    public static void makeAmuletGem(ItemStack gemStack, String ablId, int pathwayId, int color){
//        CompoundTag ogTag = gemStack.getOrCreateTag();
//        if(ogTag.contains(ARTIFACT_TAG_ID) || ogTag.contains(GEM_TAG_ID)) return;
//        if(!gemStack.is(ModItems.GEM.get())) return;
//
//        CompoundTag gemTag = new CompoundTag();
//        ListTag abilitiesList = new ListTag();
//        abilitiesList.add(makeAbilityTag(ablId, pathwayId));
//        gemTag.put("abilities", abilitiesList);
//        gemTag.putInt("color", color);
//        ogTag.put(GEM_TAG_ID, gemTag);
//        gemStack.setTag(ogTag);
//    }

    //artifact weapons without downsides and without beyonder tag
//    public static void makeBeyonderWeapon(){}

    public static void generateSealedArtifact(ItemStack stack, int pathwaySequenceId, RandomSource random){
        if(!isValidItemForArtifact(stack)) return;
        CompoundTag root = stack.getOrCreateTag();
        // quantity is 1 for sequence levels 9-7, its 2 for levels 6-4, 3 for 3 and 2, and 4 for 1 and 0
        // commented out bc we dont have enough abilities to avoid the issue of not having enough. if it asks for 4 but theres only 2 availabe, thats a problem
        //int quantity = (int) Math.floor(-0.375f * sequenceLevel + 4.375f);
        int quantity = 1;

        CompoundTag beyonderInfo = new CompoundTag();
        beyonderInfo.putInt("id", pathwaySequenceId);
        root.put(BEYONDER_TAG_ID, beyonderInfo);

        CompoundTag artifactInfo = new CompoundTag();
        artifactInfo.putUUID("artifactId", UUID.randomUUID());
        generateAbilityTags(artifactInfo, pathwaySequenceId, random, quantity);
        root.put(ARTIFACT_TAG_ID, artifactInfo);
        stack.setTag(root);
    }

    private static CompoundTag generateAbilityTags(CompoundTag tag, int pathwaySequenceId, RandomSource random, int quantity) {
        int level = pathwaySequenceId%10;
        List<String> abilities = new ArrayList<>();
        for(int i = 0; i < quantity; i++){
            String downId = getRandomAbilityId(pathwaySequenceId, random, abilities, true);
            String ablId = getRandomAbilityId(pathwaySequenceId, random, abilities, false);
            if(!downId.isEmpty()) abilities.add(downId);
            if(!ablId.isEmpty()) abilities.add(ablId);
        }
        for(String ablId: abilities){
            tag.put((new AbilityKey(ablId, level)).toString(), new CompoundTag());
        }
        return tag;
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
        return stack.hasTag() && stack.getTag().contains(ARTIFACT_TAG_ID);
    }

    public static boolean isArtifactEnabled(ItemStack stack){
        if(!stack.hasTag() || !stack.getTag().contains(ARTIFACT_TAG_ID)) return false;
        CompoundTag artifactTag = stack.getTag().getCompound(ARTIFACT_TAG_ID);
        for(String key: artifactTag.getAllKeys()){
            if(key.equals("artifactId")) continue;
            if(!artifactTag.getCompound(key).getBoolean("enabled")) continue;
            return true;
        }
        return false;
    }

    public static ArtifactHolder getArtifactFromitem(ItemStack itemStack) {
        if(!isWorkingArtifact(itemStack)) return null;
        CompoundTag artifactTag = itemStack.getTag().getCompound(ARTIFACT_TAG_ID);
        return ArtifactHolder.loadFromTag(artifactTag);
    }

    public static UUID getArtifactIdFromItem(ItemStack itemStack){
        if(!isWorkingArtifact(itemStack)) return null;
        CompoundTag artifactTag = itemStack.getTag().getCompound(ARTIFACT_TAG_ID);
        return artifactTag.getUUID("artifactId");
    }

    public static void updateArtifactTagOnItem(ArtifactHolder artifactHolder, ItemStack itemStack) {
        if(!isWorkingArtifact(itemStack)) return;
        CompoundTag root = itemStack.getOrCreateTag();
        if(!root.getCompound(ARTIFACT_TAG_ID).getUUID("artifactId").equals(artifactHolder.getArtifactId())) return;
        CompoundTag artifactTag = artifactHolder.saveToTag(new CompoundTag());
        root.put(ARTIFACT_TAG_ID, artifactTag);
        itemStack.setTag(root);
    }

    public static boolean isCharacteristic(ItemStack item) {
        return item.hasTag() && item.getTag().contains(BEYONDER_TAG_ID);
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
