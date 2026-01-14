package net.dinomine.potioneer.beyonder.client.HUD;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientConfigData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
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

    public static final IGuiOverlay HUD_MAGIC = ((forgeGui, guiGraphics, partialTick, width, height) -> {
        int id = ClientStatsData.getPathwaySequenceId();
        if(id < 0) return;
        ClientConfigData.updateData();

        int multiplier = ClientConfigData.getCurrentOrbScale();
        int orbSide = Mth.floor(43 * multiplier);
        int bgSide = Math.round(orbSide*64/43f);
        int configOffset = (int) (ClientConfigData.getCurrentOffset()*(minecraft.getWindow().getGuiScaledWidth()/2f - orbSide));
        int yOffset = (int) (minecraft.getWindow().getGuiScaledHeight() - 64*multiplier);
        int offsetLeft = ClientConfigData.isOrbOnRight() ? minecraft.getWindow().getGuiScaledWidth()/2 + configOffset : configOffset;

        /*RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, ORB);*/


        tick = (tick + minecraft.getDeltaFrameTime()*4) % (62*slowdown);
        int frame = ((int) (tick / slowdown)) % 31;

        float mana_percent = Mth.clamp(Math.round(100f*ClientStatsData.getPlayerSpirituality() / ClientStatsData.getPlayerMaxSpirituality())/100f, 0, 1);

        int sanity_percent = getSanityIndex();

        guiGraphics.blit(ORB, offsetLeft, yOffset,
                bgSide, bgSide,
                ((id/10) %4)*64, id > 40 ? 64:0,
                64, 64,
                256, 128);

//        guiGraphics.blit(MANA, offsetLeft+10, yOffset + 10 + (int)(43-mana_percent*43),
//                10 + sanity_percent*64, 10 + frame*64 + (int)((1-mana_percent)*43),
//                43, 43 - (int)(43-43*mana_percent),
//                256, 1984);
        int UVHeightToBlit = (int)(mana_percent * 43);
        int heightToBlit = (int)(multiplier*UVHeightToBlit);
        int blitManaOffset = Mth.floor(10*orbSide/43f);
//        int blitManaOffset = 20;
        guiGraphics.blit(MANA,
                offsetLeft+blitManaOffset, yOffset + blitManaOffset + orbSide - heightToBlit,
                orbSide, heightToBlit,
                10 + sanity_percent*64, 10 + frame*64 + 43 - UVHeightToBlit,
                43, UVHeightToBlit,
                256, 1984);

        guiGraphics.blit(ORB_OVERLAY, offsetLeft, yOffset,
                bgSide, bgSide,
                ((id/10) %4)*64, id > 40 ? 64:0,
                64, 64,
                256, 128);

    });

    public static int getSanityIndex(){
        float sanity = Mth.clamp(ClientStatsData.getPlayerSanity(), 0, 100);
        int sanity_percent = 0;
        if(sanity < 87.5) sanity_percent++;
        if(sanity < 45) sanity_percent++;
        if(sanity < LivingEntityBeyonderCapability.SANITY_FOR_DROP) sanity_percent++;
        return sanity_percent;
    }
}
