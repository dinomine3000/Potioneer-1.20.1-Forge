package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ClientAdvancementManager {
    public static int count = 30;
    public static int maxCount = 30;
    public static int targetSequence = 9;
    static float maxTime = 1;
    public static float progress = 1;
    public static int x;
    public static int y;
    public static int difficulty;

    public static void setDifficulty(int diff){
        difficulty = Mth.clamp(diff, 0, 10);
    }

    public static void render(Screen screen, float partialTick){
        progress -= partialTick*0.05f/maxTime;
        if(progress < 0){
            gameOver(screen, false);
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Failed advancement"));
        }
    }

    public static void startGame(Screen screen){
        count = 4*difficulty+10;
        maxCount = count;
        maxTime = 1.5f-difficulty*0.1f;
        progress = 1;
        x = (int) (Math.random() * (screen.width - 80) + 40);
        y = (int) (Math.random() * (screen.height - 80) + 40);
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Difficulty: " + String.valueOf(difficulty)));
    }

    public static void onButtonSucceed(Screen screen) {
        count--;
        progress = 1;
        if(Minecraft.getInstance().player != null){
            Minecraft.getInstance().player.playSound(ModSounds.ADVANCEMENT_CLICK.get(), 1, 1);
        }
        x = (int) (Math.random() * (screen.width - 80) + 40);
        y = (int) (Math.random() * (screen.height - 80) + 40);
        if(count < 1) gameOver(screen, true);
    }

    public static void gameOver(Screen screen, boolean success){
        screen.onClose();
        if(success){
            Minecraft.getInstance().player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.advance(targetSequence, Minecraft.getInstance().player, true, true);
            });
        }
    }
}
