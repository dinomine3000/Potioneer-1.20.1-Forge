package net.dinomine.potioneer.beyonder.pages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class SplitPage extends Page {
    private Component topText;
    private Component bottomText;

    public SplitPage(Chapter chapter, Component title, Component topText, Component bottomText) {
        super(chapter, title);
        this.topText = topText;
        this.bottomText = bottomText;
    }

    @Override
    public void draw(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        pGuiGraphics.drawWordWrap(Minecraft.getInstance().font, this.topText, leftPos, topPos, 110, 0);
        pGuiGraphics.drawWordWrap(Minecraft.getInstance().font, this.bottomText, leftPos, topPos + 95, 110, 0);
        drawMiddle(pGuiGraphics, texture, leftPos, topPos, imageWidth, imageHeight, textureWidth, textureHeight);
    }

    public abstract void drawMiddle(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight);
}
