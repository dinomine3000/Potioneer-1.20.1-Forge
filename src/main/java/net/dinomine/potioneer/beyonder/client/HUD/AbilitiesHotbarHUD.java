package net.dinomine.potioneer.beyonder.client.HUD;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class AbilitiesHotbarHUD {
    private static final ResourceLocation ICONS = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_icon_atlas.png");
    private static int WIDTH = 256;
    private static int HEIGHT = 256;
    private static int SIZE = 16;

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar() {
        return ClientAbilitiesData.showHotbar;
    }


    public static final IGuiOverlay ABILITY_HOTBAR = ((forgeGui, guiGraphics, partialTick, width, height) -> {
        if(!shouldDisplayBar()) return;

        int yOffset = 25;
        int xOffset = minecraft.getWindow().getGuiScaledWidth()/2;

        int caret = ClientAbilitiesData.getCaret();
        AbilityInfo infoL = ClientAbilitiesData.getAbilityAt(caret - 1);
        AbilityInfo infoC = ClientAbilitiesData.getAbilityAt(caret);
        AbilityInfo infoR = ClientAbilitiesData.getAbilityAt(caret + 1);

        guiGraphics.blit(ICONS, xOffset - 40, yOffset + 8, infoL.posX(), infoL.posY(),
                SIZE, SIZE, WIDTH, HEIGHT);

        //48 x 60
        guiGraphics.blit(ICONS, xOffset - 24, yOffset - 10, 48, 60, 0, 0, 24, 30, WIDTH, HEIGHT);
        guiGraphics.blit(ICONS, xOffset - 16, yOffset, 32, 32, infoC.posX(), infoC.posY(), SIZE, SIZE, WIDTH, HEIGHT);
        String name = infoC.name();
        int offset = name.length()*5/2;
        guiGraphics.drawString(minecraft.font, infoC.name(), xOffset - offset, yOffset + 39, 0, false);

        guiGraphics.blit(ICONS, xOffset + 24, yOffset + 8, infoR.posX(), infoR.posY(),
                SIZE, SIZE, WIDTH, HEIGHT);
    });
}
