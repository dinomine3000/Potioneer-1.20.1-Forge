package net.dinomine.potioneer.beyonder.screen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BeyonderScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + Potioneer.MOD_ID + ".beyonder_menu");
    private static final Component ABILITIES_BUTTON = Component.translatable("gui." + Potioneer.MOD_ID + ".beyonder_menu.abilities_button");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "gui/beyonder_menu_screen.png");

    private Component PATHWAY;
    private Component SEQUENCE;
    private Component SEQUENCE_LEVEL;
    private int color;

    private final int imageWidth, imageHeight;
    private int leftPos, topPos;

    private Button button;

    public BeyonderScreen() {
        super(TITLE);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(pKeyCode == 69) {
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

        Minecraft.getInstance().player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            this.PATHWAY = Component.translatable(Potioneer.MOD_ID + ".beyonder.pathway." + cap.getPathwayName(false));
            this.SEQUENCE = Component.translatable(Potioneer.MOD_ID + ".beyonder.sequence." + cap.getSequenceName(false));
            this.color = cap.getPathwayColor();


            //this.PATHWAY = Component.literal("Path");
            //this.SEQUENCE = Component.literal("sequence");
            //this.color = 0x404080;
            //this.SEQUENCE_LEVEL = Component.literal(String.valueOf(cap.getSequence()));

        });
        /*this.button = addRenderableWidget(
                Button.builder(
                    ABILITIES_BUTTON,
                        btn ->
                )
        )*/
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.drawString(this.font, PATHWAY, this.leftPos + 8, this.topPos + 8, this.color, false);
        pGuiGraphics.drawString(this.font, SEQUENCE, this.leftPos + 8, this.topPos + 24, 0x404040, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
