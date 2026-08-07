package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.client.screen.DefaultAdvancementScreen;
import net.dinomine.potioneer.beyonder.client.screen.MinigameScreen;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.advancement.AdvancementFailMessageCTS;
import net.dinomine.potioneer.network.messages.advancement.PlayerAdvanceMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientAdvancementManager {
    public static int targetSequence = 9;

    /**
     * Starts a minigame screen and hooks into its result.
     */
    public static void startGame(Screen screen, int difficulty) {
        if (screen instanceof MinigameScreen minigame) {
            minigame.setCompletionCallback(success -> onGameFinished(screen, success));
            minigame.setDifficulty(Mth.clamp(difficulty, 0, 10));
            Minecraft.getInstance().setScreen(screen);
        }
    }

    /**
     * Convenience method to directly launch the standard AdvancementScreen.
     */
    public static void startAdvancementMinigame(int sequence, int diff) {
        targetSequence = sequence;
        startGame(new DefaultAdvancementScreen(), diff);
    }

    private static void onGameFinished(Screen screen, boolean success) {
        screen.onClose();

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (success) {
            PacketHandler.sendMessageCTS(new PlayerAdvanceMessage(List.of(targetSequence)));
        } else {
            PacketHandler.INSTANCE.sendToServer(new AdvancementFailMessageCTS(targetSequence));
            player.sendSystemMessage(Component.literal("Lost control on the spot. oh well."));
        }
    }
}