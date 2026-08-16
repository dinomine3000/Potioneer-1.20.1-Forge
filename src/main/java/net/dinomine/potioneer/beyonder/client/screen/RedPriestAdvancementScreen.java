package net.dinomine.potioneer.beyonder.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;

import java.util.function.Consumer;

import static net.dinomine.potioneer.beyonder.client.screen.DefaultAdvancementScreen.TEXTURE;

public class RedPriestAdvancementScreen extends Screen implements MinigameScreen {

    private final RandomSource random = RandomSource.create();
    private Vec2 currentPos = new Vec2(0, 0);
    private Vec2 targetPos = new Vec2(0, 0);
    private float wanderAngle = (float) (this.random.nextFloat() * Math.PI * 2);

    private Consumer<Boolean> onComplete = null;
    private int difficulty = 1;

    private static final int DOT_SIZE = 6;
    private static final int SAFE_RADIUS = 25;
    private float lerpSpeed = 25.0f; // Higher = faster tracking, lower = smoother lag
    private float wanderAngleChange = 0.2f;
    private float wanderStrength = 0.4f;
    private float throwTime = 2f;
    private float impulseForce = 1.5f;
    private float gravityForce = 1f;

    private long renderTimestamp;
    private long lastThrowTime = System.currentTimeMillis();

    private boolean started = false;
    private float health = 100f;
    private float losePerSecond = 30f;
    private float gainPerSecond = 30f;
    private float maxTime = 10f;
    private float time = 10f;

    public RedPriestAdvancementScreen() {
        super(Component.literal("Minigame"));
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return true;
    }

    @Override
    protected void init() {
        super.init();
        Minecraft mc = Minecraft.getInstance();
        started = false;

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
        this.time = maxTime;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        started = true;
        this.lastThrowTime = System.currentTimeMillis();
        this.renderTimestamp = System.currentTimeMillis();
        return true;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);


        if(!started){
            pGuiGraphics.blit(TEXTURE, this.width/2 - SAFE_RADIUS, this.height/2 - SAFE_RADIUS, 2*SAFE_RADIUS, 2*SAFE_RADIUS, 0, 30, 50, 50, 256, 256);
            String tx = "Click to start minigame.";
            pGuiGraphics.drawString(this.font, tx, this.width/2 - this.font.width(tx)/2, this.height/2 - DOT_SIZE - 45, 0xffffff, false);
            tx = "Keep the square in the middle.";
            pGuiGraphics.drawString(this.font, tx, this.width/2 - this.font.width(tx)/2, this.height/2 - DOT_SIZE - 25, 0xffffff, false);
            tx = "Difficulty: " + this.difficulty + "/10";
            pGuiGraphics.drawString(this.font, tx, this.width/2 - this.font.width(tx)/2, this.height/2 - DOT_SIZE - 65, 0xffffff, false);
            pGuiGraphics.fill(
                    (int) (this.currentPos.x - DOT_SIZE),
                    (int) (this.currentPos.y - DOT_SIZE),
                    (int) (this.currentPos.x + DOT_SIZE),
                    (int) (this.currentPos.y + DOT_SIZE),
                    0xFFFFFFFF
            );
            return;
        }

        pGuiGraphics.blit(TEXTURE, this.width/2 - 66, this.height/2 - 50, 30, 0, 132, 14);
        pGuiGraphics.blit(TEXTURE, this.width/2 - 66, this.height/2 - 50, 30, 14, (int) (132*(time/maxTime)), 14);

        long currentTime = System.currentTimeMillis();
        float dt = Math.max(0.001f, (currentTime - this.renderTimestamp) / 1000.0f);
        this.renderTimestamp = currentTime;

        //apply constant force away from the center.
        Vec2 gravity = new Vec2(
                currentPos.x - this.width/2f,
                currentPos.y - this.height/2f
        );
        if(gravity.length() > 0.01){
            float scalar = 1f/gravity.length();
            gravity = gravity.scale(scalar).scale(gravityForce);
            this.targetPos = this.targetPos.add(new Vec2(gravity.x, gravity.y));
        }

