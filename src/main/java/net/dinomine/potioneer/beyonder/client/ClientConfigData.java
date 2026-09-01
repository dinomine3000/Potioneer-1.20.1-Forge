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

    // --- Layout Config Data ---
    private static float leftX = 0.02f;
    private static float leftY = 0.1f;
    private static float leftWidth = 0.30f;
    private static boolean renderLeft = true;

    private static float rightX = 0.68f;
    private static float rightY = 0.1f;
    private static float rightWidth = 0.3f;
    private static boolean renderRight = true;

    private static float centerX = 0.35f;
    private static float centerY = 0.2f;
    private static float centerWidth = 0.3f;
    private static boolean renderCenter = true;

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

            // Load Layout Configs
            leftX = PotioneerClientConfig.LEFT_X.get().floatValue();
            leftY = PotioneerClientConfig.LEFT_Y.get().floatValue();
            leftWidth = PotioneerClientConfig.LEFT_WIDTH.get().floatValue();
            renderLeft = PotioneerClientConfig.RENDER_LEFT.get();

            rightX = PotioneerClientConfig.RIGHT_X.get().floatValue();
            rightY = PotioneerClientConfig.RIGHT_Y.get().floatValue();
            rightWidth = PotioneerClientConfig.RIGHT_WIDTH.get().floatValue();
            renderRight = PotioneerClientConfig.RENDER_RIGHT.get();

            centerX = PotioneerClientConfig.CENTER_X.get().floatValue();
            centerY = PotioneerClientConfig.CENTER_Y.get().floatValue();
            centerWidth = PotioneerClientConfig.CENTER_WIDTH.get().floatValue();
            renderCenter = PotioneerClientConfig.RENDER_CENTER.get();
        }
    }

    public static void setNewOffset(float offset){ orb_offset = offset; }
    public static double getCurrentOffset(){ return orb_offset; }

    public static void setNewOrbScale(int scale){ orbScale = scale; }
    public static int getCurrentOrbScale(){ return orbScale; }

    public static void setOrbOnRight(boolean onRight){ orbOnRight = onRight; }
    public static boolean isOrbOnRight(){ return orbOnRight; }

    public static void setHotbarPosition(PotioneerClientConfig.HOTBAR_POS newPos){ hotbarPosition = newPos; }
    public static PotioneerClientConfig.HOTBAR_POS getHotbarPosition(){ return hotbarPosition; }

    public static void setNewHotbarScale(float scale){ hotbarScale = scale; }
    public static double getCurrentHotbarScale(){ return hotbarScale; }

    public static void setHotbarOutline(boolean state) { hotbarOutlines = state; }
    public static boolean getHotbarOutlines(){ return hotbarOutlines; }

    public static void setAlternativeBlocking(boolean state) { alternativeBlocking = state; }
    public static boolean getAlternativeBlocking(){ return alternativeBlocking; }

    // --- Getters & Setters for Layout ---
    public static float getLeftX() { return leftX; }
    public static void setLeftX(float value) { leftX = value; }

    public static float getLeftY() { return leftY; }
    public static void setLeftY(float value) { leftY = value; }

    public static float getLeftWidth() { return leftWidth; }
    public static void setLeftWidth(float value) { leftWidth = value; }

    public static boolean isRenderLeft() { return renderLeft; }
    public static void setRenderLeft(boolean state) { renderLeft = state; }

    public static float getRightX() { return rightX; }
    public static void setRightX(float value) { rightX = value; }

    public static float getRightY() { return rightY; }
    public static void setRightY(float value) { rightY = value; }

    public static float getRightWidth() { return rightWidth; }
    public static void setRightWidth(float value) { rightWidth = value; }

    public static boolean isRenderRight() { return renderRight; }
    public static void setRenderRight(boolean state) { renderRight = state; }

    public static float getCenterX() { return centerX; }
    public static void setCenterX(float value) { centerX = value; }

    public static float getCenterY() { return centerY; }
    public static void setCenterY(float value) { centerY = value; }

    public static float getCenterWidth() { return centerWidth; }
    public static void setCenterWidth(float value) { centerWidth = value; }

    public static boolean isRenderCenter() { return renderCenter; }
    public static void setRenderCenter(boolean state) { renderCenter = state; }

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
        PotioneerClientConfig.ALTERNATIVE_DISABLED_SYMBOL.set(alternativeBlocking);
        PotioneerClientConfig.ALTERNATIVE_DISABLED_SYMBOL.save();

        // Save Layout Configs
        PotioneerClientConfig.LEFT_X.set((double) leftX);
        PotioneerClientConfig.LEFT_X.save();
        PotioneerClientConfig.LEFT_Y.set((double) leftY);
        PotioneerClientConfig.LEFT_Y.save();
        PotioneerClientConfig.LEFT_WIDTH.set((double) leftWidth);
        PotioneerClientConfig.LEFT_WIDTH.save();
        PotioneerClientConfig.RENDER_LEFT.set(renderLeft);
        PotioneerClientConfig.RENDER_LEFT.save();

        PotioneerClientConfig.RIGHT_X.set((double) rightX);
        PotioneerClientConfig.RIGHT_X.save();
        PotioneerClientConfig.RIGHT_Y.set((double) rightY);
        PotioneerClientConfig.RIGHT_Y.save();
        PotioneerClientConfig.RIGHT_WIDTH.set((double) rightWidth);
        PotioneerClientConfig.RIGHT_WIDTH.save();
        PotioneerClientConfig.RENDER_RIGHT.set(renderRight);
        PotioneerClientConfig.RENDER_RIGHT.save();

        PotioneerClientConfig.CENTER_X.set((double) centerX);
        PotioneerClientConfig.CENTER_X.save();
        PotioneerClientConfig.CENTER_Y.set((double) centerY);
        PotioneerClientConfig.CENTER_Y.save();
        PotioneerClientConfig.CENTER_WIDTH.set((double) centerWidth);
        PotioneerClientConfig.CENTER_WIDTH.save();
        PotioneerClientConfig.RENDER_CENTER.set(renderCenter);
        PotioneerClientConfig.RENDER_CENTER.save();
    }
}