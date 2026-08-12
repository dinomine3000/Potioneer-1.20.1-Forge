package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.client.screen.MinigameScreen;
import net.dinomine.potioneer.beyonder.client.screen.RedPriestAdvancementScreen;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
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
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class ClientAdvancementManager {
    public static int targetSequence = 9;

    public static void attemptAdvancement(int newSeq, int addedDifficulty){
        Optional<BeyonderCapability> capOpt = ClientStatsData.getCapability();
        if(capOpt.isEmpty()) return;
        BeyonderCapability cap = capOpt.get();
        int pathwaySequenceId = cap.getPathwaySequenceId();
        int sanity = (int) cap.getSanity();
//        ClientAdvancementManager.difficulty = 10;     //Debug
        ClientAdvancementManager.startGame(new RedPriestAdvancementScreen(), newSeq, addedDifficulty + calculateDifficultyClient(pathwaySequenceId, newSeq, sanity, ClientStatsData.getActing()));
    }


    /**
     * Starts a minigame screen and hooks into its result.
     */
    public static void startGame(Screen screen, int sequence, int difficulty) {
        if (screen instanceof MinigameScreen minigame) {
            targetSequence = sequence;
            minigame.setCompletionCallback(success -> onGameFinished(screen, success));
            minigame.setDifficulty(Mth.clamp(difficulty, 0, 10));
            Minecraft.getInstance().setScreen(screen);
        }
    }

    public static int calculateDifficultyClient(int pathwaySequenceId, int newPathSeqId, int sanity, float actingProgress){
        //difference between the new sequence and current sequence
        //plus one more difficulty for every 25% sanity lost
        //plus 1 for each group of 8-6, 5-3 and 2-1 sequence levels
        //plus 2 for undigested potions
        int levelDifference;
        if(pathwaySequenceId < 0){ //adds 2 points of difficulty for every level you skip
            levelDifference = 4*Math.max(9 - newPathSeqId%10, 0);
        } else {
            levelDifference = 4*Math.max(pathwaySequenceId%10 - 1 - newPathSeqId%10, 0);
        }
        int sanityDiff = Math.round(8f-sanity/12.5f); //from 0 to 8 more points depending on your sanity
        int groupDiff = 3-Math.floorDiv(newPathSeqId%10, 3) + (newPathSeqId%10 == 0 ? 2 : 0); //plus 1 for each group
        int actingDiff = pathwaySequenceId < 0 ? 0 : (2*(1- Mth.floor(actingProgress + 0.05))); //adds 2 points of difficulty if acting progress isnt at 100%
        //adding 0.05 bc i consider 95% digestion to be complete - this makes the bar sort of "jump" when you "fully digest it",
        //which could make it more satisfying. the added 5% only happens on client side, the server would still tick upwards, and truth be told
        //the client maintains that 95% true amount, but for all intents and purposes 95% is the same as 100%, though its a manual check
        //mind you, advancing without fully digesting a potion will lead to less maximum sanity.

        int diff = levelDifference + sanityDiff + groupDiff + actingDiff;
        if(pathwaySequenceId > -1){
            int level = newPathSeqId%10;
            //if the target sequence is located between your current sequence and sequence 9,
            //aka, a lower sequence to your current one
            //add 2 points of difficulty
            //this is to prevent ppl from drinking previous potions without consequence
            if(level >= pathwaySequenceId%10) diff += 2;
        }
        // more points for demigod levels. 1 extra for 4 and 3, and 2 extra for above 2
        if(newPathSeqId%10 < 5) diff += newPathSeqId%10 > 2 ? 1 : 2;
        return diff;
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