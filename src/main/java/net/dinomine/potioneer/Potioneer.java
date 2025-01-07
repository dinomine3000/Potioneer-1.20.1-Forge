package net.dinomine.potioneer;

import com.mojang.logging.LogUtils;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.entity.ModBlockEntities;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.client.ChryonRenderer;
import net.dinomine.potioneer.entities.client.PecanRenderer;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.item.ModCreativeModTabs;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.item.ModPotions;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.particle.ModParticles;
import net.dinomine.potioneer.recipe.ModRecipes;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Potioneer.MOD_ID)
public class Potioneer
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "potioneer";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public Potioneer()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModTabs.register(eventBus);

        ModItems.register(eventBus);

        ModBlocks.register(eventBus);

        ModBlockEntities.register(eventBus);

        ModRecipes.register(eventBus);

        ModParticles.register(eventBus);

        ModSounds.register(eventBus);

        ModEntities.register(eventBus);


        // Register the commonSetup method for modloading
        eventBus.addListener(this::commonSetup);

        GeckoLib.initialize();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        eventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        PacketHandler.init();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event){
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS){
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(ModEntities.CHRYON.get(), ChryonRenderer::new);
            EntityRenderers.register(ModEntities.PECAN.get(), PecanRenderer::new);
            // Some client setup code
            ItemProperties.register(ModItems.VIAL.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "level"),
                    ((itemStack, clientLevel, livingEntity, i) ->
                            itemStack.getTag() != null ? itemStack.getTag().getInt("level") : 0));

            ItemProperties.register(ModItems.FLASK.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "level"),
                    ((itemStack, clientLevel, livingEntity, i) ->
                            itemStack.getTag() != null ? itemStack.getTag().getInt("level") : 0));
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
