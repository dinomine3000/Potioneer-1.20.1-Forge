package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.rituals.RandomizableResponse;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Random;

import static net.dinomine.potioneer.rituals.spirits.RitualSpiritResponse.getPlayer;

public class NothingResponse extends SpiritResponse {
    @Override
    public void enactResponse(RitualInputData inputData, Level level) {
    }

    @Override
    public CompoundTag saveToNBT() {
        return envelopTag(new CompoundTag(), "nothing");
    }
}
