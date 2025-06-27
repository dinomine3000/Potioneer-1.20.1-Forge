package net.dinomine.potioneer.beyonder.effects;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class BeyonderEffectType {
    private final Supplier<BeyonderEffect> factory;

    public BeyonderEffectType(Supplier<BeyonderEffect> factory) {
        this.factory = factory;
    }

    public BeyonderEffect createInstance(int sequence, float cost, int duration, boolean active) {
        return factory.get().withParams(sequence, cost, duration, active);
    }

}
