package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class NothingResponse extends SpiritResponse {
    @Override
    public void enactResponse(RitualInputData inputData, Level level) {
    }

    @Override
    public CompoundTag saveToNBT() {
        return envelopTag(new CompoundTag(), "nothing");
    }
}
