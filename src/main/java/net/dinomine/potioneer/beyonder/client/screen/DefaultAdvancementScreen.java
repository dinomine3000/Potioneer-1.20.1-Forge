package net.dinomine.potioneer.beyonder.client.screen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.AdvancementButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class DefaultAdvancementScreen extends Screen implements MinigameScreen {
    private static final Component TITLE = Component.translatable("gui." + Potioneer.MOD_ID + ".beyonder_menu");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Potioneer.MOD_ID, "textures/gui/advancement_button.png");

    // Minigame Settings & State
    private int difficulty;
    private Consumer<Boolean> completionCallback;
    private int count;
    private int maxCount;
    private float maxTime;
    private float progress = 1.0f;
    private boolean gameStarted = false;
    private int targetX, targetY;

    private AdvancementButton button;
    private int leftPos, topPos;

    public DefaultAdvancementScreen() {
        super(TITLE);
    }

    @Override
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void setCompletionCallback(Consumer<Boolean> onComplete) {
        this.completionCallback = onComplete;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = this.width / 2;
        this.topPos = this.height / 2;

        // Game Setup Rules
        this.count = 4 * this.difficulty + 10;
        this.maxCount = this.count;
        this.maxTime = 1.5f - (this.difficulty * 0.1f);
        this.progress = 1.0f;
        this.gameStarted = false;

        this.targetX = this.leftPos - 15;
        this.targetY = this.topPos + 5;

        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Difficulty: " + this.difficulty + "/10."));
        }

        // Add Target Button
        this.button = addRenderableWidget(new AdvancementButton(
                this.targetX, this.targetY, 30, 30, 0, 0, TEXTURE,
                btn -> onButtonSucceed()
        ));
    }

    private void onButtonSucceed() {
        if (!gameStarted) {
            gameStarted = true;
        }

        this.count--;
        this.progress = 1.0f;

        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.playSound(ModSounds.ADVANCEMENT_CLICK.get(), 1.0f, 1.0f);
        }

        if (this.count < 1) {
            finishGame(true);
            return;
        }

        // Calculate next target position
        this.targetX = (int) (Math.random() * (this.width - 80) + 40);
        this.targetY = (int) (Math.random() * (this.height - 80) + 40);

        this.button.setX(this.targetX);
        this.button.setY(this.targetY);
    }

    private void finishGame(boolean success) {
        if (completionCallback != null) {
            completionCallback.accept(success);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);

        // Tick Timer Logic inside Render
        if (gameStarted) {
            this.progress -= (pPartialTick * 0.05f) / this.maxTime;
            if (this.progress <= 0) {
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("Failed advancement"));
                }
                finishGame(false);
                return;
            }
        }

        // Render Text & Progress Bar
        if (!gameStarted) {
            pGuiGraphics.drawString(this.font, Component.literal("Press the targets in time."), this.leftPos - 55, this.topPos - 75, 0xFFFFFFFF, false);
            pGuiGraphics.drawString(this.font, Component.literal("You will die if you miss one."), this.leftPos - 55, this.topPos - 45, 0xFFFFFFFF, false);
            pGuiGraphics.drawString(this.font, Component.literal("Click on this one to start."), this.leftPos - 55, this.topPos - 15, 0xFFFFFFFF, false);
        } else {
            // Background bar & Progress bar fill
            pGuiGraphics.blit(TEXTURE, this.leftPos - 65, this.topPos - 100, 30, 0, 132, 14);
            pGuiGraphics.blit(TEXTURE, this.leftPos - 65, this.topPos - 100, 30, 14, (int) (this.progress * 132), 14);

            pGuiGraphics.drawString(this.font,
                    Component.literal(String.format("%s/%s", this.count, this.maxCount)),
                    this.leftPos - 15, this.topPos - 60, 0xFF909090, false);
        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}