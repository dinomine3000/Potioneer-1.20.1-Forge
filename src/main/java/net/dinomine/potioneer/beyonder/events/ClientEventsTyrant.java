package net.dinomine.potioneer.beyonder.events;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.*;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.client.HUD.AbilitiesHotbarHUD;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AmplificationEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.GeneralProhibitionEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.MistEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.WeakeningEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.config.PotioneerClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventsTyrant {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMouseDown(InputEvent.MouseButton.Pre event){
        if(Minecraft.getInstance().level == null) return;
        MistEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.TYRANT_MIST_EFFECT.getEffectId(), Minecraft.getInstance().player);
        if(eff == null) return;
        AbilityInfo currentAbility = AbilitiesHotbarHUD.getCurrentSelectedAbility();
        if(currentAbility != null && Abilities.MIST.getAblId().equalsIgnoreCase(currentAbility.innerId())) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.isSprinting()) {
                GeneralProhibitionEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.TYRANT_GENERAL_PROHIBITION.getEffectId(), mc.player);
                if(eff == null || !eff.type.equalsIgnoreCase("sprinting")) return;
                mc.player.setSprinting(false);
                mc.options.keySprint.setDown(false);
            }
        }
    }

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
    public static void renderAmplifyWeaken(RenderGuiOverlayEvent.Post event){
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Optional<BeyonderCapability> optCap = ClientStatsData.getCapability();
        if(optCap.isEmpty()) return;
        BeyonderCapability cap = optCap.get();
        WeakeningEffect weakening = (WeakeningEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_WEAKENING.getEffectId());
        AmplificationEffect amplification = (AmplificationEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_AMPLIFICATION.getEffectId());

        List<UUID> weakenedAbls = new ArrayList<>();
        List<UUID> amplifiedAbls = new ArrayList<>();
        if(weakening != null) weakenedAbls = new ArrayList<>(weakening.getAffectedInstances());
        if(amplification != null) amplifiedAbls = new ArrayList<>(amplification.getAffectedInstances());
        int idx = 0;

        float scale = (float) (PotioneerClientConfig.HOTBAR_SCALE.get()*1f);

        for(UUID instanceId: weakenedAbls){
            Ability abl = cap.getAbilitiesManager().getAbilityInstance(instanceId);
            AbilitiesHotbarHUD.drawAbility(guiGraphics, abl.getAbilityInfo(), (int)((idx++)*(AbilitiesHotbarHUD.CASE_WIDTH*scale)) + 5 + AbilitiesHotbarHUD.CASE_WIDTH/2, 10, scale);
            //guiGraphics.drawString(Minecraft.getInstance().font, abl.getAbilityInfo().descId(), 0, (int) (Minecraft.getInstance().font.lineHeight*1.5*(idx++)), 0, false);
        }

        for(UUID id: amplifiedAbls){
            Ability abl = cap.getAbilitiesManager().getAbilityInstance(id);
            AbilitiesHotbarHUD.drawAbility(guiGraphics, abl.getAbilityInfo(), (int)((idx++)*(AbilitiesHotbarHUD.CASE_WIDTH*scale)) + 5 + AbilitiesHotbarHUD.CASE_WIDTH/2, (int) (20 + AbilitiesHotbarHUD.CASE_HEIGHT*scale), scale);
            //guiGraphics.drawString(Minecraft.getInstance().font, abl.getAbilityInfo().descId(), 0, 10 + (int) (Minecraft.getInstance().font.lineHeight*1.5*(idx++)), 0, false);
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
        Optional<BeyonderCapability> opt = player.getCapability(CapProvider.BEYONDER_STATS).resolve();
        return opt.isPresent() && opt.get().getEffectsManager().hasEffect(BeyonderEffects.TYRANT_DROWNING);
    }

}