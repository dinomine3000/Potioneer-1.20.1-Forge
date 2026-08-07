package net.dinomine.potioneer.beyonder.client.screen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public interface MinigameScreen {
    /**
     * Registers a callback that executes when the minigame completes.
     * @param onComplete Consumer accepting true for success, false for failure.
     */
    void setCompletionCallback(Consumer<Boolean> onComplete);

    void setDifficulty(int difficulty);
}