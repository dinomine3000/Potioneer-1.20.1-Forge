package net.dinomine.potioneer.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CustomTextImageButton extends CustomImageButton{
    private Component text = null;
    private Font font = null;
    private boolean dropShadow = false;
    private int color = 0;
    public CustomTextImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int diffText, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress lClick) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, diffText, pResourceLocation, pTextureWidth, pTextureHeight, lClick);
    }

    public CustomTextImageButton withText(Component text, Font font, int color, boolean dropShadow){
        this.text = text;
        this.font = font;
        this.color = color;
        this.dropShadow = dropShadow;
        return this;
    }

    public void setText(Component text) {
        this.text = text;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if(text != null && visible){
            pGuiGraphics.drawString(this.font, text, this.getX() + this.width/2 - this.font.width(text)/2, this.getY() + (this.height - this.font.lineHeight)/2, color, dropShadow);
        }
    }
}
