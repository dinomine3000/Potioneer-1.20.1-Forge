package net.dinomine.potioneer.beyonder.events;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventsTyrant {

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && isDrowning(player)) {
            event.setRed(0.1F);
            event.setGreen(0.1F);
            event.setBlue(0.3F);
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && isDrowning(player)) {
            if (event.getMode() == FogRenderer.FogMode.FOG_TERRAIN) {
                event.setNearPlaneDistance(2.0F);
                event.setFarPlaneDistance(7.0F);
                event.setFogShape(FogShape.SPHERE);

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void renderWaterOverlay(RenderGuiOverlayEvent.Pre event){
        Player player = Minecraft.getInstance().player;
        if (player != null && isDrowning(player)) {
            RenderSystem.enableBlend();
            event.getGuiGraphics().setColor(0.7f, 0.6f, 1, 0.05f);
            event.getGuiGraphics().blit(new ResourceLocation(Potioneer.MOD_ID, "textures/effect/water_still_single.png"),
                    0, 0, event.getGuiGraphics().guiWidth(), event.getGuiGraphics().guiHeight(), 0, 0, 16, 16, 16, 16);
            event.getGuiGraphics().setColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
        }
    }

    private static boolean isDrowning(Player player){
        Optional<LivingEntityBeyonderCapability> opt = player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        return opt.isPresent() && opt.get().getEffectsManager().hasEffect(BeyonderEffects.TYRANT_DROWNING);
    }

}