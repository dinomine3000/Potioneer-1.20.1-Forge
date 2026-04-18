package net.dinomine.potioneer.util;

import net.dinomine.potioneer.util.misc.CharacteristicHelper;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ModCompoundTags {

    public static final String BEYONDER_TAG_ID = "beyonder_info";

    public static boolean hasTag(String tagId, ItemStack item){
        return item.hasTag() && item.getTag().contains(tagId);
    }

    @Nullable
    public static CompoundTag getTagFromItemOrNull(String tagId, ItemStack item){
        if (hasTag(tagId, item)) {
            return item.getTag();
        }
        return null;
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

    /**
     * Tag that holds characteristic information. any item with this will be seen as a characteristic.
     */
    public static class BeyonderInfoTag{
        private static final String listKey = "charIds";

        public static void setTagForItem(ItemStack stack, int pathSeqId){
            setTagForItem(stack, List.of(pathSeqId));
        }

        public static void setTagForItem(ItemStack stack, List<Integer> ids){
            CompoundTag root = stack.getOrCreateTag();
            root.put(BEYONDER_TAG_ID, createTagForIds(ids));
            stack.setTag(root);
        }

        public static CompoundTag createTagForId(int charId){
            return createTagForIds(List.of(charId));
        }
        public static CompoundTag createTagForIds(List<Integer> charIds){
            return setTagIds(charIds, new CompoundTag());
        }

        public static CompoundTag setTagIds(List<Integer> ids, CompoundTag tag){
            ListTag charIds = toNumberListTag(ids);
            tag.put(listKey, charIds);
            return tag;
        }
        public static List<Integer> getCharIds(CompoundTag tag){
            return fromIntListTag(tag.getList(listKey, Tag.TAG_INT));
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
        }

        public static boolean containsId(int id, CompoundTag tag){
            return getCharIds(tag).contains(id);
        }

        public static boolean isOfSamePathway(int pathwayId, CompoundTag tag){
            return getCharIds(tag).stream().map(id -> Math.floorDiv(id, 10)).anyMatch(id -> id == pathwayId);
        }
    }
}
