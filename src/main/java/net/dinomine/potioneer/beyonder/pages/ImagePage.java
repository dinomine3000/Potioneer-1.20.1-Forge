package net.dinomine.potioneer.beyonder.pages;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ImagePage extends SplitPage {
    private ResourceLocation resourceLocation;
    private int uPos, vPos, width, height;
    private float scale = 1f;
    private int texX = 256, texY = 256;

    public ImagePage(Chapter chapter, Component title, Component topText, Component bottomText, ResourceLocation imageLocation, int uPos, int vPos, int width, int height) {
        super(chapter, title, topText, bottomText);
        this.resourceLocation = imageLocation;
        this.uPos = uPos;
        this.vPos = vPos;
        this.width = width;
        this.height = height;
    }

    public ImagePage withDimensions(int textureWidth, int textureHeight){
        this.texX = textureWidth;
        this.texY = textureHeight;
        return this;
    }

    public ImagePage withScale(float scale){
        this.scale = scale;
        return this;
    }

    @Override
    public void drawMiddle(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        int blitW = (int)(scale * width);
        int blitH = (int)(scale * height);
        pGuiGraphics.blit(resourceLocation, leftPos + 55 - blitW/2, topPos + 63 - blitH/2, blitW, blitH, uPos, vPos, width, height, texX, texY);
    }
}
