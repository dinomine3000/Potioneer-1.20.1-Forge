package net.dinomine.potioneer.beyonder.pages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class SplitPage extends Page {
    private final Component topText;
    private final Component bottomText;
    private int yOffset = 0;

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
        pGuiGraphics.drawWordWrap(Minecraft.getInstance().font, this.topText, leftPos, topPos, 110, 0);
        drawMiddle(pGuiGraphics, texture, leftPos, topPos, imageWidth, imageHeight, textureWidth, textureHeight);
        pGuiGraphics.drawWordWrap(Minecraft.getInstance().font, this.bottomText, leftPos, topPos + 95 - yOffset, 110, 0);
    }

    public abstract void drawMiddle(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight);
}
