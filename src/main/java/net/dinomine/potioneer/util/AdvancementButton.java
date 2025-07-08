package net.dinomine.potioneer.util;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

public class AdvancementButton extends ImageButton {
    public AdvancementButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, ResourceLocation pResourceLocation, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, 0, pResourceLocation, pOnPress);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.active && this.visible) {
            boolean flag = this.clicked(pMouseX, pMouseY);
            if (flag) {
                this.onClick(pMouseX, pMouseY);
                return true;
            }

        }
        return false;
    }
}
