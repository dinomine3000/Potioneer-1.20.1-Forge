package net.dinomine.potioneer.beyonder.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

public abstract class Page {
    public enum Chapter{
        BEYONDER,
        ABILITIES,
        DIVINATION,
        RITUALS,
        GODS,
        KNOWN_DEITIES,
        CHARMS,
        CHARACTERISTICS,
        LORE
    }
    public Chapter chapter;
    private final Component title;
    protected float scale = 0.8f;

    public Page withScale(float scale){
        this.scale = scale;
        return this;
    }


    public void drawScaledText(GuiGraphics pGuiGraphics, FormattedText text, int leftPos, int topPos, int color, int wordWrapWidth){
        PoseStack pose = pGuiGraphics.pose();
        pose.pushPose();
        float textScale = scale;
        pose.scale(textScale, textScale, textScale);
        pGuiGraphics.drawWordWrap(Minecraft.getInstance().font, text, (int) (leftPos/textScale), (int)(topPos/textScale), (int)(wordWrapWidth/textScale), color);
        pose.popPose();
    }

    public Component getTitle() {
        return this.title;
    }

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)) return true;
        if(!(obj instanceof Page page)) return false;
        return title.equals(page.title);
    }

    public Page(Chapter chapter, Component title){
        this.chapter = chapter;
        this.title = title;
    }

    public Page(Chapter chapter, String titleId){
        this.chapter = chapter;
        this.title = Component.translatable("page.potioneer." + titleId);
    }

    public abstract void draw(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight);
}
