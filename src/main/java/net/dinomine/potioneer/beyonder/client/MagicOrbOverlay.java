package net.dinomine.potioneer.beyonder.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dinomine.potioneer.Potioneer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RenderShape;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class MagicOrbOverlay {
    private static final ResourceLocation ORB = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/hud_orb_bg.png");
    private static final ResourceLocation ORB_OVERLAY = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/hud_orb_overlay.png");
    private static final ResourceLocation MANA = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/spirituality.png");

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar() {
        return true;
    }

    public static final IGuiOverlay HUD_MAGIC = ((forgeGui, guiGraphics, partialTick, width, height) -> {
        if(!shouldDisplayBar()) return;


        int yOffset = minecraft.getWindow().getGuiScaledHeight() - 62;
        int offsetLeft = minecraft.getWindow().getGuiScaledWidth() - 107;

        /*RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, ORB);*/

        int manaOffsetX = offsetLeft + 10;
        int manaOffsetY = yOffset + 10;

        guiGraphics.blit(ORB, offsetLeft, yOffset, 0, 0, 64, 64,256, 128);
        guiGraphics.blit(MANA, manaOffsetX, manaOffsetY, 10, 10, 43, 43,256, 64);
        guiGraphics.blit(ORB_OVERLAY, offsetLeft, yOffset, 0, 0, 64, 64,256, 128);

    });
}
