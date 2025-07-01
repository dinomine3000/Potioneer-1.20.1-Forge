package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.screen.AdvancementScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientStatsData {
    private static float spirituality = 100f;
    private static int maxSpirituality = 100;
    private static int sanity = 100;
    private static int pathwayId;
    public static boolean keyPressed = false;
    private static int[] beyonderStats = new int[]{0, 0, 0, 0, 0};

    private static int luck = 0;
    private static int minLuck = 0;
    private static int maxLuck = 0;

    public static void attemptAdvancement(int newSeq){
        //difference between the new sequence and current sequence
        //plus one more difficulty for every 25% sanity lost
        //plus 1 for each group of 9-7, 6-4 and 3-1 sequence levels
        //plus 1 or 2 for undigested potions
        ClientAdvancementManager.setDifficulty((Math.max(pathwayId%10 - newSeq%10, 1) //adds the difference in levels. from 1 to 10
                + Math.round(4f-sanity/25f) //from 0 to 4 more points
                + 3-Math.floorDiv(newSeq%10, 3))); //adds from 0 to 3 points of difficulty
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
}
