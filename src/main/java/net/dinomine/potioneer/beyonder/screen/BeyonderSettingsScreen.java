package net.dinomine.potioneer.beyonder.screen;

import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientConfigData;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.config.PotioneerClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static net.dinomine.potioneer.beyonder.client.ClientAbilitiesData.showHotbarOnConfigScreen;

public class BeyonderSettingsScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + Potioneer.MOD_ID + ".options_menu");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/client_config_menu.png");

    private final int imageWidth, imageHeight;
    private final int TEXTURE_WIDTH, TEXTURE_HEIGHT;
    private int leftPos, topPos;

    private int offsetSliderStartX;
    private int offsetSliderEndX;
    private int currentSliderX = 0;
    private int sliderRange;
    private boolean sliding = false;

    private int hotbarSliderStartX;
    private int hotbarSliderEndX;
    private int currentHotbarSliderX = 0;
    private int hotbarSliderRange;
    private boolean hotbarSliding = false;

    private Button orbLeftButton;
    private Button orbRightButton;
    private Button orbSizeIncreaseButton;
    private Button orbSizeDecreaseButton;

    private Button hotbarPositionLeft;
    private Button hotbarPositionRight;
    private Button hotbarPositionTop;

    private Button goToMainMenu;
    private Button goToAbilitiesMenu;

    private Button saveButton;

    public BeyonderSettingsScreen() {
        super(TITLE);
        this.imageWidth = 176;
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
        showHotbarOnConfigScreen(true);

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height- this.imageHeight) / 2;

        this.offsetSliderStartX = leftPos + 8;
        this.offsetSliderEndX = leftPos + 161;
        this.sliderRange = offsetSliderEndX - offsetSliderStartX;
        this.currentSliderX = leftPos + 8 + (int) (sliderRange * ClientConfigData.getCurrentOffset());

        this.hotbarSliderStartX = leftPos + 8;
        this.hotbarSliderEndX = leftPos + 161;
        this.hotbarSliderRange = hotbarSliderEndX - hotbarSliderStartX;
        this.currentHotbarSliderX = leftPos + 8 + (int) (hotbarSliderRange * ((ClientConfigData.getCurrentHotbarScale()-1)/4f));

        //sub buttons
        goToMainMenu = new ImageButton(leftPos + 4, topPos + 165, 43, 18,
                163, 208, 0, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> {BeyonderScreen.goToMainMenu();});
        addRenderableWidget(goToMainMenu);
        goToAbilitiesMenu = new ImageButton(leftPos + 47, topPos + 165, 42, 18,
                234, 219, 0, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> {BeyonderScreen.goToAbilities();});
        addRenderableWidget(goToAbilitiesMenu);

        //save button
        Component saveButtonName = Component.translatable("gui.potioneer.options_save_button");
        saveButton = Button.builder(saveButtonName, button -> saveData())
                .pos(leftPos + 88 - this.font.width(saveButtonName)/2, topPos + 145)
                .size(this.font.width(saveButtonName), 15).build();
        addRenderableWidget(saveButton);

        //orb margin buttons
        int textWidth = this.font.width(Component.translatable("gui.potioneer.options_orb")) + 5;
        orbLeftButton = new ImageButton(leftPos + 6 + textWidth, topPos + 48, 13, 13,
                40, 217, 13, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> setOrbOnRight(false));
        addRenderableWidget(orbLeftButton);
        orbRightButton = new ImageButton(leftPos + 20 + textWidth, topPos + 48, 13, 13,
                53, 217, 13, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> setOrbOnRight(true));
        addRenderableWidget(orbRightButton);
        orbSizeIncreaseButton = new ImageButton(leftPos + 35 + textWidth, topPos + 42, 13, 13,
                66, 217, 13, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> increaseOrbSize(true));
        addRenderableWidget(orbSizeIncreaseButton);
        orbSizeDecreaseButton = new ImageButton(leftPos + 35 + textWidth, topPos + 54, 13, 13,
                79, 217, 13, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> increaseOrbSize(false));
        addRenderableWidget(orbSizeDecreaseButton);

        //hotbar buttons
        hotbarPositionLeft = new ImageButton(leftPos + 8, topPos + 75, 13, 13,
                40, 217, 13, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> setHotbarPosition(PotioneerClientConfig.HOTBAR_POS.LEFT));
        addRenderableWidget(hotbarPositionLeft);
        hotbarPositionTop = new ImageButton(leftPos + 23, topPos + 75, 13, 13,
                66, 217, 13, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> setHotbarPosition(PotioneerClientConfig.HOTBAR_POS.TOP));
        addRenderableWidget(hotbarPositionTop);
        hotbarPositionRight = new ImageButton(leftPos + 38, topPos + 75, 13, 13,
                53, 217, 13, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, btn -> setHotbarPosition(PotioneerClientConfig.HOTBAR_POS.RIGHT));
        addRenderableWidget(hotbarPositionRight);

    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        sliding = false;
        hotbarSliding = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(pMouseX > offsetSliderStartX && pMouseX < offsetSliderEndX
                && pMouseY > topPos + 30 && pMouseY < topPos + 43){
            sliding = true;
            currentSliderX = Mth.clamp((int)pMouseX, offsetSliderStartX, offsetSliderEndX);
            ClientConfigData.setNewOffset(Mth.clamp((float) ((pMouseX - leftPos)/sliderRange), 0, 1));
            return true;
        } else if(pMouseX > hotbarSliderStartX && pMouseX < hotbarSliderEndX
                && pMouseY > topPos + 100 && pMouseY < topPos + 113){
            hotbarSliding = true;
            currentHotbarSliderX = Mth.clamp((int)pMouseX, hotbarSliderStartX, hotbarSliderEndX);
            ClientConfigData.setNewHotbarScale(1 + 4*Mth.clamp((float) ((pMouseX - leftPos)/hotbarSliderRange), 0, 1));
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if(sliding){
            currentSliderX = Mth.clamp((int)pMouseX, offsetSliderStartX, offsetSliderEndX);
            ClientConfigData.setNewOffset(Mth.clamp((float) ((pMouseX - offsetSliderStartX)/sliderRange), 0, 1));
            return true;
        } else if(hotbarSliding){
            currentHotbarSliderX = Mth.clamp((int)pMouseX, hotbarSliderStartX, hotbarSliderEndX);
            ClientConfigData.setNewHotbarScale(1 + 4*Mth.clamp((float) ((pMouseX - hotbarSliderStartX)/hotbarSliderRange), 0, 1));
            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        //bg
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0,
                imageWidth, imageHeight,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);

        //title
        pGuiGraphics.drawString(this.font, Component.translatable("gui.potioneer.options_menu"),
                leftPos + imageWidth/2 - this.font.width(Component.translatable("gui.potioneer.options_menu"))/2,
                topPos + 10, 0, false);

        //offset slider
        pGuiGraphics.drawString(this.font, Component.translatable("gui.potioneer.options_slider_title"),
                leftPos + imageWidth/2 - this.font.width(Component.translatable("gui.potioneer.options_slider_title"))/2,
                topPos + 20, 0x555555, false);
        pGuiGraphics.blit(TEXTURE, leftPos + 8, topPos + 30, 0, 204,
                160, 13,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
        pGuiGraphics.blit(TEXTURE, currentSliderX, topPos + 30, 0, 217,
                8, 13);

        //orb margin buttons
        pGuiGraphics.drawString(this.font, Component.translatable("gui.potioneer.options_orb"),
                leftPos + 8,
                topPos + 50, 0x555555, false);
        saveButton.render(pGuiGraphics, pMouseX, pMouseY, 0);
        orbRightButton.render(pGuiGraphics, pMouseX, pMouseY, 0);
        orbLeftButton.render(pGuiGraphics, pMouseX, pMouseY, 0);
        orbSizeIncreaseButton.render(pGuiGraphics, pMouseX, pMouseY, 0);
        orbSizeDecreaseButton.render(pGuiGraphics, pMouseX, pMouseY, 0);

        //hotbar buttons
        pGuiGraphics.drawString(this.font, Component.translatable("gui.potioneer.options_hotbar"),
                leftPos + 8,
                topPos + 65, 0x555555, false);
        hotbarPositionLeft.render(pGuiGraphics, pMouseX, pMouseY, 0);
        hotbarPositionRight.render(pGuiGraphics, pMouseX, pMouseY, 0);
        hotbarPositionTop.render(pGuiGraphics, pMouseX, pMouseY, 0);

        //hotbar slider
        pGuiGraphics.drawString(this.font, Component.translatable("gui.potioneer.options_hotbar_slider"),
                leftPos + imageWidth/2 - this.font.width(Component.translatable("gui.potioneer.options_hotbar_slider"))/2,
                topPos + 90, 0x555555, false);
        pGuiGraphics.blit(TEXTURE, leftPos + 8, topPos + 100, 0, 204,
                160, 13,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
        pGuiGraphics.blit(TEXTURE, currentHotbarSliderX, topPos + 100, 0, 217,
                8, 13);

    }

    private void saveData(){
        ClientConfigData.saveData();
    }

    private void setOrbOnRight(boolean onRight){
        ClientConfigData.setOrbOnRight(onRight);
    }

    private void increaseOrbSize(boolean increase){
        int val = ClientConfigData.getCurrentOrbScale() + (increase ? 1 : -1);
        ClientConfigData.setNewOrbScale(Mth.clamp(val, 1, 12));
    }

    private void setHotbarPosition(PotioneerClientConfig.HOTBAR_POS pos){
        ClientConfigData.setHotbarPosition(pos);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        showHotbarOnConfigScreen(false);
        super.onClose();
    }
}
