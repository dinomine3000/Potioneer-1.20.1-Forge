package net.dinomine.potioneer.beyonder.pages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class TextPage extends Page{
    private final Component text;

    public TextPage(Chapter chapter, Component translatableTitle, Component translatableText){
        super(chapter, translatableTitle);
        this.text = translatableText;
    }

    @Override
    public void draw(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        pGuiGraphics.drawWordWrap(Minecraft.getInstance().font, text, leftPos + 165, topPos + 15, 110, 0);
    }
}
