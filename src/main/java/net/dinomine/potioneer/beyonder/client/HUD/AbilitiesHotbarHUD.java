package net.dinomine.potioneer.beyonder.client.HUD;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.apache.logging.log4j.core.layout.HtmlLayout;
import org.joml.Matrix4f;

public class AbilitiesHotbarHUD {
    private static final ResourceLocation ICONS = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_icon_atlas.png");
    private static int WIDTH = 256;
    private static int HEIGHT = 256;
    private static int SIZE = 16;
    private static int CASE_WIDTH = 24;
    private static int CASE_HEIGHT = 30;

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar() {
        return ClientAbilitiesData.showHotbar && ClientStatsData.getPathwayId() > -1 && ClientAbilitiesData.hasAbilities();
    }


    public static final IGuiOverlay ABILITY_HOTBAR = ((forgeGui, guiGraphics, partialTick, width, height) -> {
        if(minecraft.isPaused()) return;
        ClientAbilitiesData.animationTick(partialTick);
        if(!shouldDisplayBar()) return;


        // 0 -> animation done, stuff should be in its position
        // 1 -> animation just started, caret moved up, stuff should be offset to be to the right of their spot
        // -1 -> animation just started, caret moved down, stuff should be offset to be to the left of their spot
        float animPercent = ClientAbilitiesData.animationTime / ClientAbilitiesData.maxAnimationtime;

        int yOffset = 25;
        int xOffset = minecraft.getWindow().getGuiScaledWidth()/2;

        int caret = ClientAbilitiesData.getCaret();
        AbilityInfo infoL = ClientAbilitiesData.getAbilityAt(caret - 1);
        AbilityInfo infoC = ClientAbilitiesData.getAbilityAt(caret);
        AbilityInfo infoR = ClientAbilitiesData.getAbilityAt(caret + 1);

        if(animPercent < 0){
            drawAbility(guiGraphics, ClientAbilitiesData.getAbilityAt(caret - 2), caret - 2,
                    xOffset + 90 + (int)(30*animPercent), yOffset + 20 + (int)(animPercent*10), -animPercent);
        }
        if(animPercent > 0){
            drawAbility(guiGraphics, ClientAbilitiesData.getAbilityAt(caret + 2), caret + 2,
                    xOffset - 90 + (int)(30*animPercent), yOffset + 20 - (int)(animPercent*10), animPercent);
        }

        drawAbility(guiGraphics, infoL, caret - 1, (xOffset - 60) + (int)(animPercent*60),
                yOffset + 10 - (int)(10*animPercent), 1f + animPercent);
        drawAbility(guiGraphics, infoC, caret, xOffset + (int)(animPercent*60),
                yOffset + (int)(10*(Math.abs(animPercent))), 2 - Math.abs(animPercent));
        drawAbility(guiGraphics, infoR, caret + 1, xOffset + 60 + (int)(animPercent*60),
                yOffset + 10 + (int)(10*animPercent), 1f - animPercent);

    });

    private static void drawAbility(GuiGraphics guiGraphics, AbilityInfo info, int caret, int xPos, int yPos, float scale){

        //48 x 60 - case
        int caseX = xPos - (int) (12 * scale);
        guiGraphics.blit(ICONS, caseX, yPos, (int) (CASE_WIDTH*scale), (int) (CASE_HEIGHT*scale), 0, 0, CASE_WIDTH, CASE_HEIGHT, WIDTH, HEIGHT);
        //ability icon
        guiGraphics.blit(ICONS, xPos - (int) (SIZE*scale / 2), yPos + (int)(5*scale), (int)(SIZE*scale), (int)(SIZE*scale), info.posX(), info.posY(), SIZE, SIZE, WIDTH, HEIGHT);
        //name render
        String name = info.name();
        float size = 0.6f*scale;
        int offset = (int) ((xPos - minecraft.font.width(name)*size / 2f));
        Matrix4f mat = new Matrix4f(
                size, 0, 0, 0,
                0, size, 0, 0,
                0, 0, size, 0,
                0, 0, 0, size
        );
        mat = mat.mul(guiGraphics.pose().last().pose());
        minecraft.font.drawInBatch(name, offset/size, (yPos + (24*scale))/size, 0, false,
                mat, guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0,
                15728880, minecraft.font.isBidirectional());
//        guiGraphics.drawString(minecraft.font, name, offset, yPos + (24*scale), 0, false);

        if(!ClientAbilitiesData.isEnabled(caret)){
            guiGraphics.fillGradient(caseX, yPos, (int) (caseX + CASE_WIDTH*scale), (int) (yPos + CASE_HEIGHT*scale), 0xDD999999, 0xDD666666);
        }

        //cooldown gradient
        float percent = 1 - ((float) ClientAbilitiesData.getCooldown(caret) / ClientAbilitiesData.getMaxCooldown(caret));

        guiGraphics.fillGradient(caseX, (int) (yPos + (percent)*CASE_HEIGHT*scale), (int) (caseX + CASE_WIDTH*scale), (int) (yPos + CASE_HEIGHT*scale), 0xDD696969, 0xDD424242);

    }
}
