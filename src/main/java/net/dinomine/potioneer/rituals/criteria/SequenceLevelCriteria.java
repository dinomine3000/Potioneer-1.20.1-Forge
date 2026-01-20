package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RandomizableCriteria;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import java.util.Random;

public class SequenceLevelCriteria extends ResponseCriteria implements RandomizableCriteria<SequenceLevelCriteria> {

    private final int maxUnitsDigit;

    public SequenceLevelCriteria(int maximumSequenceLevelValue) {
        this.maxUnitsDigit = maximumSequenceLevelValue;
    }

    @Override
    public boolean checkCondition(RitualInputData input, Level level) {
        return input.pathwaySequenceId() % 10 <= maxUnitsDigit;
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

    @Override
    public SequenceLevelCriteria getRandom() {
        Random random = new Random();
        return new SequenceLevelCriteria(random.nextInt(6, 10));
    }
}
