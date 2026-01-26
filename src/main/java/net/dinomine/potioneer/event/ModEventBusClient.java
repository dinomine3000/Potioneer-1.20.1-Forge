package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.HUD.AbilitiesHotbarHUD;
import net.dinomine.potioneer.beyonder.client.HUD.MagicOrbOverlay;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.custom.PotionCauldronBlock;
import net.dinomine.potioneer.block.entity.ModBlockEntities;
import net.dinomine.potioneer.block.entity.renderer.PotionCauldronBlockEntityRenderer;
import net.dinomine.potioneer.block.entity.renderer.RitualPedestalBlockEntityRenderer;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.item.custom.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClient {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(ModBlockEntities.POTION_CAULDRON_BLOCK_ENTITY.get(), PotionCauldronBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RITUAL_PEDESTAL_BLOCK_ENTITY.get(), RitualPedestalBlockEntityRenderer::new);

    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event){
        event.register(KeyBindings.INSTANCE.beyonderMenuKey);
        event.register(KeyBindings.INSTANCE.quickAbilityKey);
        event.register(KeyBindings.INSTANCE.showHotbarKey);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event){
        event.register(new AbstractLiquidContainer.LiquidContainerTint(), ModItems.VIAL.get(), ModItems.FLASK.get());
        event.register(new CharacteristicItem.CharacteristicTint(), ModItems.CHARACTERISTIC.get());
        event.register(new ArtifactTint(), ModItems.RING.get());
        event.register(new ArtifactTint(), ModItems.CROWN.get());
        event.register(new GemItem.GemItemTint(), ModItems.GEM.get());
        event.register(new NecklaceItem.NecklaceItemTint(), ModItems.AMULET.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
        event.register(new PotionCauldronBlock.PotionCauldronTint(), ModBlocks.POTION_CAULDRON.get());
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event){
        event.registerAboveAll("beyonder", MagicOrbOverlay.HUD_MAGIC);
        event.registerAboveAll("ability_hotbar", AbilitiesHotbarHUD.ABILITY_HOTBAR);
    }
}
