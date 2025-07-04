package net.dinomine.potioneer.menus;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CrafterAnvilScreen extends ItemCombinerScreen<CrafterAnvilMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/paragon_anvil_menu.png");
    private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");
    private EditBox name;
    private final Player player;

    public CrafterAnvilScreen(CrafterAnvilMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE);
        this.player = pPlayerInventory.player;
        this.titleLabelX = 60;
    }

    public void containerTick() {
        super.containerTick();
        this.name.tick();
    }

    protected void subInit() {
        int $$0 = (this.width - this.imageWidth) / 2;
        int $$1 = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, $$0 + 62, $$1 + 24, 103, 12, Component.translatable("container.repair"));
        this.name.setCanLoseFocus(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(50);
        this.name.setResponder(this::onNameChanged);
        this.name.setValue("");
        this.addWidget(this.name);
        this.setInitialFocus(this.name);
        this.name.setEditable(false);
    }

    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        String $$3 = this.name.getValue();
        this.init(pMinecraft, pWidth, pHeight);
        this.name.setValue($$3);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) {
            this.minecraft.player.closeContainer();
        }

        return this.name.keyPressed(pKeyCode, pScanCode, pModifiers) || this.name.canConsumeInput() || super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void onNameChanged(String p_97899_) {
        Slot $$1 = this.menu.getSlot(0);
        if ($$1.hasItem()) {
            String $$2 = p_97899_;
            if (!$$1.getItem().hasCustomHoverName() && p_97899_.equals($$1.getItem().getHoverName().getString())) {
                $$2 = "";
            }

            if (this.menu.setItemName($$2)) {
                this.minecraft.player.connection.send(new ServerboundRenameItemPacket($$2));
            }

        }
    }

    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(pGuiGraphics, pMouseX, pMouseY);
        int cost = this.menu.getCost();
        if (cost > 0 && !this.menu.hasFuel()) {
            int $$4 = 8453920;
            Component labelComponent;
            if (!this.menu.getSlot(2).hasItem()) {
                System.out.println("No result");
                labelComponent = null;
            } else {
                labelComponent = Component.translatable("container.repair.cost", cost);
                if (!this.menu.getSlot(2).mayPickup(this.player)) {
                    $$4 = 16736352;
                }
            }

            if (labelComponent != null) {
                int $$8 = this.imageWidth - 8 - this.font.width(labelComponent) - 2;
                int $$9 = 69;
                pGuiGraphics.blit(TEXTURE, 79,  64, 97, 64, 18, 18);
                pGuiGraphics.fill($$8 - 2, 67, this.imageWidth - 8, 79, 1325400064);
                pGuiGraphics.drawString(this.font, labelComponent, $$8, 69, $$4);
            }
        }

    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        pGuiGraphics.blit(TEXTURE, this.leftPos + 59, this.topPos + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
    }

    public void renderFg(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.name.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderErrorIcon(GuiGraphics pGuiGraphics, int pX, int pY) {
        if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(this.menu.getResultSlot()).hasItem()) {
            pGuiGraphics.blit(TEXTURE, pX + 99, pY + 45, this.imageWidth, 0, 28, 21);
        }

    }

    public void slotChanged(AbstractContainerMenu pContainerToSend, int pSlotInd, ItemStack pStack) {
        if (pSlotInd == 0 && !pStack.is(Items.BOOK)) {
            this.name.setValue(pStack.isEmpty() ? "" : pStack.getHoverName().getString());
            this.name.setEditable(!pStack.isEmpty());
            this.setFocused(this.name);
        }

    }
}
