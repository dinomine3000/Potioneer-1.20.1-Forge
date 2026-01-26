package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.config.PotioneerClientConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientConfigData {
    private static boolean flag = false;

    private static double orb_offset = 0.2d;
    private static boolean orbOnRight = true;
    private static int orbScale = 1;
    private static double hotbarScale = 1f;
    private static boolean hotbarOutlines = true;
    private static boolean alternativeBlocking = true;
    private static PotioneerClientConfig.HOTBAR_POS hotbarPosition = PotioneerClientConfig.HOTBAR_POS.TOP;

    public static void updateData(){
        if(!flag){
            flag = true;
            orbOnRight = PotioneerClientConfig.ORB_ON_RIGHT.get();
            orb_offset = PotioneerClientConfig.ORB_OFFSET.get();
            orbScale = PotioneerClientConfig.ORB_SCALE.get();
            hotbarScale = PotioneerClientConfig.HOTBAR_SCALE.get();
            hotbarPosition = PotioneerClientConfig.HOTBAR_POSITION.get();
            hotbarOutlines = PotioneerClientConfig.HOTBAR_ABILITY_CASES_WITH_OUTLINE.get();
            alternativeBlocking = PotioneerClientConfig.ALTERNATIVE_DISABLED_SYMBOL.get();
        }
    }

    public static void setNewOffset(float offset){
        orb_offset = offset;
    }

    public static double getCurrentOffset(){
        return orb_offset;
    }

    public static void setNewOrbScale(int scale){
        orbScale = scale;
    }

    public static int getCurrentOrbScale(){
        return orbScale;
    }

    public static void setOrbOnRight(boolean onRight){
        orbOnRight = onRight;
    }

    public static boolean isOrbOnRight(){
        return orbOnRight;
    }

    public static void setHotbarPosition(PotioneerClientConfig.HOTBAR_POS newPos){
        hotbarPosition = newPos;
    }

    public static PotioneerClientConfig.HOTBAR_POS getHotbarPosition(){
        return hotbarPosition;
    }

    public static void setNewHotbarScale(float scale){
        hotbarScale = scale;
    }

    public static double getCurrentHotbarScale(){
        return hotbarScale;
    }

    public static void setHotbarOutline(boolean state) {
        hotbarOutlines = state;
    }

    public static boolean getHotbarOutlines(){
        return hotbarOutlines;
    }

    public static void setAlternativeBlocking(boolean state) {
        alternativeBlocking = state;
    }

    public static boolean getAlternativeBlocking(){
        return alternativeBlocking;
    }

    public static void saveData(){
        System.out.println("Saving config file");
        PotioneerClientConfig.ORB_OFFSET.set(orb_offset);
        PotioneerClientConfig.ORB_OFFSET.save();
        PotioneerClientConfig.ORB_ON_RIGHT.set(orbOnRight);
        PotioneerClientConfig.ORB_ON_RIGHT.save();
        PotioneerClientConfig.ORB_SCALE.set(orbScale);
        PotioneerClientConfig.ORB_SCALE.save();
        PotioneerClientConfig.HOTBAR_SCALE.set(hotbarScale);
        PotioneerClientConfig.HOTBAR_SCALE.save();
        PotioneerClientConfig.HOTBAR_POSITION.set(hotbarPosition);
        PotioneerClientConfig.HOTBAR_POSITION.save();
        PotioneerClientConfig.HOTBAR_ABILITY_CASES_WITH_OUTLINE.set(hotbarOutlines);
        PotioneerClientConfig.HOTBAR_ABILITY_CASES_WITH_OUTLINE.save();
        PotioneerClientConfig.ALTERNATIVE_DISABLED_SYMBOL.save();
    }

}
