package net.dinomine.potioneer.rituals.spirits;

import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EvilSpirit extends RitualSpiritResponse{
    private List<String> itemsId;

    public EvilSpirit(RitualResponseLogic logic, List<String> validItems) {
        super(null);
        setupLogic(logic);
        itemsId = validItems;
    }

    private void setupLogic(RitualResponseLogic logic) {
        setLogic(logic);
    }

    @Override
    public boolean isValidIncense(String incenseId) {
        return false;
    }

    @Override
    public boolean isValidItems(List<ItemStack> items) {
        return items.stream().anyMatch(itemStack -> itemsId.contains(itemStack.getItem().toString()));
    }

    public CompoundTag saveToNBT(){
        CompoundTag result = new CompoundTag();
        CompoundTag ritualLogic = responseLogic.saveToNBT();
        result.put("logic", ritualLogic);
        saveStringList(result, "items", itemsId);
        return result;
    }

    public static EvilSpirit fromNBT(CompoundTag compoundTag){
        RitualResponseLogic logic = RitualResponseLogic.fromNBT(compoundTag.getCompound("logic"));
        List<String> itemIds = loadStringList(compoundTag, "items");
        return new EvilSpirit(logic, itemIds);
    }

    public static void saveStringList(CompoundTag tag, String key, List<String> strings) {
        ListTag listTag = new ListTag();
        for (String s : strings) {
            listTag.add(StringTag.valueOf(s));
        }
        tag.put(key, listTag);
    }

    public static List<String> loadStringList(CompoundTag tag, String key) {
        List<String> result = new ArrayList<>();
        if (tag.contains(key, 9)) { // 9 = ListTag
            ListTag listTag = tag.getList(key, 8); // 8 = StringTag
            for (int i = 0; i < listTag.size(); i++) {
                result.add(listTag.getString(i));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return saveToNBT().getAsString();
    }
}
