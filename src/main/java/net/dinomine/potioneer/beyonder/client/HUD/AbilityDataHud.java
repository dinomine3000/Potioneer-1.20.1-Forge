package net.dinomine.potioneer.beyonder.client.HUD;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;

@OnlyIn(Dist.CLIENT)
public abstract class AbilityDataHud {
    protected int height;
    protected int width;
    protected int topPos;
    protected int leftPos;

    public void setDimensions(int height, int width, int leftPos, int topPos){
        this.height = height;
        this.width = width;
        this.topPos = topPos;
        this.leftPos = leftPos;
    }
    abstract boolean shouldRender(Minecraft instance, LocalPlayer player);
    abstract int render(ForgeGui var1, GuiGraphics guiGraphics, float partialTick);
    public void trigger(){}

}
