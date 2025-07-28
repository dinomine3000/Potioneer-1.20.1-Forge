package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class PlayerResponse extends SpiritResponse {
    @Override
    public void enactResponse(RitualInputData inputData, Level level) {
        System.out.println("ritual to player being responded...");
    }

    @Override
    public CompoundTag saveToNBT() {
        return envelopTag(new CompoundTag(), "player");
    }
}
