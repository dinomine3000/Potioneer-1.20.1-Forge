package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class DefaultResponse extends SpiritResponse{
    private BiConsumer<RitualInputData, Level> consumer;

    public DefaultResponse(BiConsumer<RitualInputData, Level> consumer){
        this.consumer = consumer;
    }

    @Override
    public void enactResponse(RitualInputData inputData, Level level) {
        consumer.accept(inputData, level);
    }

    @Override
    public CompoundTag saveToNBT() {
        return new CompoundTag();
    }
}
