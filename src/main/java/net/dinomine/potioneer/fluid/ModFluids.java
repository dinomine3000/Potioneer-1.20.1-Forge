package net.dinomine.potioneer.fluid;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, Potioneer.MOD_ID);

    public static final RegistryObject<FlowingFluid> SOURCE_SPIRITUALITY = FLUIDS.register("spirituality_fluid",
            () -> new ForgeFlowingFluid.Source(ModFluids.SPIRITUALITY_FLUID_PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING_SPIRITUALITY = FLUIDS.register("flowing_spirituality_fluid",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.SPIRITUALITY_FLUID_PROPERTIES));


    public static final ForgeFlowingFluid.Properties SPIRITUALITY_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.SPIRITUALITY_FLUID_TYPE, SOURCE_SPIRITUALITY, FLOWING_SPIRITUALITY)
            .slopeFindDistance(2).levelDecreasePerBlock(1)
            .block(ModBlocks.SPIRITUALITY_BLOCK).bucket(ModItems.SPIRITUALITY_BUCKET);


    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