        // Gradually drift the angle by a small random delta (-maxAngleChange to +maxAngleChange)
        this.wanderAngle += (this.random.nextFloat() * 2f - 1f) * wanderAngleChange;

        // Convert angle to a unit directional vector
        Vec2 smoothRandomVec = new Vec2(
                (float) Math.cos(this.wanderAngle) * wanderStrength,
                (float) Math.sin(this.wanderAngle) * wanderStrength
        );

        this.targetPos = this.targetPos.add(smoothRandomVec);

        // Throw impulse every 5 seconds by displacing targetPos to a random edge/direction
        if (currentTime - this.lastThrowTime >= throwTime*1000) {
            this.lastThrowTime = currentTime - random.nextInt((int) (throwTime*500));
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

            Vec2 diffVec = new Vec2(targetX - this.targetPos.x, targetY - this.targetPos.y).scale(impulseForce);
            this.targetPos = this.targetPos.add(diffVec);
        }

        // Frame-rate independent linear interpolation towards targetPos
        float lerpFactor = 1.0f - (float) Math.exp(-lerpSpeed * dt);
        float interpolatedX = Mth.clamp(Mth.lerp(lerpFactor, this.currentPos.x, this.targetPos.x), DOT_SIZE, this.width - DOT_SIZE);
        float interpolatedY = Mth.clamp(Mth.lerp(lerpFactor, this.currentPos.y, this.targetPos.y), DOT_SIZE, this.height - DOT_SIZE);

        this.currentPos = new Vec2(interpolatedX, interpolatedY);

        // Render dot at current interpolated position
        float healthPercent = Mth.clamp(health / 100f, 0.0f, 1.0f);
        int alpha = (int) (healthPercent * 255.0f);
        int color = (alpha << 24) | 0x00FFFFFF;


        if(isSafe()){
            pGuiGraphics.blit(TEXTURE, this.width/2 - SAFE_RADIUS, this.height/2 - SAFE_RADIUS, 2*SAFE_RADIUS, 2*SAFE_RADIUS, 50, 30, 50, 50, 256, 256);
            health += gainPerSecond*dt;
        } else {
            pGuiGraphics.blit(TEXTURE, this.width/2 - SAFE_RADIUS, this.height/2 - SAFE_RADIUS, 2*SAFE_RADIUS, 2*SAFE_RADIUS, 0, 30, 50, 50, 256, 256);
            health -= losePerSecond*dt;
        }

        pGuiGraphics.fill(
                (int) (this.currentPos.x - DOT_SIZE),
                (int) (this.currentPos.y - DOT_SIZE),
                (int) (this.currentPos.x + DOT_SIZE),
                (int) (this.currentPos.y + DOT_SIZE),
                color
        );

        if(this.health < 0) {
            endGame(false);
        }
        this.time -= dt;
        if(this.time <= 0) endGame(true);
    }

    private boolean isSafe(){
        Vec2 center = new Vec2(this.width/2f, this.height/2f);
        float distSqr = center.distanceToSqr(currentPos);
        return distSqr < SAFE_RADIUS*SAFE_RADIUS;
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

    private void endGame(boolean win){
        if(onComplete != null) onComplete.accept(win);
        //System.out.println("End game: " + win);
        onClose();
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


    @Override public void setCompletionCallback(Consumer<Boolean> onComplete) {this.onComplete = onComplete;}
    @Override public void setDifficulty(int difficulty) {
        this.losePerSecond = 30;
        this.gainPerSecond = 12 + 18*difficulty/10f;
        this.maxTime = 10 + difficulty*10/10f;
        this.time = this.maxTime;
        lerpSpeed = 20.0f - difficulty*10/10f; // Higher = faster tracking, lower = smoother lag
        wanderAngleChange = 0.2f;
        wanderStrength = 0.2f + difficulty*0.35f/10f;
        throwTime = 3f - difficulty*1.2f/10f;
        impulseForce = 0.5f + difficulty*1.5f/10f;
        gravityForce = 0.3f + difficulty*0.3f/10f;
        this.difficulty = difficulty;
    }
}