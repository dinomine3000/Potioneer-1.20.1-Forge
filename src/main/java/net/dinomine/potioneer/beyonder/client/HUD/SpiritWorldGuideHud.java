package net.dinomine.potioneer.beyonder.client.HUD;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.worldgen.dimension.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class SpiritWorldGuideHud extends AbilityDataHud {
        private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_huds.png");

    @Override
    boolean shouldRender(Minecraft instance, LocalPlayer player) {
        return player.level().dimension().equals(ModDimensions.SPIR_WORLD_LEVEL_KEY);
    }

    @Override
    int render(ForgeGui var1, GuiGraphics guiGraphics, float partialTick) {
        int height = 50;
        guiGraphics.blitNineSlicedSized(TEXTURE, leftPos, topPos, width, height, 5, 49, 49, 110, 0, 256, 256);
        //guiGraphics.fill(leftPos, topPos, leftPos + width, topPos + height, 0xff0000ff);
        Font font = Minecraft.getInstance().font;

        String tx1 = "Real World Coordinates:";
        guiGraphics.drawString(font, tx1, leftPos + width/2 - font.width(tx1)/2, topPos + height/2 - font.lineHeight/2 - 5, 0, false);

        String tx = "%s, %s".formatted(
                Math.floor(Minecraft.getInstance().player.position().x*32),
                Math.floor(Minecraft.getInstance().player.position().z*32)
        );
        int txWidth = font.width(tx);
        guiGraphics.drawString(font, tx, leftPos + width/2 - txWidth/2, topPos + height/2-font.lineHeight/2 + 5, 0, false);
        return height;
    }
}
