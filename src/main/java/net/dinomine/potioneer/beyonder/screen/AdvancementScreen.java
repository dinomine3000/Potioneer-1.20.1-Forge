package net.dinomine.potioneer.beyonder.screen;

import com.eliotlash.mclib.math.functions.limit.Min;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientAdvancementManager;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.AdvancementButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.awt.event.MouseMotionAdapter;

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
        pGuiGraphics.blit(TEXTURE, this.leftPos - 65, this.topPos - 100, 30, 0, 132 ,14);
        pGuiGraphics.blit(TEXTURE, this.leftPos - 65, this.topPos - 100, 30, 14, (int)(ClientAdvancementManager.progress * 132) ,14);

        pGuiGraphics.drawString(this.font, Component.literal(String.format("%s/%s", ClientAdvancementManager.count, ClientAdvancementManager.maxCount)), this.leftPos, this.topPos - 120, 0x404040, false);

        this.button.setX(ClientAdvancementManager.x);
        this.button.setY(ClientAdvancementManager.y);
        ClientAdvancementManager.render(this, pPartialTick);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
