package net.dinomine.potioneer;

import com.mojang.logging.LogUtils;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.entity.ModBlockEntities;
import net.dinomine.potioneer.block.entity.renderer.MinerBlockRenderer;
import net.dinomine.potioneer.block.entity.renderer.PriestBlockRenderer;
import net.dinomine.potioneer.block.entity.renderer.WaterTrapBlockRenderer;
import net.dinomine.potioneer.config.PotioneerClientConfig;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.config.PotioneerRitualsConfig;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.client.*;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.item.ModCreativeModTabs;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.item.custom.UnshadowedCrucifixItem;
import net.dinomine.potioneer.loot.ModLootModifiers;
import net.dinomine.potioneer.menus.CrafterAnvilScreen;
import net.dinomine.potioneer.menus.CraftingScreen;
import net.dinomine.potioneer.menus.ModMenuTypes;
import net.dinomine.potioneer.menus.ritual_altar.RitualAltarScreen;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.particle.ModParticles;
import net.dinomine.potioneer.recipe.ModRecipes;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.registries.NewRegistryEvent;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.core.molang.LazyVariable;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.core.molang.MolangQueries;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Potioneer.MOD_ID)
public class Potioneer
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "potioneer";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


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

        Pathways.register(eventBus);

//        ModAttributes.REGISTRY.register(eventBus);

//        JSONParserHelper.loadChangedFormulas();
//        JSONParserHelper.loadNewFormulas();


        // Register the commonSetup method for modloading
        eventBus.addListener(this::commonSetup);

        GeckoLib.initialize();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PotioneerCommonConfig.SPEC, "potioneer-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PotioneerClientConfig.SPEC, "potioneer-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, PotioneerRitualsConfig.SPEC, "potioneer-server-rituals.toml");
       // ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PotioneerFormulaConfig.SPEC, "potioneer-formula.toml");

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

    @SubscribeEvent
    public void registerAblities(NewRegistryEvent event){

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
            EntityRenderers.register(ModEntities.WANDERING_CACTUS.get(), WanderingCactusRenderer::new);
            EntityRenderers.register(ModEntities.DEMONIC_WOLF.get(), DemonicWolfRenderer::new);
            EntityRenderers.register(ModEntities.DIVINATION_ROD.get(), RodRenderer::new);
            EntityRenderers.register(ModEntities.SEA_GOD_SCEPTER.get(), SeaGodRenderer::new);
            EntityRenderers.register(ModEntities.CHARACTERISTIC.get(), CharRenderer::new);
            EntityRenderers.register(ModEntities.ASTEROID.get(), AsteroidRenderer::new);
            EntityRenderers.register(ModEntities.DICE_EFFECT_ENTITY.get(), DiceEffectRenderer::new);
            EntityRenderers.register(ModEntities.SLOT_MACHINE_ENTITY.get(), SlotMachineRenderer::new);
            EntityRenderers.register(ModEntities.CHARM_ENTITY.get(), CharmRenderer::new);
            EntityRenderers.register(ModEntities.WATER_BLOCK_EFFECT_ENTITY.get(), WaterEffectEntityRenderer::new);

            MolangParser.INSTANCE.register(new LazyVariable("query.target_x", 0.0F));

            BlockEntityRenderers.register(ModBlockEntities.MINER_LIGHT_BLOCK_ENTITY.get(), MinerBlockRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.PRIEST_LIGHT_BLOCK_ENTITY.get(), PriestBlockRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.WATER_TRAP_BLOCK_ENTITY.get(), WaterTrapBlockRenderer::new);
            // Some client setup code
            //give different item models for these items depending on their tags
            ItemProperties.register(ModItems.VIAL.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "level"),
                    ((itemStack, clientLevel, livingEntity, i) ->
                            itemStack.getTag() != null ? itemStack.getTag().getCompound("potion_info").getInt("amount") : 0));

            ItemProperties.register(ModItems.FLASK.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "level"),
                    ((itemStack, clientLevel, livingEntity, i) ->
                            itemStack.getTag() != null ? itemStack.getTag().getCompound("potion_info").getInt("amount") : 0));

            ItemProperties.register(ModItems.RING.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "artifact"),
                    ((itemStack, clientLevel, livingEntity, i) ->
                            itemStack.getTag() != null && itemStack.getTag().contains(MysticalItemHelper.ARTIFACT_TAG_ID) ?  1f : 0f));

            ItemProperties.register(ModItems.CROWN.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "artifact"),
                    ((itemStack, clientLevel, livingEntity, i) ->
                            itemStack.getTag() != null && itemStack.getTag().contains(MysticalItemHelper.ARTIFACT_TAG_ID) ?  1f : 0f));

            ItemProperties.register(ModItems.UNSHADOWED_CRUCIFIX.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "crucifix_state"),
                    ((itemStack, clientLevel, livingEntity, i) -> {
                        if(!itemStack.hasTag() || !itemStack.getTag().contains(UnshadowedCrucifixItem.CRUCIFIX_TAG_ID)) return 2;
                        CompoundTag tag = itemStack.getOrCreateTag().getCompound(UnshadowedCrucifixItem.CRUCIFIX_TAG_ID);
                        return tag.getInt("state");
                    }));
            ItemProperties.register(ModItems.CHARM.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "charm"),
                    ((itemStack, clientLevel, livingEntity, i) -> {
                        if(itemStack.hasTag() && itemStack.getTag().contains(MysticalItemHelper.CHARM_TAG_ID)){
                            return Math.floorDiv(itemStack.getTag().getCompound(MysticalItemHelper.CHARM_TAG_ID).getInt("pathwaySequenceId"), 10);
                        }
                        return -1;
                    }));

            ItemProperties.register(ModItems.AMULET.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "enabled"),
                    ((itemStack, clientLevel, livingEntity, i) -> {
                        if(itemStack.hasTag() && itemStack.getTag().contains(MysticalItemHelper.ARTIFACT_TAG_ID)){
                            return itemStack.getTag().getCompound(MysticalItemHelper.ARTIFACT_TAG_ID).getBoolean("enabled") ? 1 : -1;
                        }
                        return -1;
                    }));

            ItemProperties.register(ModItems.LEYMANOS_TRAVELS.get(),
                    new ResourceLocation(Potioneer.MOD_ID, "entity"),
                    ((itemStack, clientLevel, livingEntity, i) -> livingEntity == null ? 1 : 0));


            ItemProperties.register(ModBlocks.SCRIPTURE_STAND.get().asItem(),
                    new ResourceLocation(Potioneer.MOD_ID, "pathway_id"),
                    ((itemStack, clientLevel, livingEntity, i) -> {
                        if(itemStack.hasTag() && itemStack.getTag().contains("stand_id")){
                            return itemStack.getTag().getInt("stand_id");
                        }
                        return 0;
                    }));

            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.CRAFTER_MENU.get(), CraftingScreen::new);
                MenuScreens.register(ModMenuTypes.CRAFTER_ANVIL_MENU.get(), CrafterAnvilScreen::new);
                MenuScreens.register(ModMenuTypes.RITUAL_ALTAR_MENU.get(), RitualAltarScreen::new);

                ItemBlockRenderTypes.setRenderLayer(ModBlocks.STAR_FLOWER_BLOCK.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.RITUAL_ALTAR.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.RITUAL_INK.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.SOLSEER_TORCH.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.SOLSEER_WALL_TORCH.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.FAKE_WATER.get(), RenderType.translucent());
            });


            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
