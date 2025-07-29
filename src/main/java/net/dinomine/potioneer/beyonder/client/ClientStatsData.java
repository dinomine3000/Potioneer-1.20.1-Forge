package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.client.screen.AdvancementScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static net.dinomine.potioneer.util.misc.AdvancementDifficultyHelper.calculateDifficultyClient;

@OnlyIn(Dist.CLIENT)
public class ClientStatsData {
    private static float spirituality = 100f;
    private static int maxSpirituality = 100;
    private static int sanity = 100;
    private static int pathwayId;
    public static boolean keyPressed = false;
    private static int[] beyonderStats = new int[]{0, 0, 0, 0, 0};
    private static float actingProgress = 0;

    private static int luck = 0;
    private static int minLuck = 0;
    private static int maxLuck = 0;

    public static void attemptAdvancement(int newSeq, int addedDifficulty){

        ClientAdvancementManager.setDifficulty(addedDifficulty + calculateDifficultyClient(pathwayId, newSeq, sanity, actingProgress));
//        ClientAdvancementManager.difficulty = 10;     //Debug
        ClientAdvancementManager.targetSequence = Math.min(newSeq, pathwayId);
        if(pathwayId == -1) ClientAdvancementManager.targetSequence = newSeq;
        Minecraft.getInstance().setScreen(new AdvancementScreen());
    }

    public static void setSpirituality(float spir){
        ClientStatsData.spirituality = spir;
    }

    public static void setMaxSpirituality(int spir){
        ClientStatsData.maxSpirituality = spir;
    }

    public static void setSanity(int sanity){
        ClientStatsData.sanity = sanity;
    }

    public static void setPathwayId(int id){
        ClientStatsData.pathwayId = id;
    }

    public static float getPlayerSpirituality(){
        return spirituality;
    }

    public static int getPlayerMaxSpirituality(){
        return maxSpirituality;
    }

    public static int getPlayerSanity(){
        return sanity;
    }

    public static int getPathwayId(){
        return pathwayId;
    }

    public static int getStat(int idx){
        return beyonderStats[idx];
    }

    public static void setStats(int[] stats) {
        beyonderStats = stats;
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
        if(actingProgress == 0 && acting >= 0.95){
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
