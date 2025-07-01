package net.dinomine.potioneer;

import com.mojang.logging.LogUtils;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.misc.MysticismHelper;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.entity.ModBlockEntities;
import net.dinomine.potioneer.block.entity.renderer.MinerBlockRenderer;
import net.dinomine.potioneer.block.entity.renderer.WaterTrapBlockRenderer;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.client.ChryonRenderer;
import net.dinomine.potioneer.entities.client.PecanRenderer;
import net.dinomine.potioneer.entities.client.RodRenderer;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.item.ModCreativeModTabs;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.loot.ModLootModifiers;
import net.dinomine.potioneer.menus.CrafterAnvilScreen;
import net.dinomine.potioneer.menus.CraftingScreen;
import net.dinomine.potioneer.menus.ModMenuTypes;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.particle.ModParticles;
import net.dinomine.potioneer.recipe.ModRecipes;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
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

        //BeyonderEffects.init(eventBus);

        ModCreativeModTabs.register(eventBus);

        ModItems.register(eventBus);

        ModBlocks.register(eventBus);

        ModBlockEntities.register(eventBus);

        ModRecipes.register(eventBus);

        ModParticles.register(eventBus);

        ModSounds.register(eventBus);

        ModEntities.register(eventBus);

        ModLootModifiers.register(eventBus);

        ModEffects.register(eventBus);

        ModMenuTypes.MENU_TYPES.register(eventBus);


        // Register the commonSetup method for modloading
        eventBus.addListener(this::commonSetup);

        GeckoLib.initialize();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PotioneerCommonConfig.SPEC, "potioneer-common.toml");

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
            //Geckolib registers
            EntityRenderers.register(ModEntities.CHRYON.get(), ChryonRenderer::new);
            EntityRenderers.register(ModEntities.PECAN.get(), PecanRenderer::new);
            EntityRenderers.register(ModEntities.DIVINATION_ROD.get(), RodRenderer::new);

            BlockEntityRenderers.register(ModBlockEntities.MINER_LIGHT_BLOCK_ENTITY.get(), MinerBlockRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.WATER_TRAP_BLOCK_ENTITY.get(), WaterTrapBlockRenderer::new);

            // Some client setup code
            ItemProperties.register(ModItems.VIAL.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "level"),
                    ((itemStack, clientLevel, livingEntity, i) ->
                            itemStack.getTag() != null ? itemStack.getTag().getCompound("potion_info").getInt("amount") : 0));

            ItemProperties.register(ModItems.FLASK.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "level"),
                    ((itemStack, clientLevel, livingEntity, i) ->
                            itemStack.getTag() != null ? itemStack.getTag().getCompound("potion_info").getInt("amount") : 0));

            ItemProperties.register(ModItems.VOODOO_DOLL.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "bloodied"),
                    ((itemStack, clientLevel, livingEntity, i) ->
                            itemStack.getTag() != null && itemStack.getTag().contains(MysticismHelper.mysticismTagId) ?  1f : 0f));

            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.CRAFTER_MENU.get(), CraftingScreen::new);
                MenuScreens.register(ModMenuTypes.CRAFTER_ANVIL_MENU.get(), CrafterAnvilScreen::new);
            });


            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
