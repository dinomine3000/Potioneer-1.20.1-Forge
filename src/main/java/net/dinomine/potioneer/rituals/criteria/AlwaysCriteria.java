package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class AlwaysCriteria extends ResponseCriteria{

    @Override
    public boolean checkCondition(RitualInputData input, Level level) {
        return true;
    }

    @Override
    public CompoundTag saveToNBT() {
        return envelopTag(new CompoundTag(), "always");
    }
}
