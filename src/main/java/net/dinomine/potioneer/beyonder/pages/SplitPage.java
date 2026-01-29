package net.dinomine.potioneer.beyonder.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

public abstract class SplitPage extends Page {
    private final Component topText;
    private final Component bottomText;
    private int yOffset = 0;
    private boolean drawTop = true, drawBottom = true;

    protected void setDrawTop(boolean drawTop){
        drawTop = drawTop;
    }

    protected void setDrawBottom(boolean drawBottom){
        drawBottom = drawBottom;
    }

    public SplitPage withOffset(int yOffset){
        this.yOffset = yOffset;
        return this;
    }

    public SplitPage(Chapter chapter, Component title, Component topText, Component bottomText) {
        super(chapter, title);
        this.topText = topText;
        this.bottomText = bottomText;
    }

    public SplitPage(Chapter chapter, Component title, String id){
        super(chapter, title);
        this.topText = Component.translatable("contents.potioneer." + id + "_top");
        this.bottomText = Component.translatable("contents.potioneer." + id + "_bottom");
    }

    public SplitPage(Chapter chapter, String id) {
        super(chapter, id);
        this.topText = Component.translatable("contents.potioneer." + id + "_top");
        this.bottomText = Component.translatable("contents.potioneer." + id + "_bottom");
    }

    @Override
    public void draw(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        if(drawTop) drawScaledText(pGuiGraphics, this.topText, leftPos + 5, topPos, 0, 110);
//        drawScaledText(pGuiGraphics, this.bottomText, leftPos + 5, topPos + 100 - yOffset, 0, 110);
        if(drawBottom) drawMiddle(pGuiGraphics, texture, leftPos, topPos, imageWidth, imageHeight, textureWidth, textureHeight);
    }

    public abstract void drawMiddle(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight);
}
