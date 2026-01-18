package net.dinomine.potioneer.beyonder.pages;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public abstract class Page {
    public enum Chapter{
        BEYONDER,
        RITUALS,
        GODS,
        KNOWN_DEITIES,
        CHARMS,
        CHARACTERISTICS,
        LORE
    }
    public Chapter chapter;
    private Component title;

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

    public abstract void draw(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight);
}
