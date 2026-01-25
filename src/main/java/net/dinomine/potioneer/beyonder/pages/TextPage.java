package net.dinomine.potioneer.beyonder.pages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TextPage extends Page{
    private final Component text;

    public TextPage(Chapter chapter, Component translatableTitle, Component translatableText){
        super(chapter, translatableTitle);
        this.text = translatableText;
    }

    public TextPage(Chapter chapter, String id){
        super(chapter, id);
        this.text = Component.translatable("contents.potioneer." + id);
    }

    @Override
    public void draw(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        pGuiGraphics.drawWordWrap(Minecraft.getInstance().font, text, leftPos, topPos, 120, 0);
    }
}
