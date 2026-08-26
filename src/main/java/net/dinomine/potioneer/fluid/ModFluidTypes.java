package net.dinomine.potioneer.fluid;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("block/water_flow");
    public static final ResourceLocation SPIRITUALITY_OVERLAY_RL = new ResourceLocation("block/water_still");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Potioneer.MOD_ID);

    public static final RegistryObject<FluidType> SPIRITUALITY_FLUID_TYPE = register("spirituality_fluid",
            FluidType.Properties.create().lightLevel(1).temperature(0).supportsBoating(false).canPushEntity(true).density(1).viscosity(1),
            WATER_STILL_RL, WATER_FLOWING_RL, SPIRITUALITY_OVERLAY_RL,
            0x99D3F2EF, new Vector3f(211f / 255f, 242f / 255f, 234f / 255f));



    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties, ResourceLocation stillRl, ResourceLocation flowingRl, ResourceLocation overlayRl, int color, Vector3f colorCoordinate) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(stillRl, flowingRl, overlayRl, color, colorCoordinate, properties));
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
