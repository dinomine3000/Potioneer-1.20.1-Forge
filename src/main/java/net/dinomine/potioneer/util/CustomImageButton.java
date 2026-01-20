package net.dinomine.potioneer.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

public class CustomImageButton extends ImageButton {
    private OnPress rClick;
    protected int posX;
    protected int posY;
    public CustomImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int diffText, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress lClick) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, diffText, pResourceLocation, pTextureWidth, pTextureHeight, lClick);
        this.posX = pX;
        this.posY = pY;
    }

    public CustomImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int diffText, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress lClick, OnPress rClick) {
        this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, diffText, pResourceLocation, pTextureWidth, pTextureHeight, lClick);
        this.rClick = rClick;
    }

    public CustomImageButton withRClick(OnPress rClick){
        this.rClick = rClick;
        return this;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(pButton)) {
                boolean flag = this.clicked(pMouseX, pMouseY);
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    if(pButton == InputConstants.MOUSE_BUTTON_LEFT) this.onPress();
                    else if(pButton == InputConstants.MOUSE_BUTTON_RIGHT && this.rClick != null) this.rClick.onPress(this);
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    protected boolean isValidClickButton(int pButton) {
        return pButton == InputConstants.MOUSE_BUTTON_LEFT || pButton == InputConstants.MOUSE_BUTTON_RIGHT;
    }
}
