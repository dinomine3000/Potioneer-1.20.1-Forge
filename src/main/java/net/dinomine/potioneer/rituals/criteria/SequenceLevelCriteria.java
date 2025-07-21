package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class SequenceLevelCriteria extends ResponseCriteria{

    private final int maxUnitsDigit;

    public SequenceLevelCriteria(int maximumSequenceLevelValue) {
        this.maxUnitsDigit = maximumSequenceLevelValue;
    }

    @Override
    public boolean checkCondition(RitualInputData input) {
        return input.pathwayId() % 10 <= maxUnitsDigit;
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", "sequence_level");
        tag.putInt("max_units_digit", maxUnitsDigit);
        return envelopTag(tag, "sequence_level");
    }

    public static SequenceLevelCriteria getFromTag(Tag tag) throws IllegalArgumentException {
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        int digit = compoundTag.getInt("max_units_digit");
        return new SequenceLevelCriteria(digit);
    }
}
