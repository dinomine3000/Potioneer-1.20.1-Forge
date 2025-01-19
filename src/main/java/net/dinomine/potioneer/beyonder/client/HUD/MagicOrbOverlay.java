package net.dinomine.potioneer.beyonder.client.HUD;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class MagicOrbOverlay {
    private static float tick = 0;
    private static final int slowdown = 16;
    private static final ResourceLocation ORB = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/hud_orb_bg.png");
    private static final ResourceLocation ORB_OVERLAY = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/hud_orb_overlay.png");
    private static final ResourceLocation MANA = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/spirituality.png");

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar() {
        return true;
    }

    public static final IGuiOverlay HUD_MAGIC = ((forgeGui, guiGraphics, partialTick, width, height) -> {
        int id = ClientStatsData.getPathwayId();
        if(id < 0) return;


        int yOffset = minecraft.getWindow().getGuiScaledHeight() - 62;
        int offsetLeft = minecraft.getWindow().getGuiScaledWidth()/2 + 100;

        /*RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, ORB);*/


        tick = (tick + partialTick) % (62*slowdown);
        int frame = ((int) (tick / slowdown)) % 31;

        float mana_percent = Mth.clamp(Math.round(100f*ClientStatsData.getPlayerSpirituality() / ClientStatsData.getPlayerMaxSpirituality())/100f, 0, 1);
        float sanity = Mth.clamp(ClientStatsData.getPlayerSanity()/100f, 0f, 1f);
        int sanity_percent = switch((int)(sanity*4)){
            case 0 -> 3;
            case 1 -> 2;
            case 2 -> 1;
            default -> 0;
        };

        guiGraphics.blit(ORB, offsetLeft, yOffset, ((id/10) %4)*64, id > 40 ? 64:0, 64, 64,256, 128);

        guiGraphics.blit(MANA, offsetLeft+10, yOffset + 10 + (int)(43-mana_percent*43),
                10 + sanity_percent*64, 10 + frame*64 + (int)((1-mana_percent)*43),
                43, 43 - (int)(43-43*mana_percent),
                256, 1984);

        guiGraphics.blit(ORB_OVERLAY, offsetLeft, yOffset, ((id/10) %4)*64, id > 40 ? 64:0, 64, 64,256, 128);

    });
}
