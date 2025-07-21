package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.function.Consumer;

public class DefaultResponse extends SpiritResponse{
    private Consumer<RitualInputData> consumer;

    public DefaultResponse(Consumer<RitualInputData> consumer){
        this.consumer = consumer;
    }

    @Override
    public void enactResponse(RitualInputData inputData) {
        consumer.accept(inputData);
    }

    @Override
    public CompoundTag saveToNBT() {
        return new CompoundTag();
    }
}
