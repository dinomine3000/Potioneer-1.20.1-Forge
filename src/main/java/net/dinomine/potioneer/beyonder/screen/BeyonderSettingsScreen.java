package net.dinomine.potioneer.beyonder.screen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BeyonderSettingsScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + Potioneer.MOD_ID + ".options_menu");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/client_config_menu.png");

    private final int imageWidth, imageHeight;
    private final int TEXTURE_WIDTH, TEXTURE_HEIGHT;
    private int leftPos, topPos;

    private int offsetSliderStartX;
    private int offsetSliderEndX;

    private Button goToMainMenu;
    private Button goToAbilitiesMenu;

    public BeyonderSettingsScreen() {
        super(TITLE);
        this.imageWidth = 177;
        this.imageHeight = 183;
        this.TEXTURE_WIDTH = 286;
        this.TEXTURE_HEIGHT = 256;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(pKeyCode == 69 || pKeyCode == KeyBindings.INSTANCE.beyonderMenuKey.getKey().getValue()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height- this.imageHeight) / 2;

        goToMainMenu = new ImageButton(leftPos + 4, topPos + 165, 43, 18,
                163, 208, 0, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> {BeyonderScreen.goToMainMenu();});
        addRenderableWidget(goToMainMenu);
        goToAbilitiesMenu = new ImageButton(leftPos + 47, topPos + 165, 42, 18,
                234, 219, 0, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> {BeyonderScreen.goToAbilities();});
        addRenderableWidget(goToAbilitiesMenu);

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0,
                imageWidth, imageHeight,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
        pGuiGraphics.blit(TEXTURE, leftPos + 10, topPos + 30, 0, 204,
                170, 13,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
        pGuiGraphics.drawString(this.font, Component.translatable("gui.potioneer.options_menu"),
                leftPos + imageWidth/2 - this.font.width(Component.translatable("gui.potioneer.options_menu"))/2,
                topPos + 10, 0, false);
    }
}
