package net.dinomine.potioneer.beyonder.client.HUD;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityFactory;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientConfigData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.client.screen.BeyonderSettingsScreen;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.config.PotioneerClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class AbilitiesHotbarHUD {
    private static final ResourceLocation ICONS = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_icon_atlas.png");
    public static int ICONS_WIDTH = 180;
    public static int ICONS_HEIGHT = 512;
    public static int ICON_WIDTH = 16;
    public static int ICON_HEIGHT = 24;
    private static final int CASE_WIDTH = 26;
    private static final int CASE_HEIGHT = 32;
    private static final int CAST_WIDTH = 13;
    private static final int CAST_HEIGHT = 32;

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar() {
        return (ClientAbilitiesData.showHotbar || minecraft.screen instanceof BeyonderSettingsScreen) && Minecraft.getInstance().player != null && ClientStatsData.getPathwaySequenceId() > -1 && !ClientAbilitiesData.getHotbar().isEmpty();
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
        int caret = ClientAbilitiesData.getCaret();
        AbilityInfo infoL = ClientAbilitiesData.getAbilityAt(caret - 1);
        AbilityInfo infoC = ClientAbilitiesData.getAbilityAt(caret);
        AbilityInfo infoR = ClientAbilitiesData.getAbilityAt(caret + 1);

        float scale = (float)ClientConfigData.getCurrentHotbarScale();
        PotioneerClientConfig.HOTBAR_POS hotbarPos = ClientConfigData.getHotbarPosition();

        int yOffset = (int) ((-70*scale + scale*(90*ClientAbilitiesData.openingAnimationPercent)));
        int xOffset = minecraft.getWindow().getGuiScaledWidth()/2;

        if(hotbarPos == PotioneerClientConfig.HOTBAR_POS.LEFT){
            //TODO maybe deal with integer/float division here?
            xOffset = (int) (scale*(CASE_WIDTH/2 -70 + (90*ClientAbilitiesData.openingAnimationPercent)));
            yOffset = minecraft.getWindow().getGuiScaledHeight()/2 + (int)(10*scale*(CASE_HEIGHT/2 - 20));
        } else if(hotbarPos == PotioneerClientConfig.HOTBAR_POS.RIGHT){
            xOffset = (int) ((minecraft.getWindow().getGuiScaledWidth() + scale*70 - CASE_WIDTH/2 - (scale*90*ClientAbilitiesData.openingAnimationPercent)));
            yOffset = minecraft.getWindow().getGuiScaledHeight()/2 + (int)(10*scale*(CASE_HEIGHT/2 - 20));
        }

        drawCases(guiGraphics, hotbarPos, animPercent, caret, xOffset, yOffset, infoL, infoC, infoR, scale);


    });

    private static void drawCases(GuiGraphics guiGraphics, PotioneerClientConfig.HOTBAR_POS hotbarPos, float animPercent, int caret, int xOffset, int yOffset, AbilityInfo infoL, AbilityInfo infoC, AbilityInfo infoR, float scale){
        if(hotbarPos == PotioneerClientConfig.HOTBAR_POS.LEFT){
            xOffset += (int) (10*scale);
            if(animPercent < 0){
                drawAbility(guiGraphics, ClientAbilitiesData.getAbilityAt(caret + 2), caret + 2,
                        xOffset,
                        (int) (yOffset + 120*scale + (int)(48*scale*animPercent)),
                        -animPercent*scale);
            }
            if(animPercent > 0){
                drawAbility(guiGraphics, ClientAbilitiesData.getAbilityAt(caret - 2), caret - 2,
                        xOffset,
                        (int) (yOffset -80*scale + (int)(40*scale*animPercent)),
                        animPercent*scale);
            }
            //yOffset + 10 - (int)(10*animPercent)
            //(xOffset - 60) + (int)(animPercent*60)
            int diff1 = (int)(scale * 40);
            int diff2 = (int)(scale * 72);
            drawAbility(guiGraphics, infoL, caret - 1,
                    xOffset,
                    (yOffset - diff1) + (int)(animPercent*diff1),
                    scale*(1f + animPercent));
            drawAbility(guiGraphics, infoC, caret,
                    xOffset,
                    yOffset + (int)(animPercent < 0 ? animPercent*diff1 : animPercent*diff2),
                    scale*(2 - Math.abs(animPercent)));
            drawAbility(guiGraphics, infoR, caret + 1,
                    xOffset,
                    yOffset + diff2 + (int)(animPercent*diff2),
                    scale*(1f - animPercent));
        }
        else if(hotbarPos == PotioneerClientConfig.HOTBAR_POS.RIGHT){
            xOffset -= (int) (10*scale);
            if(animPercent < 0){
                drawAbility(guiGraphics, ClientAbilitiesData.getAbilityAt(caret + 2), caret + 2,
                        xOffset,
                        (int) (yOffset + 120*scale + (int)(40*scale*animPercent)),
                        -animPercent*scale);
            }
            if(animPercent > 0){
                drawAbility(guiGraphics, ClientAbilitiesData.getAbilityAt(caret - 2), caret - 2,
                        xOffset,
                        (int) (yOffset -80*scale + (int)(40*scale*animPercent)),
                        animPercent);
            }
            //yOffset + 10 - (int)(10*animPercent)
            //(xOffset - 60) + (int)(animPercent*60)
            int diff = (int)(scale*40);
            drawAbility(guiGraphics, infoL, caret - 1,
                    xOffset,
                    (yOffset - diff) + (int)(animPercent*diff),
                    scale*(1f + animPercent));
            int diff2 = (int)(scale*72);
            drawAbility(guiGraphics, infoC, caret,
                    xOffset,
                    yOffset + (int)(animPercent < 0 ? animPercent*diff : animPercent*diff2),
                    scale*(2 - Math.abs(animPercent)));
            drawAbility(guiGraphics, infoR, caret + 1,
                    xOffset,
                    yOffset + diff2 + (int)(animPercent*diff2),
                    scale*(1f - animPercent));

        }
        else {
            int diff = (int) (60*scale);

            if(animPercent < 0){
                drawAbility(guiGraphics, ClientAbilitiesData.getAbilityAt(caret + 2), caret + 2,
                        xOffset + (int)(diff*1.5f) + (int)(diff*animPercent/2f),
                        yOffset + (int)(20*scale) + (int)(animPercent*10*scale), -animPercent*scale);
            }
            if(animPercent > 0){
                drawAbility(guiGraphics, ClientAbilitiesData.getAbilityAt(caret - 2), caret - 2,
                        xOffset - (int)(diff*1.5f) + (int)(diff*animPercent/2f),
                        yOffset + (int)(20*scale) - (int)(animPercent*10*scale),
                        animPercent*scale);
            }

            drawAbility(guiGraphics, infoL, caret - 1,
                    (xOffset - diff) + (int)(animPercent*diff),
                    yOffset + (int)(10*scale) - (int)(10*animPercent*scale),
                    (1f + animPercent)*scale);
            drawAbility(guiGraphics, infoC, caret,
                    xOffset + (int)(animPercent*diff),
                    yOffset + (int)(10*scale*(Math.abs(animPercent))),
                    (2 - Math.abs(animPercent))*scale);
            drawAbility(guiGraphics, infoR, caret + 1,
                    xOffset + diff + (int)(animPercent*diff),
                    yOffset + (int)(10*scale) + (int)(10*animPercent*scale),
                    (1f - animPercent)*scale);

        }

    }

    public static void drawAbility(GuiGraphics guiGraphics, AbilityInfo info, int caret, int xPos, int yPos, float scale){
        int abilityX = Pathways.getPathwayById(info.getPathwayId()).getAbilityX();
        int caseX = xPos - (int) (CASE_WIDTH * scale / 2);

        //ability cast (primary vs secondary) shape
        AbilityFactory abl = Abilities.getAbilityFactory(info.getKey());
        if(abl.getHasSecondaryFunction()){
            float pPercent = ClientAbilitiesData.getPercent(true);
            float sPercent = ClientAbilitiesData.getPercent(false);
            int pCastHeight = (int)(CAST_HEIGHT*(1-pPercent));
            int sCastHeight = (int)(CAST_HEIGHT*(1-sPercent));
            guiGraphics.blit(ICONS, caseX, yPos + (int)(scale * (CAST_HEIGHT - pCastHeight)),
                    (int) (CAST_WIDTH*scale), (int) (pCastHeight*scale), 151, pPercent != 0 ? 73 - pCastHeight : 3,
                    CAST_WIDTH, pCastHeight, ICONS_WIDTH, ICONS_HEIGHT);
            guiGraphics.blit(ICONS, (caseX +  (int) (CASE_WIDTH*scale/2f)), yPos + (int)(scale * (CAST_HEIGHT - sCastHeight)),
                    (int) (CAST_WIDTH*scale), (int) (sCastHeight*scale), 164, sPercent != 0 ? 73 - sCastHeight : 3,
                    CAST_WIDTH, sCastHeight, ICONS_WIDTH, ICONS_HEIGHT);
        }

        //48 x 60 - case
        guiGraphics.blit(ICONS, caseX, yPos, (int) (CASE_WIDTH*scale), (int) (CASE_HEIGHT*scale), abilityX - 5, 0, CASE_WIDTH, CASE_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);

        //ability icon
        if(!ClientAbilitiesData.isEnabled(caret)){
            RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, 1.0F); // Greyscale tint
        }
        ResourceLocation AbilityIcon = Abilities.getAbilityFactory(info.innerId()).getTextureLocation();
        guiGraphics.blit(AbilityIcon, caseX + (int) (5*scale), yPos + (int)(4*scale), (int)(ICON_WIDTH*scale), (int)(ICON_HEIGHT*scale), abilityX, abl.getPosY(), ICON_WIDTH, ICON_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);

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
        float spir = ClientStatsData.getPlayerSpirituality();
        int cost = abl.getCostFunction().apply(info.getSequenceLevel());
        if(!ClientAbilitiesData.isEnabled(caret) || spir < cost){
//            guiGraphics.blit(ICONS, caseX + (int) (5*scale), yPos + (int)(4*scale), (int)(ICON_WIDTH*scale), (int)(ICON_HEIGHT*scale), 130, 32, ICON_WIDTH, ICON_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);

            guiGraphics.fillGradient(caseX + (int) (5*scale), yPos + (int) (4*scale),
                    (int) (caseX + 5*scale + ICON_WIDTH*scale), (int) (yPos  + 4*scale + ICON_HEIGHT*scale), 0x99707070, 0x99404040);
        }


        //cooldown gradient
        float percent = Mth.clamp(1 - ((float) ClientAbilitiesData.getCooldown(caret) / ClientAbilitiesData.getMaxCooldown(caret)), 0, 1);
        guiGraphics.fillGradient(caseX + (int) (5*scale), (int) (yPos + (int) (4*scale) + (percent)*ICON_HEIGHT*scale),
                (int) (caseX + (int) (5*scale) + ICON_WIDTH*scale), (int) (yPos + (int) (4*scale) + ICON_HEIGHT*scale), 0xDD696969, 0xDD424242);

        //barrier symbol if ability is disabled
        if(ClientAbilitiesData.getCooldown(caret) < 0){
            //Copied from the icons part
            guiGraphics.blit(ICONS, caseX + (int) (5*scale), yPos + (int)(4*scale), (int)(ICON_WIDTH*scale), (int)(ICON_HEIGHT*scale), 130, 4, ICON_WIDTH, ICON_HEIGHT, ICONS_WIDTH, ICONS_HEIGHT);
        }
    }
}
