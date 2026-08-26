package net.dinomine.potioneer.worldgen.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SpiritWorldSpecialEffects extends DimensionSpecialEffects {
    public SpiritWorldSpecialEffects() {
        super(
                Float.NaN,
                false,
                DimensionSpecialEffects.SkyType.NORMAL,
                true,
                true
        );

    }

    @Override
    public @NotNull Vec3 getBrightnessDependentFogColor(@NotNull Vec3 biomeFogColor, float daylight) {
        return biomeFogColor;
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }
}
