package net.dinomine.potioneer.util.misc;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.recipe.PotionContentData;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class ModNbtUtils {

    public static final String PURIFYING_TAG = "purifying";

    private static final String BEYONDER_TAG_ID = "beyonder_info";
    private static final String ARTIFACT_TAG_ID = "artifact_info";
    private static final String POTION_TAG_ID = "potion_info";
    private static final String CHARM_TAG_ID = "mystical_charm_info";
    private static final String MYSTICISM_TAG_ID = "mysticism_info";

    public enum TAGS{
        ARTIFACT(ARTIFACT_TAG_ID),
        BEYONDER(BEYONDER_TAG_ID),
        POTION(POTION_TAG_ID),
        MYSTICISM(MYSTICISM_TAG_ID),
        CHARM(CHARM_TAG_ID);

        private final String tagId;

        TAGS(String tagId) {
            this.tagId = tagId;
        }
        public String getTagId() {
            return this.tagId;
        }
    }

    public static CompoundTag setItemRootTag(ItemStack item, @Nullable CompoundTag tag, String tagKeyId){
        CompoundTag root = item.getOrCreateTag();
        if(tag == null) return root;
        root.put(tagKeyId, tag);
        item.setTag(root);
        return root;
    }

    public static CompoundTag setItemRootTag(ItemStack item, @Nullable CompoundTag tag, TAGS tagKey){
        return setItemRootTag(item, tag, tagKey.tagId);
    }

    public static boolean hasTag(TAGS tag, ItemStack item){
        return hasTag(tag.tagId, item);
    }


    public static boolean hasTag(String tagId, ItemStack item){
        return !item.isEmpty() && item.hasTag() && item.getTag().contains(tagId);
    }

    @Nullable
    public static CompoundTag getTagFromItem(String tagId, ItemStack item){
        if (hasTag(tagId, item)) {
            assert item.getTag() != null;
            return item.getTag().getCompound(tagId);
        }
        return null;
    }

    @Nullable
    public static CompoundTag getTagFromItem(TAGS tag, ItemStack item){
        return getTagFromItem(tag.tagId, item);
    }

    public static <T extends Number> ListTag toNumberListTag(List<T> array) {
        ListTag list = new ListTag();
        for (T f : array) {
            if(f instanceof Double dVal){
                list.add(DoubleTag.valueOf(dVal));
            } else if(f instanceof Integer iVal){
                list.add(IntTag.valueOf(iVal));
            }
        }
        return list;
    }

    public static ArrayList<Double> fromDoubleListTag(ListTag list) {
        ArrayList<Double> result = new ArrayList<>();
        for (Tag tag : list) {
            result.add(((DoubleTag) tag).getAsDouble());
        }
        return result;
    }

    public static ArrayList<Integer> fromIntListTag(ListTag list) {
        ArrayList<Integer> result = new ArrayList<>();
        for (Tag tag : list) {
            result.add(((IntTag) tag).getAsInt());
        }
        return result;
    }

    public static void writeStringList(CompoundTag parentTag, String key, List<String> stringList) {
        ListTag listTag = new ListTag();

        for (String str : stringList) {
            listTag.add(StringTag.valueOf(str));
        }
        parentTag.put(key, listTag);
    }

    public static List<String> readStringList(CompoundTag parentTag, String key) {
        List<String> result = new ArrayList<>();

        if (parentTag.contains(key, Tag.TAG_LIST)) {
            ListTag listTag = parentTag.getList(key, Tag.TAG_STRING);

            for (int i = 0; i < listTag.size(); i++) {
                String value = listTag.getString(i);
                result.add(value);
            }
        }
        return result;
    }
    /**
     * Tag that holds characteristic information. any item with this will be seen as a characteristic.
     * "beyonder_info": {
     *     "charIds":[19, 28, 29, 18, 17]
     * }
     */
    public static class BeyonderInfoTag{
        private static final String listKey = "charIds";

        public static void setTagForItem(ItemStack stack, int pathSeqId){
            setTagForItem(stack, List.of(pathSeqId));
        }

        public static CompoundTag setTagForItem(ItemStack stack, List<Integer> ids){
            CompoundTag beyonderTag = createTagForIds(ids);
            setItemRootTag(stack, beyonderTag, BEYONDER_TAG_ID);
            return beyonderTag;
        }

        private static CompoundTag createTagForIds(List<Integer> ids){
            CompoundTag tag = new CompoundTag();
            tag.putIntArray(listKey, ids);
            return tag;
        }
        public static List<Integer> getCharIds(CompoundTag tag){
            int[] array = tag.getIntArray(listKey);
            return Arrays.stream(array).boxed().toList();
        }

        public static boolean isCharacteristic(ItemStack stack){
            return hasTag(TAGS.BEYONDER, stack);
        }

        public static int getAssociatedPathSeqLevel(CompoundTag tag){
            List<Integer> bestIds = CharacteristicHelper.closestToLowerTens(getCharIds(tag)).stream().sorted().toList();
            if(bestIds.isEmpty()) return -1;
            int match = bestIds.get(0);
            for(int charId: bestIds){
                if(charId % 10 < match % 10) match = charId;
            }
            return match;
        }

/*
        public static CompoundTag removeId(int id, CompoundTag tag){
            List<Integer> charIds = getCharIds(tag);
            if(charIds.contains(id)){
                charIds.remove((Object) id);
            }
            return setTagIds(charIds, tag);
        }

        public static CompoundTag addId(int id, CompoundTag tag){
            List<Integer> charIds = getCharIds(tag);
            if(!charIds.contains(id)){
                charIds.add(id);
                return setTagIds(charIds, tag);
            } else return tag;
        }*/

        public static boolean containsId(int id, CompoundTag tag){
            return getCharIds(tag).contains(id);
        }

        public static boolean isOfSamePathway(int pathwayId, CompoundTag tag){
            return getCharIds(tag).stream().map(id -> Math.floorDiv(id, 10)).anyMatch(id -> id == pathwayId);
        }
    }

    /**
     * Tag that hold artifact information. logically, it should also come with a BeyonderInfoTag attached,
     * but i might do beyonder weapons with simple abilities that will likely just have this
     */
    public static class ArtifactInfoTag{
        private static final String UUID_KEY = "artifactId";
        private static final String STACK_KEY = "itemStack";
        private static final String NEED_CHARGE_KEY = "needsCharge";
        private static final String CHARGE_KEY = "charge";

        public static CompoundTag generateArtifactTag(ItemStack stack, List<String> abilityIds, int sequenceLevel){
            return generateArtifactTag(stack, abilityIds.stream().map(id -> new AbilityKey(id, sequenceLevel)).toList());
        }

        /**
         * generates a brand new artifact tag with the given abilities.
         * @param abilityKeys
         * @return
         */
        public static CompoundTag generateChargedArtifactTag(ItemStack stack, List<AbilityKey> abilityKeys, float chargeSeconds){
            return generateArtifactTag(stack, UUID.randomUUID(), abilityKeys, true, chargeSeconds);
        }
        public static CompoundTag generateArtifactTag(ItemStack stack, UUID artifactId, List<AbilityKey> abilityKeys, boolean needsCharge, float useSeconds){
            CompoundTag resArtifactTag = new CompoundTag();
            resArtifactTag.putUUID(UUID_KEY, artifactId);
            for(AbilityKey abl: abilityKeys){
                resArtifactTag.put(abl.onArtifact(artifactId).toString(), new CompoundTag());
            }
            resArtifactTag.putBoolean(NEED_CHARGE_KEY, needsCharge);
            resArtifactTag.putFloat(CHARGE_KEY, useSeconds);
            setItemRootTag(stack, resArtifactTag, ARTIFACT_TAG_ID);
            return resArtifactTag;
        }
        /**
         * generates a brand new artifact tag with the given abilities.
         * @param abilityKeys
         * @return
         */
        public static CompoundTag generateArtifactTag(ItemStack stack, List<AbilityKey> abilityKeys){
            return generateArtifactTag(stack, UUID.randomUUID(), abilityKeys);
        }
        public static CompoundTag generateArtifactTag(ItemStack stack, UUID artifactId, List<AbilityKey> abilityKeys){
            return generateArtifactTag(stack, artifactId, abilityKeys, false, -1);
        }

        public static void saveArtifactToItem(ArtifactHolder artifact, ItemStack stack){
            CompoundTag artifactTag = getTagFromArtifactHolder(artifact.withStack(stack), false);
            setItemRootTag(stack, artifactTag, ARTIFACT_TAG_ID);
        }

        /**
         *
         * @param stack
         * @return true if any non-downside abilities are enabled
         */
        public static boolean isArtifactEnabled(ItemStack stack){
            if(!hasTag(ARTIFACT_TAG_ID, stack)) return false;
            CompoundTag artifactTag = ModNbtUtils.getTagFromItem(ARTIFACT_TAG_ID, stack);
            for(String key: artifactTag.getAllKeys()){
                if(key.equals(UUID_KEY)) continue;
                if(artifactTag.getCompound(key).getBoolean("downside")) continue;
                if(!artifactTag.getCompound(key).getBoolean("enabled")) continue;
                return true;
            }
            return false;
        }

        public static UUID getArtifactId(CompoundTag tag){
            if(tag == null) return null;
            return tag.getUUID(UUID_KEY);
        }

        public static UUID getArtifactId(ItemStack stack){
            return getArtifactId(getTagFromItem(ARTIFACT_TAG_ID, stack));
        }

        public static CompoundTag getArtifactTagFromItem(ItemStack stack){
            return getTagFromItem(ARTIFACT_TAG_ID, stack);
        }

        public static ArtifactHolder getArtifactHolderFromItem(ItemStack stack){
            return getArtifactHolderFromTag(getTagFromItem(ARTIFACT_TAG_ID, stack));
        }
        public static boolean isArtifactCharged(ItemStack stack){
            if(!hasTag(TAGS.ARTIFACT, stack)) return false;
            return isArtifactCharged(getTagFromItem(TAGS.ARTIFACT, stack));
        }

        public static boolean isArtifactCharged(CompoundTag artifactTag){
            if(artifactTag == null) return false;
            return !artifactTag.getBoolean(NEED_CHARGE_KEY) || artifactTag.getFloat(CHARGE_KEY) > 0;
        }

        public static boolean doesArtifactNeedCharge(ItemStack stack){
            return hasTag(TAGS.ARTIFACT, stack) && getTagFromItem(TAGS.ARTIFACT, stack).getBoolean(NEED_CHARGE_KEY);
        }

        public static float getArtifactCharge(ItemStack stack){
            return hasTag(TAGS.ARTIFACT, stack) ? getTagFromItem(TAGS.ARTIFACT, stack).getFloat(CHARGE_KEY) : 0f;
        }

        public static ArtifactHolder getArtifactHolderFromTag(CompoundTag artifactTag){
            if(artifactTag == null) return null;

            UUID artifactId = getArtifactId(artifactTag);
            List<Ability> abilities = new ArrayList<>();
            for(String stringKey: artifactTag.getAllKeys()){
                if(stringKey.equals(UUID_KEY)) continue;
                AbilityKey key = AbilityKey.fromString(stringKey);
                if(key.isEmpty()) continue;
                int savedLevel = artifactTag.getCompound(stringKey).contains("levelState") ? artifactTag.getCompound(stringKey).getInt("level") : key.getSequenceLevel();
                Ability ability = Abilities.createAbilityInstance(key, savedLevel);
                ability.setArtifactAbilityKey(artifactId);
                ability.loadNbt(artifactTag);
                abilities.add(ability);
            }
            ItemStack stack = ItemStack.EMPTY;
            if(artifactTag.contains(STACK_KEY)) stack = ItemStack.of(artifactTag.getCompound(STACK_KEY));
            if(artifactTag.getBoolean(NEED_CHARGE_KEY)){
                return new ArtifactHolder(abilities, artifactId, stack, artifactTag.getFloat(CHARGE_KEY));
            } else {
                return new ArtifactHolder(abilities, artifactId, stack);
            }
        }

        public static CompoundTag getTagFromArtifactHolder(ArtifactHolder artifact, boolean saveItem){
            CompoundTag artifactTag = new CompoundTag();
            artifactTag.putUUID(UUID_KEY, artifact.getArtifactId());
            for(Ability abl: artifact.abilities.values()){
                artifactTag.put(abl.getAbilityKey().toString(), abl.saveNbt());
            }
            for(Ability abl: artifact.downsides.values()){
                artifactTag.put(abl.getAbilityKey().toString(), abl.saveNbt());
            }
            if(saveItem) artifactTag.put(STACK_KEY, artifact.item.save(new CompoundTag()));
            artifactTag.putBoolean(NEED_CHARGE_KEY, artifact.needsCharge);
            artifactTag.putFloat(CHARGE_KEY, artifact.chargeSeconds);
            return artifactTag;
        }

        public static boolean isItemArtifact(ItemStack mainHandItem) {
            return hasTag(TAGS.ARTIFACT, mainHandItem);
        }
    }

    /**
     * tag that holds potion content data, including success
     * "potion_info"{
     *     "timestamp": time,
     *     "name": "19", / "name": "conflict"
     *     "isComplete": false, (means it was not made with the supplementary ingredients)
     *     "color": 0,
     *     "amount": 2
     *
     * }
     *
     */
    public static class PotionInfoTag{
        private static final String TIMESTAMP_KEY = "timestamp";
        private static final String NAME_KEY = "name";
        private static final String COMPLETE_KEY = "isComplete";
        private static final String COLOR_KEY = "color";
        private static final String AMOUNT_KEY = "amount";
        private static final String CONFLICT_VALUE = "conflict";
        private static final int ticksToSpoil = 5*60*20;

        public static final int MAX_VIAL_AMOUNT = 1;
        public static final int MAX_FLASK_AMOUNT = 2;

        public static ItemStack setTagForItem(PotionContentData data, ItemStack stack){
            CompoundTag potionInfo = new CompoundTag();

            //cap off amount
            int amount = Math.max(data.amount, 0);
            if(stack.is(ModItems.VIAL.get())) amount = Math.min(MAX_VIAL_AMOUNT, amount);
            if(stack.is(ModItems.FLASK.get())) amount = Math.min(MAX_FLASK_AMOUNT, amount);

            potionInfo.putInt(AMOUNT_KEY, amount);
            potionInfo.putString(NAME_KEY, data.name);
            potionInfo.putInt(COLOR_KEY, data.color);
            potionInfo.putBoolean(COMPLETE_KEY, data.isComplete);
            setItemRootTag(stack, potionInfo, POTION_TAG_ID);
            return stack;
        }

        public static ItemStack applyTagToItem(PotionContentData data, ItemStack stack){
            PotionContentData dataToSet = data.copy();
            if(hasTag(TAGS.POTION, stack)){
                CompoundTag info = getTagFromItem(TAGS.POTION, stack);
                //first, verify amount
                int existingLevel = getPotionAmount(info);
                data.amount = Math.max(getMaxItemAmount(stack), existingLevel + data.amount);
                //then verify theyre both complete
                boolean prevVal = isPotionComplete(info);
                data.isComplete &= prevVal;
            }
            return setTagForItem(dataToSet, stack);
        }

        public static int getMaxItemAmount(ItemStack stack){
            if(stack.is(ModItems.VIAL.get())) return MAX_VIAL_AMOUNT;
            if(stack.is(ModItems.FLASK.get())) return MAX_FLASK_AMOUNT;
            return 0;
        }

        public static void tickPotionSpoilTime(ItemStack potionItem, Long gameTime){
            if(!hasTag(TAGS.POTION, potionItem)) return;
            CompoundTag potionTag = getTagFromItem(TAGS.POTION, potionItem);
            if(!potionTag.contains(TIMESTAMP_KEY)) potionTag.putLong(TIMESTAMP_KEY, gameTime + ticksToSpoil);
            if(potionTag.getLong(TIMESTAMP_KEY) < gameTime) potionItem.setCount(0);
        }

        public static boolean isConflict(String name){return name.equalsIgnoreCase(CONFLICT_VALUE);}

        public static boolean isDrinkablePotion(CompoundTag potionTag){
            boolean validPotion = true;
            try {
                Integer.parseInt(potionTag.getString(NAME_KEY));
            } catch (Exception e){
                validPotion = isConflict(potionTag.getString(NAME_KEY));
            }
            return validPotion;
        }

        public static boolean isConflictingPotion(CompoundTag potionTag){
            return isConflict(potionTag.getString(NAME_KEY));
        }

        public static int getSequenceLevelOrThrow(CompoundTag potionTag){
            return Integer.parseInt(potionTag.getString(NAME_KEY));
        }

        public static boolean hasSequenceLevel(CompoundTag potionTag){
            try {
                Integer.parseInt(potionTag.getString(NAME_KEY));
                return true;
            } catch (Exception e){
                return false;
            }
        }

        public static boolean isPotionComplete(CompoundTag potionTag){
            return potionTag.contains(COMPLETE_KEY) ? potionTag.getBoolean(COMPLETE_KEY) : true;
        }

        public static String getPotionName(CompoundTag potionTag){
            return potionTag == null ? "" : potionTag.getString(NAME_KEY);
        }

        public static int getPotionColor(CompoundTag potionTag){
            return potionTag.getInt(COLOR_KEY);
        }
        public static int getPotionAmount(CompoundTag potionTag){
            if(potionTag == null || potionTag.isEmpty()) return 0;
            return potionTag.getInt(AMOUNT_KEY);
        }
        public static int getPotionAmount(ItemStack stack){
            CompoundTag potionTag = getTagFromItem(TAGS.POTION, stack);
            return getPotionAmount(potionTag);
        }

        public static CompoundTag setPotionAmount(CompoundTag potionTag, int newAmount){
            potionTag.putInt(AMOUNT_KEY, newAmount);
            return potionTag;
        }

        public static ItemStack convertStack(ItemStack potionStack){
            if(!potionStack.is(ModItems.VIAL.get()) || !potionStack.is(ModItems.FLASK.get())) return potionStack;

            //in both cases, we do the same thing: copy existing tags and put them on a flask.
            if(!hasTag(TAGS.POTION, potionStack) || potionStack.is(ModItems.VIAL.get())) {
                if(!potionStack.hasTag()) return doConversion(potionStack);

                CompoundTag tag = potionStack.getOrCreateTag();
                ItemStack res = doConversion(potionStack);
                res.setTag(tag.copy());
                return res;
            }

            //now only flasks with the potion tag remain
            CompoundTag potionTag = getTagFromItem(TAGS.POTION, potionStack);
            ItemStack res = doConversion(potionStack);
            res.setCount(getPotionAmount(potionTag));
            setItemRootTag(res, setPotionAmount(potionTag, 1), POTION_TAG_ID);
            return res;
        }

        private static ItemStack doConversion(ItemStack ogStack){ return new ItemStack(ogStack.is(ModItems.VIAL.get()) ? ModItems.FLASK.get() : ModItems.VIAL.get());}

        public static boolean sumAmountsLessThan(CompoundTag t1, CompoundTag t2, int max) {
            return getPotionAmount(t1) + getPotionAmount(t2) < max;
        }

        public static ItemStack sumContentsIntoFlask(ItemStack i0, ItemStack i1) {
            CompoundTag tag0 = getTagFromItem(TAGS.POTION, i0);
            CompoundTag tag1 = getTagFromItem(TAGS.POTION, i1);
            int sum = getPotionAmount(tag0) + getPotionAmount(tag1);
            if(sum > MAX_FLASK_AMOUNT) throw new IllegalArgumentException("[Potioneer] Given potion contents add up to more than " + MAX_FLASK_AMOUNT + " for a flask");

            CompoundTag finalPotionTag = tag0.copy();
            ItemStack resultingItem = new ItemStack(ModItems.FLASK.get());
            setItemRootTag(resultingItem, setPotionAmount(finalPotionTag, sum), POTION_TAG_ID);
            return resultingItem;
        }
    }

    /**
     * tag that holds charm info data
     * "mystical_charm_info":{
     *     "pathwaySequenceId" : 15,
     *     "effectId": "water_affinity",
     * }
     */
    public static class CharmInfoTag{
        private static final String PATH_KEY = "pathwaySequenceId";
        private static final String EFFECT_KEY = "effectId";
        private static final String DURATION_KEY = "duration";

        public static int getPathwaySequenceId(CompoundTag charmTag){return charmTag.getInt(PATH_KEY);}
        public static String getEffectId(CompoundTag charmTag){return charmTag.getString(EFFECT_KEY);}
        public static int getDuration(CompoundTag charmTag){return charmTag.getInt(DURATION_KEY);}

        public static CompoundTag createTag(String beyonderEffectId, int duration, int pathwaySequenceId) {
            CompoundTag res = new CompoundTag();
            res.putInt(PATH_KEY, pathwaySequenceId);
            res.putInt(DURATION_KEY, duration);
            res.putString(EFFECT_KEY, beyonderEffectId);
            return res;
        }
    }

    /**
     * tag that holds mystical information
     * "mysticism_info": {
     *      "spirituality":[
     *           "spirituality_0": 5,
     *           "spirituality_1": 25,
     *           "spirituality_2": 7
     *      ],
     *      "players":[
     *           "player_0": [UUID_HERE],
     *           "player_1": [UUID_HERE],
     *           "player_2": [UUID_HERE]
     *      ],
     *      "names":[
     *          "name_0": [NAME_HERE],
     *          "name_1": [NAME_HERE],
     *          "name_2": [NAME_HERE],
     *      ]
     * }
     */
    public static class MysticismTag{
        public static final String SPIRITUALITY_KEY = "spirituality";
        public static final String PLAYER_KEY = "players";
        public static final String NAME_KEY = "names";

        public static UUID getPlayerIdFromMysticalTag(CompoundTag mysticalTag, Level level, int toConsume){
            CompoundTag spirituality = mysticalTag.getCompound(SPIRITUALITY_KEY);
            CompoundTag ids = mysticalTag.getCompound(PLAYER_KEY);
            int i = 0;
            int bestIndex = 0;
            float bestSpirituality = -1;
            UUID bestId = UUID.randomUUID();
            while(spirituality.contains("spirituality_" + i)){
                float testSpirituality = spirituality.getFloat("spirituality_" + i);
                if(testSpirituality > bestSpirituality){
                    UUID id = ids.getUUID("player_" + i);
                    if(level == null || level.getPlayerByUUID(id) != null){
                        bestIndex = i;
                        bestSpirituality = testSpirituality;
                        bestId = id;
                    }
                }
                i++;
            }
            if(bestSpirituality != -1){
                if(bestSpirituality - toConsume <= 0){
                    spirituality.remove("spirituality_" + bestIndex);
                    ids.remove("player_" + bestIndex);
                } else {
                    spirituality.putFloat("spirituality_" + bestIndex, bestSpirituality - toConsume);
                }
                return bestId;
            }
            return null;
        }

        public static String getPlayerNameFromTag(CompoundTag mysticalTag){
            CompoundTag spirituality = mysticalTag.getCompound(SPIRITUALITY_KEY);
            CompoundTag names = mysticalTag.getCompound(NAME_KEY);
            int i = 0;
            int bestIndex = 0;
            float bestSpirituality = -1;
            String bestName = "";
            while(spirituality.contains("spirituality_" + i)){
                float testSpirituality = spirituality.getFloat("spirituality_" + i);
                if(testSpirituality > bestSpirituality){
                    bestName = names.getString("name_" + i);
                    bestIndex = i;
                    bestSpirituality = testSpirituality;
                }
                i++;
            }
            if(bestSpirituality != -1){
                return bestName;
            }
            return "";
        }
        public static ItemStack updateOrApplyInfluenceOnItem(ItemStack stack, float spiritualityAmount, Player player){
            if(stack.isEmpty()) return stack;
            CompoundTag tag = getTagFromItem(TAGS.MYSTICISM, stack);
            if(tag == null) tag = generateNewMysticismTag();
            setItemRootTag(stack, updateOrApplyTagInfluence(tag, spiritualityAmount, player), TAGS.MYSTICISM);
            return stack;
        }
        public static CompoundTag updateOrApplyTagInfluence(CompoundTag mystTag, float spiritualityAmount, Player player){
            if(player == null) {
                System.out.println("[Potioneer] Warning: Tried to add spirituality from a non-existent player. Did you mean to add blank spirituality? Check your TODO LIST!");
                Potioneer.LOGGER.info("Tried to add spirituality to a null player, skipping...");
                return mystTag;
            }
            CompoundTag spiritualityTag = mystTag.getCompound(SPIRITUALITY_KEY);
            CompoundTag idTag = mystTag.getCompound(PLAYER_KEY);
            CompoundTag nameTag = mystTag.getCompound(NAME_KEY);
            int i = 0;
            boolean flag = false;
            //tries to find a valid index i:
            //an index i is valid if its an index that corresponds to the player
            //if it couldnt find that player, it then searches for an available spot to write their information
            //in the end, you get an i that corresponds to either the players old spot, or a new one if its the first time writing this player in.
            for(String key: idTag.getAllKeys()){
                if(idTag.getUUID(key).equals(player.getUUID())){
                    flag = true;
                    break;
                }
                i++;
            }
            if(!flag){
                i = 0;
                while(spiritualityTag.contains("spirituality_" + i)){
                    i++;
                }
            }
            String tagKey = "spirituality_" + i;

            float oldSpirituality = spiritualityTag.getFloat(tagKey);
            if(oldSpirituality + spiritualityAmount <= 0){
                spiritualityTag.remove(tagKey);
                idTag.remove("player_" + i);
                nameTag.remove("name_" + i);
            } else {
                spiritualityTag.putFloat(tagKey, oldSpirituality + spiritualityAmount);
                idTag.putUUID("player_" + i, player.getUUID());
                nameTag.putString("name_" + i, player.getDisplayName().getString());
            }
            return mystTag;
        }

        public static float getTotalSpirituality(CompoundTag mystTag){
            CompoundTag spiritualityTag = mystTag.getCompound(SPIRITUALITY_KEY);
            List<String> invalidToRemove = new ArrayList<>();
            float sum = 0f;
            for(String key: spiritualityTag.getAllKeys()){
                float value = spiritualityTag.getFloat(key);
                if(value < 0) invalidToRemove.add(key);
                else sum += value;
            }
            invalidToRemove.forEach(key -> {
                spiritualityTag.remove(key);
                mystTag.getCompound(PLAYER_KEY).remove(key.replace("spirituality", "player"));
            });
            return sum;
        }

        public static float getSpiritualityOfTag(CompoundTag mysticalTag){
            return getTotalSpirituality(mysticalTag);
        }

        public static CompoundTag generateNewMysticismTag(){
            CompoundTag mystTag = new CompoundTag();
            CompoundTag spiritualityTag = new CompoundTag();
            CompoundTag idTag = new CompoundTag();
            CompoundTag nameTag = new CompoundTag();
            mystTag.put(SPIRITUALITY_KEY, spiritualityTag);
            mystTag.put(PLAYER_KEY, idTag);
            mystTag.put(NAME_KEY, nameTag);
            return mystTag;
        }
    }
}
