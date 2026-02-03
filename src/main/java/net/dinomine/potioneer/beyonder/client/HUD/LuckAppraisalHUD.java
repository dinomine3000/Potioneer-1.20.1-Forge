package net.dinomine.potioneer.beyonder.client.HUD;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientHudData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class LuckAppraisalHUD {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/luck_appraisal_atlas.png");
    private static final int WIDTH = 256, HEIGHT = 256, CORNER_SIZE = 5;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayOverlay() {
        return ClientHudData.shouldDisplayLuckHud();
    }

    public static final IGuiOverlay LUCK_OVERLAY = ((forgeGui, guiGraphics, partialTick, width, height) -> {
        if(minecraft.isPaused()){
            return;
        }
        if(!shouldDisplayOverlay()) return;
        ClientHudData.sendUpdateRequest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.8f);
        float scale = 1f;
        int windowHeight = ClientHudData.showLuckNotStats() ? 140 : 90;
        int windowWidth = ClientHudData.showLuckNotStats() ? 200 : 150;
        int x1 = width/10, y1 = height / 2 - windowHeight/2, x2 = x1 + windowWidth, y2 = y1 + windowHeight;
        drawBgCorners(guiGraphics, scale, x1, y1, x2, y2);
        drawEdgesAndCenter(guiGraphics, scale, x1, y1, x2, y2);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        drawSnakes(guiGraphics, scale, x1, y1, x2, y2);
        drawText(guiGraphics, scale, x1, y1, x2, y1);
    });

    private static void drawBgCorners(GuiGraphics guiGraphics, float scale, int x1, int y1, int x2, int y2){
        int width = x2-x1;
        int height = y2-y1;
        int adjustedSize = (int) (CORNER_SIZE*scale);
        guiGraphics.blit(TEXTURE, x1, y1, adjustedSize, adjustedSize, 0, 0, CORNER_SIZE, CORNER_SIZE, WIDTH, HEIGHT);
        guiGraphics.blit(TEXTURE, x1 + width - adjustedSize, y1, adjustedSize, adjustedSize, 44, 0, CORNER_SIZE, CORNER_SIZE, WIDTH, HEIGHT);
        guiGraphics.blit(TEXTURE, x1 + width - adjustedSize, y1 + height - adjustedSize, adjustedSize, adjustedSize, 44, 44, CORNER_SIZE, CORNER_SIZE, WIDTH, HEIGHT);
        guiGraphics.blit(TEXTURE, x1, y1 + height - adjustedSize, adjustedSize, adjustedSize, 0, 44, CORNER_SIZE, CORNER_SIZE, WIDTH, HEIGHT);
    }

    private static void drawEdgesAndCenter(GuiGraphics guiGraphics, float scale, int x1, int y1, int x2, int y2){
        int width = x2-x1;
        int height = y2-y1;
        int cornerArea = 2*(int)(Math.floor(5*scale));
        int px = x1 + (int)(scale*CORNER_SIZE);
        int py = y1 + (int)(scale*CORNER_SIZE);
        if(width > cornerArea){
            guiGraphics.blit(TEXTURE, px, y1, width - cornerArea, (int)(CORNER_SIZE*scale), CORNER_SIZE, 0, 39, CORNER_SIZE, WIDTH, HEIGHT);
            guiGraphics.blit(TEXTURE, px, y2 - (int)(CORNER_SIZE*scale), width - cornerArea, (int)(CORNER_SIZE*scale), CORNER_SIZE, 44, 39, CORNER_SIZE, WIDTH, HEIGHT);
        }
        if(height > cornerArea){
            guiGraphics.blit(TEXTURE, x1, py, (int)(CORNER_SIZE*scale), height - cornerArea, 0, CORNER_SIZE, CORNER_SIZE, 39, WIDTH, HEIGHT);
            guiGraphics.blit(TEXTURE, x2 - (int)(CORNER_SIZE*scale), py, (int)(CORNER_SIZE*scale), height - cornerArea, 44, CORNER_SIZE, CORNER_SIZE, 39, WIDTH, HEIGHT);
        }
        if(width > cornerArea && height > cornerArea){
            guiGraphics.blit(TEXTURE, px, py, width - cornerArea, height - cornerArea, CORNER_SIZE, CORNER_SIZE, 39, 39, WIDTH, HEIGHT);
        }
    }

    private static void drawSnakes(GuiGraphics guiGraphics, float scale, int x1, int y1, int x2, int y2){
        int width = x2-x1;
        int height = y2-y1;
//        float newScale = Math.min(width/2f/25, height*0.8f/40);
        float newScale = 2;
        guiGraphics.blit(TEXTURE, x1 - (int)(5*newScale), y1 - (int)(2*newScale), (int)(27*newScale), (int)(19*newScale), 49, 0, 27, 19, WIDTH, HEIGHT);
        guiGraphics.blit(TEXTURE, x2 - (int)((CORNER_SIZE + 17)*newScale), y1 - (int)(1*newScale), (int)(14*newScale), (int)(6*newScale), 79, 1, 14, 6, WIDTH, HEIGHT);
        guiGraphics.blit(TEXTURE, x2 - (int)(6*newScale), y2 - height/2 - (int)(newScale*19), (int)(8*newScale), (int)(38*newScale), 97, 6, 8, 38, WIDTH, HEIGHT);
        guiGraphics.blit(TEXTURE, x2 - (int)(26*newScale), y2 - (int)(5*newScale), (int)(24*newScale), (int)(9*newScale), 78, 46, 24, 9, WIDTH, HEIGHT);
        guiGraphics.blit(TEXTURE, x1 - (int)(5*newScale), y2 - (int)(22*newScale), (int)(26*newScale), (int)(28*newScale), 49, 29, 26, 28, WIDTH, HEIGHT);
    }

    private static void drawText(GuiGraphics guiGraphics, float scale, int x1, int y1, int x2, int y2){
        guiGraphics.drawString(Minecraft.getInstance().font, ClientHudData.getNameComponent(), x1 + 10, y1 + 20, 0, false);
        if(ClientHudData.showLuckNotStats()){
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("hud.potioneer.luck_axis"), x1 + 55, y1 + 40, 0, false);
            //luck range
            guiGraphics.drawString(Minecraft.getInstance().font, ClientHudData.getLuckData(), x1 + 10, y1 + 55, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(- ClientHudData.getMinLuck())), x1 + 65, y1 + 55, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(ClientHudData.getPosLuck())), x1 + 115, y1 + 55, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(ClientHudData.getMaxLuck())), x1 + 165, y1 + 55, 0, false);

            //base values
            guiGraphics.drawString(Minecraft.getInstance().font, ClientHudData.getBaseLuck(), x1 + 10, y1 + 70, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(-ClientHudData.baseMin)), x1 + 65, y1 + 70, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(ClientHudData.basePos)), x1 + 115, y1 + 70, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(ClientHudData.baseMax)), x1 + 165, y1 + 70, 0, false);

            //modifiers
            guiGraphics.drawString(Minecraft.getInstance().font, ClientHudData.getTempLuck(), x1 + 10, y1 + 85, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(-ClientHudData.tempMin)), x1 + 65, y1 + 85, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(ClientHudData.tempPos)), x1 + 115, y1 + 85, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(ClientHudData.tempMax)), x1 + 165, y1 + 85, 0, false);

            //decaying values
            guiGraphics.drawString(Minecraft.getInstance().font, ClientHudData.getDecayLuck(), x1 + 10, y1 + 100, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(-ClientHudData.decayMin)), x1 + 65, y1 + 100, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(ClientHudData.decayPos)), x1 + 115, y1 + 100, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(ClientHudData.decayMax)), x1 + 165, y1 + 100, 0, false);

        }
        else {
            guiGraphics.drawString(Minecraft.getInstance().font, ClientHudData.getHealthComponent(), x1 + 10, y1 + 35, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, ClientHudData.getSpiritualityComponent(), x1 + 10, y1 + 50, 0, false);
            guiGraphics.drawString(Minecraft.getInstance().font, ClientHudData.getSanityComponent(), x1 + 10, y1 + 65, 0, false);
        }

    }
}
