package net.dinomine.potioneer.beyonder.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dinomine.potioneer.Potioneer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.Comparator;
import java.util.List;

public class TooltipHelper {
    private static final ResourceLocation TEX = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_gui.png");
    private static final int SLICE_SIZE = 4;
    private static final int TEX_X = 98;
    private static final int TEX_Y = 211;
    private static final int TEX_WIDTH = 215;
    private static final int TEX_HEIGHT = 295;


    public static void drawTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int maxTooltipLength, int screenWidth, int screenHeight, Component text, int color, Font font){
        int textLength = font.width(text);
        int ttWidth = Math.min(textLength + 2* SLICE_SIZE, maxTooltipLength);
        List<FormattedCharSequence> lines = font.split(text, maxTooltipLength - 2* SLICE_SIZE);
        int ttHeight = lines.size()*font.lineHeight + 2* SLICE_SIZE;
        //after splitting the text, its possible the longest line is noticeably shorter than the calculated ttWidth
        //so we adjust it here to make the box more flush with the text width.
        ttWidth = Math.min(font.width(lines.stream().max(Comparator.comparingInt(font::width)).get()) + 2*SLICE_SIZE, ttWidth);
        int posX = Math.min(mouseX, screenWidth - ttWidth);
        int posY = Math.min(mouseY, screenHeight - ttHeight);
        //RenderSystem.setShaderColor((color & 0xff0000) >> 16, (color & 0x00ff00) >> 8, color & 0x0000ff, 1f);
        guiGraphics.blitNineSlicedSized(TEX, posX, posY, ttWidth, ttHeight, SLICE_SIZE, 65, 38, TEX_X, TEX_Y, TEX_WIDTH, TEX_HEIGHT);
        //RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        guiGraphics.drawWordWrap(font, text, posX + SLICE_SIZE, posY + SLICE_SIZE, ttWidth - 2* SLICE_SIZE, 0xffffff);
    }
}
