package net.dinomine.potioneer.beyonder.pages;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPage extends SplitPage{
    private final Item renderItem;
    private float scale = 2;
    public ItemPage(Chapter chapter, Component title, Component topText, Component bottomText, Item stack) {
        super(chapter, title, topText, bottomText);
        this.renderItem = stack;
    }
    public ItemPage(Chapter chapter, String id, Item stack) {
        super(chapter, id);
        this.renderItem = stack;
    }

    public ItemPage withScale(float scale){
        this.scale = scale;
        return this;
    }

    @Override
    public void drawMiddle(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(-leftPos - 8, -topPos - 8, 0);
        pGuiGraphics.pose().scale(scale, scale, scale);
        pGuiGraphics.pose().translate(leftPos + 8, topPos + 8, 0);
        pGuiGraphics.renderItem(new ItemStack(renderItem), 16, 17);
        pGuiGraphics.pose().popPose();
    }
}
