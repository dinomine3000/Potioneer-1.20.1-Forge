package net.dinomine.potioneer.rituals.spirits;

import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static net.dinomine.potioneer.savedata.RitualSpiritsSaveData.loadStringList;
import static net.dinomine.potioneer.savedata.RitualSpiritsSaveData.saveStringList;

public class EvilSpirit extends RitualSpiritResponse{
    protected List<String> itemsId;

    protected EvilSpirit(){
        super(null);
    }

    public EvilSpirit(RitualResponseLogic logic, List<String> validItems) {
        this();
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
        return items.stream().anyMatch(itemStack -> itemsId.contains(itemStack.getItem().getDescriptionId()));
    }

    public CompoundTag saveToNBT(){
        CompoundTag result = new CompoundTag();
        CompoundTag ritualLogic = responseLogic.saveToNBT();
        result.put("logic", ritualLogic);
        saveStringList(result, "items", itemsId);
        return result;
    }

    public static EvilSpirit fromNBT(CompoundTag compoundTag){
        if(compoundTag.contains("pathwaySequenceId")){
            return Deity.getDeityFromNBT(compoundTag);
        } else {
            RitualResponseLogic logic = RitualResponseLogic.fromNBT(compoundTag.getCompound("logic"));
            List<String> itemIds = loadStringList(compoundTag, "items");
            return new EvilSpirit(logic, itemIds);
        }
    }


    @Override
    public String toString() {
        return saveToNBT().getAsString();
    }
}
