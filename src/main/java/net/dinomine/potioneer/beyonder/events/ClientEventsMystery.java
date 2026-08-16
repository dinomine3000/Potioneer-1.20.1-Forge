package net.dinomine.potioneer.beyonder.events;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.client.HUD.AbilitiesHotbarHUD;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.beyonder.client.screen.BeyonderScreen;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.mystery.GymnasticsEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.AmplificationEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.WeakeningEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.config.PotioneerClientConfig;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.DoubleJumpMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventsMystery {

    private static float realHealth = -1.0F;

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id())) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            CapProvider.beyonder(player).ifPresent(cap -> {
                if(cap.getEffectsManager().hasEffect(BeyonderEffects.MYSTERY_ILLUSION)){
                    realHealth = player.getHealth();
                    player.setHealth(1.0F);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id())) {
            if (realHealth >= 0.0F) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    mc.player.setHealth(realHealth);
                }
                realHealth = -1.0F;
            }
        }
    }

    @SubscribeEvent
    public static <T extends LivingEntity, M extends EntityModel<T>> void onRender(RenderLivingEvent.Pre<T, M> event){
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(event.getEntity());
        if(optCap.isEmpty()) return;
        if(optCap.get().getEffectsManager().hasEffect(BeyonderEffects.MYSTERY_INVISIBLE)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && isDodging(player)) {
            event.setRed(0.1F);
            event.setGreen(0.3F);
            event.setBlue(0.1F);
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && isDodging(player)) {
            if (event.getMode() == FogRenderer.FogMode.FOG_TERRAIN) {
                event.setNearPlaneDistance(4.0F);
                event.setFarPlaneDistance(10.0F);
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
        /*if (player != null && isDodging(player)) {
            RenderSystem.enableBlend();
            event.getGuiGraphics().setColor(0.7f, 0.6f, 1, 0.05f);
            event.getGuiGraphics().blit(new ResourceLocation(Potioneer.MOD_ID, "textures/effect/water_still_single.png"),
                    0, 0, event.getGuiGraphics().guiWidth(), event.getGuiGraphics().guiHeight(), 0, 0, 16, 16, 16, 16);
            event.getGuiGraphics().setColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();

        }*/
    }

    private static boolean isDodging(Player player){
        Optional<BeyonderCapability> opt = player.getCapability(CapProvider.BEYONDER_STATS).resolve();
        return opt.isPresent() &&
                (opt.get().getEffectsManager().hasEffect(BeyonderEffects.MYSTERY_DODGE));
    }

    private static boolean jumpO = false;
    private static boolean flying = false;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player == null) return;
        if(player.input.jumping){
            if(!jumpO){
                jumpO = true;
                onJumpStart(player);
            }
        } else {
            jumpO = false;
        }
        flying = player.getAbilities().flying;
    }

    private static void onJumpStart(LocalPlayer player){
        if(AbilityFunctionHelper.isEntityStandingOnGround(1, player, true)) return;
        if(!flying){
            GymnasticsEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.MYSTERY_GYMNASTICS.getEffectId(), player);
            if(eff != null && eff.canJump() && !player.getAbilities().flying){
                player.jumpFromGround();
            }
        }
    }

}