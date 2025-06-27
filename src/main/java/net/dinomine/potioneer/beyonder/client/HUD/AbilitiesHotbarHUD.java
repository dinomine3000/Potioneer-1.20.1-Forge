package net.dinomine.potioneer.beyonder.client.HUD;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class AbilitiesHotbarHUD {
    private static final ResourceLocation ICONS = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_icon_atlas.png");
    public static int ICONS_WIDTH = 146;
    public static int ICONS_HEIGHT = 256;
    public static int ICON_WIDTH = 16;
    public static int ICON_HEIGHT = 24;
    private static int CASE_WIDTH = 26;
    private static int CASE_HEIGHT = 32;

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar() {
        return ClientAbilitiesData.showHotbar && ClientStatsData.getPathwayId() > -1 && !ClientAbilitiesData.getHotbar().isEmpty();
    }

    public static final IGuiOverlay ABILITY_HOTBAR = ((forgeGui, guiGraphics, partialTick, width, height) -> {
        if(minecraft.isPaused()){
            ClientAbilitiesData.showHotbar = false;
            return;
        }

        ClientAbilitiesData.animationTick(4*minecraft.getDeltaFrameTime());
        if(!shouldDisplayBar()) return;


        // 0 -> animation done, stuff should be in its position
        // 1 -> animation just started, caret moved up, stuff should be offset to be to the right of their spot
        // -1 -> animation just started, caret moved down, stuff should be offset to be to the left of their spot
        float animPercent = ClientAbilitiesData.animationTime / ClientAbilitiesData.maxAnimationtime;

        int yOffset = (int) (-65 + (90*ClientAbilitiesData.openingAnimationPercent));
        int xOffset = minecraft.getWindow().getGuiScaledWidth()/2;

        int caret = ClientAbilitiesData.getCaret();
        AbilityInfo infoL = ClientAbilitiesData.getAbilityAt(caret - 1);
        AbilityInfo infoC = ClientAbilitiesData.getAbilityAt(caret);
        AbilityInfo infoR = ClientAbilitiesData.getAbilityAt(caret + 1);

        if(animPercent < 0){
            drawAbility(guiGraphics, ClientAbilitiesData.getAbilityAt(caret + 2), caret + 2,
                    xOffset + 90 + (int)(30*animPercent), yOffset + 20 + (int)(animPercent*10), -animPercent);
        }
        if(animPercent > 0){
            drawAbility(guiGraphics, ClientAbilitiesData.getAbilityAt(caret - 2), caret - 2,
                    xOffset - 90 + (int)(30*animPercent), yOffset + 20 - (int)(animPercent*10), animPercent);
        }

        drawAbility(guiGraphics, infoL, caret - 1, (xOffset - 60) + (int)(animPercent*60),
                yOffset + 10 - (int)(10*animPercent), 1f + animPercent);
        drawAbility(guiGraphics, infoC, caret, xOffset + (int)(animPercent*60),
                yOffset + (int)(10*(Math.abs(animPercent))), 2 - Math.abs(animPercent));
        drawAbility(guiGraphics, infoR, caret + 1, xOffset + 60 + (int)(animPercent*60),
                yOffset + 10 + (int)(10*animPercent), 1f - animPercent);

    });

    public static void drawAbility(GuiGraphics guiGraphics, AbilityInfo info, int caret, int xPos, int yPos, float scale){

        //48 x 60 - case
        int pathway = Math.floorDiv(info.id(), 10);
        int caseX = xPos - (int) (CASE_HEIGHT * scale / 2);
        guiGraphics.blit(ICONS, caseX, yPos, (int) (CASE_WIDTH*scale), (int) (CASE_HEIGHT*scale), 26*pathway, 0, CASE_WIDTH, CASE_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);

        //ability icon
        if(!ClientAbilitiesData.isEnabled(caret, true)){
            RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, 1.0F); // Greyscale tint
        }
        guiGraphics.blit(ICONS, caseX + (int) (5*scale), yPos + (int)(4*scale), (int)(ICON_WIDTH*scale), (int)(ICON_HEIGHT*scale), info.posX(), info.posY(), ICON_WIDTH, ICON_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F); // Reset color

        //name render
//        String name = info.name();
//        float size = 0.6f*scale;
//        int offset = (int) ((xPos - minecraft.font.width(name)*size / 2f));
//        Matrix4f mat = new Matrix4f(
//                size, 0, 0, 0,
//                0, size, 0, 0,
//                0, 0, size, 0,
//                0, 0, 0, size
//        );
//        mat = mat.mul(guiGraphics.pose().last().pose());
//        minecraft.font.drawInBatch(name, offset/size, (yPos + (24*scale))/size, 0, false,
//                mat, guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0,
//                15728880, minecraft.font.isBidirectional());
//        guiGraphics.drawString(minecraft.font, name, offset, yPos + (24*scale), 0, false);

        //disabled gradient
        if(!ClientAbilitiesData.isEnabled(caret, true)){
//            guiGraphics.blit(ICONS, caseX + (int) (5*scale), yPos + (int)(4*scale), (int)(ICON_WIDTH*scale), (int)(ICON_HEIGHT*scale), 130, 32, ICON_WIDTH, ICON_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);

            guiGraphics.fillGradient(caseX + (int) (5*scale), yPos + (int) (4*scale),
                    (int) (caseX + (int) (5*scale) + ICON_WIDTH*scale), (int) (yPos  + (int) (4*scale) + ICON_HEIGHT*scale), 0x99707070, 0x99404040);
        }


        float percent = Mth.clamp(1 - ((float) ClientAbilitiesData.getCooldown(caret, true) / ClientAbilitiesData.getMaxCooldown(caret, true)), 0, 1);
        //cooldown gradient

        guiGraphics.fillGradient(caseX + (int) (5*scale), (int) (yPos + (int) (4*scale) + (percent)*ICON_HEIGHT*scale),
                (int) (caseX + (int) (5*scale) + ICON_WIDTH*scale), (int) (yPos + (int) (4*scale) + ICON_HEIGHT*scale), 0xDD696969, 0xDD424242);

        //barrier symbol if ability is disabled
        if(ClientAbilitiesData.getCooldown(caret, true) < 0){
            //Copied from the icons part
            guiGraphics.blit(ICONS, caseX + (int) (5*scale), yPos + (int)(4*scale), (int)(ICON_WIDTH*scale), (int)(ICON_HEIGHT*scale), 130, 4, ICON_WIDTH, ICON_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);
        }
    }
}
