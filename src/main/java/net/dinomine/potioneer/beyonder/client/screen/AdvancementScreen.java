package net.dinomine.potioneer.beyonder.client.screen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientAdvancementManager;
import net.dinomine.potioneer.util.AdvancementButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AdvancementScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + Potioneer.MOD_ID + ".beyonder_menu");
    private static final Component ABILITIES_BUTTON = Component.translatable("gui." + Potioneer.MOD_ID + ".beyonder_menu.abilities_button");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/advancement_button.png");

    private static final Component BUTTON = Component.empty();

    private MouseHandler mouseHandler;
    private int leftPos, topPos;

    private AdvancementButton button;

    public AdvancementScreen() {
        super(TITLE);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(pKeyCode == 69) {
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width) / 2;
        this.topPos = (this.height) / 2;

        ClientAdvancementManager.startGame(this);
        mouseHandler = new MouseHandler(Minecraft.getInstance());

        this.button = addRenderableWidget(new AdvancementButton(this.leftPos, this.topPos, 30, 30, 0, 0, TEXTURE,
                btn -> ClientAdvancementManager.onButtonSucceed(this)));

//        this.button = addRenderableWidget(
//                ImageButton.builder(
//                        BUTTON,
//                        btn -> ClientAdvancementManager.onButtonSucceed(this)
//                ).bounds(this.leftPos, this.topPos, 20, 20).build()
//        );
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        ClientAdvancementManager.render(this, pPartialTick);
        if(ClientAdvancementManager.start){
            pGuiGraphics.drawString(this.font, Component.literal("Press the targets in time."), this.leftPos - 55, this.topPos - 75, 0xFFFFFFFF, false);
            pGuiGraphics.drawString(this.font, Component.literal("You will die if you miss one."), this.leftPos - 55, this.topPos - 45, 0xFFFFFFFF, false);
            pGuiGraphics.drawString(this.font, Component.literal("Click on this one to start."), this.leftPos - 55, this.topPos - 15, 0xFFFFFFFF, false);
        } else {
            pGuiGraphics.blit(TEXTURE, this.leftPos - 65, this.topPos - 100, 30, 0, 132 ,14);
            pGuiGraphics.blit(TEXTURE, this.leftPos - 65, this.topPos - 100, 30, 14, (int)(ClientAdvancementManager.progress * 132) ,14);

            pGuiGraphics.drawString(this.font,
                    Component.literal(String.format("%s/%s", ClientAdvancementManager.count, ClientAdvancementManager.maxCount)),
                    this.leftPos - 15, this.topPos - 60, 0xFF909090, false);


        }
        this.button.setX(ClientAdvancementManager.x);
        this.button.setY(ClientAdvancementManager.y);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
