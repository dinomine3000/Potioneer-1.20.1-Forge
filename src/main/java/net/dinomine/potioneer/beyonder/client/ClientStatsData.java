package net.dinomine.potioneer.beyonder.client;

public class ClientStatsData {
    private static int spirituality;
    private static int maxSpirituality;
    private static int sanity;
    private static int pathwayId;

    public static void setSpirituality(int spir){
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


    public static int getPlayerSpirituality(){
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

}
