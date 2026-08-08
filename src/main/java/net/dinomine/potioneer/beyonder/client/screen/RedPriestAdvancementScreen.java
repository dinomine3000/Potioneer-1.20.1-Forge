package net.dinomine.potioneer.beyonder.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;

public class RedPriestAdvancementScreen extends Screen implements MinigameScreen {

    private Vec2 currentPos = new Vec2(0, 0);
    private Vec2 targetPos = new Vec2(0, 0);

    private static final int DOT_SIZE = 6;
    private static final float LERP_SPEED = 12.0f; // Higher = faster tracking, lower = smoother lag

    private long renderTimestamp;
    private long lastThrowTime = System.currentTimeMillis();
    private final RandomSource random = RandomSource.create();

    public RedPriestAdvancementScreen() {
        super(Component.literal("Minigame"));
    }

    @Override
    protected void init() {
        super.init();
        Minecraft mc = Minecraft.getInstance();

        // Lock mouse for infinite dragging
        InputConstants.grabOrReleaseMouse(
                mc.getWindow().getWindow(),
                212995,
                mc.getWindow().getScreenWidth() / 2.0,
                mc.getWindow().getScreenHeight() / 2.0
        );

        // Center both current and target position
        Vec2 center = new Vec2((float) (this.width / 2.0), (float) (this.height / 2.0));
        this.currentPos = center;
        this.targetPos = center;
        this.lastThrowTime = System.currentTimeMillis();
        this.renderTimestamp = System.currentTimeMillis();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        long time = System.currentTimeMillis();
        float dt = Math.max(0.001f, (time - this.renderTimestamp) / 1000.0f);
        this.renderTimestamp = time;

        //apply constant force away from the center.
        Vec2 gravity = new Vec2(
                currentPos.x - this.width/2f,
                currentPos.y - this.height/2f
        );
        if(gravity.length() > 0.01){
            float scalar = 2/gravity.length();
            gravity = gravity.scale(scalar);
            this.targetPos = this.targetPos.add(new Vec2(gravity.x, gravity.y));
        }

        // Throw impulse every 5 seconds by displacing targetPos to a random edge/direction
        if (time - this.lastThrowTime >= 2000) {
            this.lastThrowTime = time;
            int edge = this.random.nextInt(4);
            float targetX;
            float targetY;

            switch (edge) {
                case 0 -> { // Top edge
                    targetX = this.random.nextInt(this.width);
                    targetY = 0;
                }
                case 1 -> { // Right edge
                    targetX = this.width;
                    targetY = this.random.nextInt(this.height);
                }
                case 2 -> { // Bottom edge
                    targetX = this.random.nextInt(this.width);
                    targetY = this.height;
                }
                default -> { // Left edge
                    targetX = 0;
                    targetY = this.random.nextInt(this.height);
                }
            }

            this.targetPos = new Vec2(targetX, targetY);

            this.targetPos = this.targetPos.add(new Vec2(targetX, targetY));
        }

        // Frame-rate independent linear interpolation towards targetPos
        float lerpFactor = 1.0f - (float) Math.exp(-LERP_SPEED * dt);
        float interpolatedX = Mth.clamp(Mth.lerp(lerpFactor, this.currentPos.x, this.targetPos.x), DOT_SIZE, this.width - DOT_SIZE);
        float interpolatedY = Mth.clamp(Mth.lerp(lerpFactor, this.currentPos.y, this.targetPos.y), DOT_SIZE, this.width - DOT_SIZE);

        this.currentPos = new Vec2(interpolatedX, interpolatedY);

        // Render dot at current interpolated position
        pGuiGraphics.fill(
                (int) (this.currentPos.x - DOT_SIZE),
                (int) (this.currentPos.y - DOT_SIZE),
                (int) (this.currentPos.x + DOT_SIZE),
                (int) (this.currentPos.y + DOT_SIZE),
                0xFFFFFFFF
        );
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        // Drag directly shifts targetPos, while currentPos smoothly follows
        this.targetPos = new Vec2(
                Mth.clamp(this.targetPos.x + (float) pDragX, DOT_SIZE, this.width - DOT_SIZE),
                Mth.clamp(this.targetPos.y + (float) pDragY, DOT_SIZE, this.height - DOT_SIZE)
        );
        return true;
    }

    @Override
    public void removed() {
        Minecraft mc = Minecraft.getInstance();
        InputConstants.grabOrReleaseMouse(
                mc.getWindow().getWindow(),
                212993,
                mc.getWindow().getScreenWidth() / 2.0,
                mc.getWindow().getScreenHeight() / 2.0
        );
        super.removed();
    }

    // Ignore Interface implementations
    @Override public void setCompletionCallback(java.util.function.Consumer<Boolean> onComplete) {}
    @Override public void setDifficulty(int difficulty) {}
}