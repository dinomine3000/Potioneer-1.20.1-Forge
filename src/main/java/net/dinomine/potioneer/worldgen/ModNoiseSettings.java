package net.dinomine.potioneer.worldgen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;

import java.util.List;

public class ModNoiseSettings {

    public static final ResourceKey<NoiseGeneratorSettings> SPIRIT_WORLD = ResourceKey.create(Registries.NOISE_SETTINGS,
            new ResourceLocation(Potioneer.MOD_ID, "spirit_world"));


    public static void bootstrap(BootstapContext<NoiseGeneratorSettings> ctx){
        ctx.register(SPIRIT_WORLD, spiritWorld());
    }
    private static NoiseGeneratorSettings spiritWorld() {
        return new NoiseGeneratorSettings(
                NoiseSettings.create(-48, 384, 1, 1),
                Blocks.AIR.defaultBlockState(),
                ModBlocks.SPIRITUALITY_BLOCK.get().defaultBlockState(),
                none(),
                SurfaceRules.state(ModBlocks.SPIRITUALITY_BLOCK.get().defaultBlockState()),
                List.of(),
                90,
                false,
                false,
                false,
                false
        );
    }

    //copied from NoiseRouterData
    protected static NoiseRouter none() {
        return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.constant(-1.0D), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }
}
