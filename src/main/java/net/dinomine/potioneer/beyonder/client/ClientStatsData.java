package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.client.screen.AdvancementScreen;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

import static net.dinomine.potioneer.util.misc.AdvancementDifficultyHelper.calculateDifficultyClient;

@OnlyIn(Dist.CLIENT)
public class ClientStatsData {
    public static boolean keyPressed = false;
//    private static int[] beyonderStats = new int[]{0, 0, 0, 0, 0};
    private static float actingProgress = 0;

    private static int luck = 0;
    private static int minLuck = 0;
    private static int maxLuck = 0;

    public static void attemptAdvancement(int newSeq, int addedDifficulty){
        Optional<LivingEntityBeyonderCapability> capOpt = getCapability();
        if(capOpt.isEmpty()) return;
        LivingEntityBeyonderCapability cap = capOpt.get();
        int pathwayId = cap.getPathwaySequenceId();
        int sanity = (int) cap.getSanity();
        ClientAdvancementManager.setDifficulty(addedDifficulty + calculateDifficultyClient(pathwayId, newSeq, sanity, actingProgress));
//        ClientAdvancementManager.difficulty = 10;     //Debug
        ClientAdvancementManager.targetSequence = Math.min(newSeq, pathwayId);
        if(pathwayId == -1) ClientAdvancementManager.targetSequence = newSeq;
        Minecraft.getInstance().setScreen(new AdvancementScreen());
    }

    public static Optional<LivingEntityBeyonderCapability> getCapability(){
        if(Minecraft.getInstance().player == null) return Optional.empty();
        if(!Minecraft.getInstance().player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent()) return Optional.empty();
        return Minecraft.getInstance().player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
    }

    public static float getPlayerSpirituality(){
        if(getCapability().isPresent()){
            return getCapability().get().getSpirituality();
        }
        System.out.println("Tried to get spirituality but none was there to be read.");
        return 0;
    }

    public static int getPlayerMaxSpirituality(){
        if(getCapability().isPresent()){
            return getCapability().get().getMaxSpirituality();
        }
        System.out.println("Tried to get spirituality but none was there to be read.");
        return 0;
    }

    public static float getPlayerSanity(){
        if(getCapability().isPresent()){
            return getCapability().get().getSanity();
        }
        System.out.println("Tried to get spirituality but none was there to be read.");
        return 100f;
    }

    public static int getPathwaySequenceId(){
        Player player = Minecraft.getInstance().player;
        if(player == null){
            System.out.println("Warning: could not get player to read pathway id");
            return -1;
        }

        if(player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent()){
            if(player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().isEmpty()) return -1;
            LivingEntityBeyonderCapability cap = player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
            return cap.getPathwaySequenceId();
        }
        return -1;
    }

    public static int getStat(int idx){
        if(getCapability().isPresent()){
            return getCapability().get().getBeyonderStats().getIntStats()[idx];
        }
        System.out.println("Tried to get spirituality but none was there to be read.");
        return 0;
    }

    public static void setLuck(int newLuck, int newMinLuck, int newMaxLuck) {
        luck = newLuck;
        minLuck = newMinLuck;
        maxLuck = newMaxLuck;
    }

    public static int getLuck(){
        return luck;
    }

    public static int getMinLuck(){
        return minLuck;
    }

    public static int getMaxLuck(){
        return maxLuck;
    }

    public static void setActing(float acting) {
        if(!PotioneerClientConfig.POTION_DIGESTED_MESSAGE.get() && actingProgress == 0 && acting >= 0.95){
            actingProgress = acting;
            return;
        }
        if(actingProgress < 0.25 && acting >= 0.25){
            Minecraft.getInstance().player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        if(actingProgress < 0.5 && acting >= 0.5){
            Minecraft.getInstance().player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        if(actingProgress < 0.75 && acting >= 0.75){
            Minecraft.getInstance().player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        if(acting >= 0.95 && actingProgress < 0.95){
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable("potioneer.message.acting_complete"));
        }
        actingProgress = acting;
    }

    public static float getActing(){
        return actingProgress;
    }
}
