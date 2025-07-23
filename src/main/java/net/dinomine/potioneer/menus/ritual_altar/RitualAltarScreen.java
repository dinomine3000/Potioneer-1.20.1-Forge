package net.dinomine.potioneer.menus.ritual_altar;

import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.KeyBindings;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.AdvancementFailMessageCTS;
import net.dinomine.potioneer.network.messages.RitualC2STextSync;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RitualAltarScreen extends AbstractContainerScreen<RitualAltarMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ritual_altar_menu.png");

    Button firstVerse;
    Button secondVerse;
    EditBox thirdVerse;

    public RitualAltarScreen(RitualAltarMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(pKeyCode == 256) {
            this.onClose();
            return true;
        }
        if (thirdVerse.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            menu.writeVerse(thirdVerse.getValue());
            return true;
        }
        if(thirdVerse.isFocused()){
            return true;
        }
        if(pKeyCode == 69){
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    protected void init() {
        super.init();
        imageWidth = 176;
        imageHeight = 166;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height- this.imageHeight) / 2;

        firstVerse = new ImageButton(leftPos + 60, topPos + 25, 60, 14, 0, 166, 14, TEXTURE, btn -> onPressButton(1));
        secondVerse = new ImageButton(leftPos + 60, topPos + 39, 60, 14, 0, 166, 14, TEXTURE, btn -> onPressButton(2));
        thirdVerse = new EditBox(this.font, leftPos + 40, topPos + 53, 100, 14, Component.literal("third verse"));
        thirdVerse.setValue(menu.getVerse(3));
        thirdVerse.setMaxLength(120);
        addRenderableWidget(firstVerse);
        addRenderableWidget(secondVerse);
        addRenderableWidget(thirdVerse);
    }

    private void onPressButton(int verseIdx){
        menu.increaseVerse(verseIdx);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (thirdVerse.mouseClicked(mouseX, mouseY, button)) {
            thirdVerse.setFocused(true);
            return true;
        }
        thirdVerse.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (thirdVerse.charTyped(pCodePoint, pModifiers)) {
            menu.writeVerse(thirdVerse.getValue());
            return true;
        }
        return super.charTyped(pCodePoint, pModifiers);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, 176, 166);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        Component prayers = Component.translatable("rituals.potioneer.prayers");
        pGuiGraphics.drawString(this.font, prayers, leftPos + 90 - this.font.width(prayers.getString())/2, topPos + 15, 0, false);
        pGuiGraphics.drawString(this.font, getComponentInIntergalactic(menu.getVerse(1)), leftPos + 65, topPos + 28, 0xFFFFFF, false);
        pGuiGraphics.drawString(this.font, getComponentInIntergalactic(menu.getVerse(2)), leftPos + 65, topPos + 42, 0xFFFFFF, false);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        PacketHandler.INSTANCE.sendToServer(new RitualC2STextSync(menu.blockEntity.getBlockPos(),
                menu.getVerse(1), menu.getVerse(2), menu.getVerse(3)));
        super.onClose();
    }
    private Component getComponentInIntergalactic(String string){
        return Component.literal(string).withStyle(style -> style.withFont(new ResourceLocation("minecraft", "alt")));
    }
}
