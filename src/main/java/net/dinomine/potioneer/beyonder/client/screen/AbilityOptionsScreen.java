package net.dinomine.potioneer.beyonder.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.config.PotioneerClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class AbilityOptionsScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + Potioneer.MOD_ID + ".ability_options");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_options.png");
    private static final int DEADZONE = 20;
    private static final int ORB_SIDE_LENGTH = 38;

    private AbilityOptions options;
    private final AbilityKey abilityKey;
    private final boolean castPrimary;
    private int mouseX, mouseY;

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(pKeyCode == InputConstants.KEY_E || pKeyCode == InputConstants.KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public AbilityOptionsScreen(AbilityOptions options, AbilityKey keyToCast, boolean castPrimary) {
        super(TITLE);
        this.options = options;
        this.abilityKey = keyToCast;
        this.castPrimary = castPrimary;
    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        mouseX = pMouseX;
        mouseY = pMouseY;

        List<AbilityOptions> optionsList = options.getFurtherOptions();
        int noOptions = optionsList.size();
        if(noOptions < 2) return;

        AbilityOptions selectedOption = getHoveredOption(mouseX, mouseY);
        for(int i = 0; i < noOptions; i++){
            AbilityOptions currOption = optionsList.get(i);

            float angle = (float) ((i + 0.5)*2*Mth.PI/noOptions);
            float dist = 70;
            int deltaX = (int) (Mth.sin(angle)*dist);
            int deltaY = (int) (Mth.cos(angle) * dist);

            int posX = this.width/2 + deltaX - ORB_SIDE_LENGTH/2;
            int posY = this.height/2 - deltaY - ORB_SIDE_LENGTH/2;
            int cornerSize = 3;
            Component name = currOption.nameComponent;
            int width = this.font.width(name);

            if(selectedOption != null && selectedOption.is(currOption.name())){
                pGuiGraphics.blitNineSlicedSized(TEXTURE, posX + ORB_SIDE_LENGTH/2 - width/2 - cornerSize, posY + ORB_SIDE_LENGTH, width + cornerSize*2, this.font.lineHeight + 2*cornerSize, cornerSize, 8, 8, 54, 41, 94, 56);
                pGuiGraphics.blit(TEXTURE, posX, posY, 39, 0, ORB_SIDE_LENGTH, ORB_SIDE_LENGTH, 94, 56);
            } else {
                pGuiGraphics.blitNineSlicedSized(TEXTURE, posX + ORB_SIDE_LENGTH/2 - width/2 - cornerSize, posY + ORB_SIDE_LENGTH, width + cornerSize*2, this.font.lineHeight + 2*cornerSize, cornerSize, 8, 8, 15, 41, 94, 56);
                pGuiGraphics.blit(TEXTURE, posX, posY, 0, 0, ORB_SIDE_LENGTH, ORB_SIDE_LENGTH, 94, 56);
            }
            if(currOption.textureLocation != null)
                pGuiGraphics.blit(currOption.textureLocation, posX + ORB_SIDE_LENGTH/2 - currOption.sizeX/2, posY + ORB_SIDE_LENGTH/2 - currOption.sizeY/2, currOption.textureX, currOption.textureY, currOption.sizeX, currOption.sizeY, 180, 632);
            pGuiGraphics.drawString(this.font, name, posX + ORB_SIDE_LENGTH/2 - this.font.width(name)/2 + 1, posY + ORB_SIDE_LENGTH + cornerSize, 0xffffff, false);
        }

    }

    //this one is called only when i press Esc or E
    @Override
    public void onClose() {
        super.onClose();
    }


    //this one is called everytime it closes
    @Override
    public void removed() {
        super.removed();
        AbilityOptions selection = getHoveredOption(mouseX, mouseY);
        if(selection == null) return;
        if(selection.isFinalOption()){
            CompoundTag args = new CompoundTag();
            args.putString("option", selection.name());
            ClientAbilitiesData.useAbility(minecraft.player, abilityKey, castPrimary, args);
        } else {
            minecraft.tell(() -> {
                minecraft.setScreen(new AbilityOptionsScreen(selection, abilityKey, castPrimary));
            });
        }
    }

    private static double getGuiScale(){return PotioneerClientConfig.HOTBAR_SCALE.get();}

    private AbilityOptions getHoveredOption(int mouseX, int mouseY){
        if(new Vec2(mouseX, mouseY).distanceToSqr(new Vec2(this.width/2f, this.height/2f)) < DEADZONE * DEADZONE) return null;
        int numberOfOptions = options.getFurtherOptions().size();
        if(numberOfOptions == 1) return options.getFurtherOptions().get(0);

        float angleRad = (float) (Mth.atan2(mouseY - this.height/2f, mouseX - this.width/2f) + Mth.HALF_PI);
        if(angleRad < 0) angleRad += 2*Mth.PI;
        //angleRad - ]0, 2*Pi]
        float secSize = 2*Mth.PI / numberOfOptions;
        int sector = (int)(angleRad / secSize);
        return options.getFurtherOptions().get(sector);
    }

    public static void mouseRelease(){
        if(Minecraft.getInstance().screen instanceof AbilityOptionsScreen)
            Minecraft.getInstance().setScreen(null);
    }
}
