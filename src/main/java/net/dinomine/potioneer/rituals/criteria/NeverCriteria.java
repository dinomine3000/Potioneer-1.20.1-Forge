package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class NeverCriteria extends ResponseCriteria{

    @Override
    public boolean checkCondition(RitualInputData input, Level level) {
        return false;
    }

    @Override
    public CompoundTag saveToNBT() {
        return envelopTag(new CompoundTag(), "never");
    }
}
