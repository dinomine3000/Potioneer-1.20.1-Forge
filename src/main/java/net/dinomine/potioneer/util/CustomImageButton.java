package net.dinomine.potioneer.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class CustomImageButton extends ImageButton {
    private OnPress rClick;
    public CustomImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int diffText, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress lClick, @Nullable OnPress rClick) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, diffText, pResourceLocation, pTextureWidth, pTextureHeight, lClick);
        this.rClick = rClick;
    }

    public CustomImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int diffText, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress lClick) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, diffText, pResourceLocation, pTextureWidth, pTextureHeight, lClick);
        this.rClick = null;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(pButton)) {
                boolean flag = this.clicked(pMouseX, pMouseY);
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(pButton);
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public void onClick(int pButton) {
        if(pButton == InputConstants.MOUSE_BUTTON_LEFT) this.onPress();
        else if(pButton == InputConstants.MOUSE_BUTTON_RIGHT) this.rClick.onPress(this);
    }

    @Override
    protected boolean isValidClickButton(int pButton) {
        return pButton == InputConstants.MOUSE_BUTTON_LEFT || pButton == InputConstants.MOUSE_BUTTON_RIGHT;
    }
}
