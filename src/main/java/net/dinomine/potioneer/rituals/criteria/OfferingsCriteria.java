package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RandomizableCriteria;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.savedata.RitualSpiritsSaveData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

import static net.dinomine.potioneer.savedata.RitualSpiritsSaveData.loadStringList;
import static net.dinomine.potioneer.savedata.RitualSpiritsSaveData.saveStringList;

public class OfferingsCriteria extends ResponseCriteria implements RandomizableCriteria<OfferingsCriteria> {
    private final List<String> itemIds;

    public OfferingsCriteria(List<String> itemIds){
        this.itemIds = itemIds;
    }

    @Override
    public boolean checkCondition(RitualInputData input, Level level) {
        return input.offerings().stream().anyMatch(itemStack -> itemIds.contains(itemStack.getItem().getDescriptionId()));
    }

    @Override
    public Tag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        saveStringList(tag, "items", itemIds);
        return envelopTag(tag, "offerings");
    }

    public static OfferingsCriteria getFromTag(Tag tag){
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        List<String> itemIds = loadStringList(compoundTag, "items");
        return new OfferingsCriteria(itemIds);
    }

    @Override
    public OfferingsCriteria getRandom() {
        Random random = new Random();
        return new OfferingsCriteria(RitualSpiritsSaveData.getRandomItems(random.nextInt(1, 3)));
    }
}
