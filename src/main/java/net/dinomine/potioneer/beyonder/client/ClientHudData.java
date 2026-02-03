package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.AppraisalDataMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientHudData {
    public static float tempMin = 0, tempPos = 0, tempMax = 0;
    public static float decayMin = 0, decayPos = 0, decayMax = 0;
    public static float baseMin = 0, basePos = 0, baseMax = 0;
    private static float lucks = 0;
    private static float health = 0, maxHealth = 1;
    private static float sanity = 0, maxSanity = 1;
    private static float spirituality = 0, maxSpirituality = 1;
    private static boolean showLuck = true;

    private static final long defaultDuration = 5000L;
    private static long startShowTimestamp = 0L, lastUpdateMessage = 0L;

    public static int entityId = 0;

    public static void sendUpdateRequest(){
        if(!shouldDisplayLuckHud()) return;
        if(System.currentTimeMillis() - lastUpdateMessage < 1000) return;
        PacketHandler.sendMessageCTS(new AppraisalDataMessage(entityId, new float[0], showLuck));
        lastUpdateMessage = System.currentTimeMillis();
    }

    public static boolean showLuckNotStats(){
        return showLuck;
    }

    public static boolean shouldDisplayLuckHud(){
        return System.currentTimeMillis() - startShowTimestamp < defaultDuration;
    }

    public static void showLuckHud(boolean luck){
        startShowTimestamp = System.currentTimeMillis();
        if(luck) startShowTimestamp += 2000l;
        showLuck = luck;
    }

    public static Component getBaseLuck() {
        return Component.translatable("hud.potioneer.base_luck");
    }

    public static Component getTempLuck() {
        return Component.translatable("hud.potioneer.temp_luck");
    }

    public static Component getDecayLuck() {
        return Component.translatable("hud.potioneer.decay_luck");
    }

    public static Component getLuckData() {
        return Component.translatable("hud.potioneer.luck");
    }

    public static Component getNameComponent(){
        if(Minecraft.getInstance().level != null){
            Entity ent = Minecraft.getInstance().level.getEntity(entityId);
            if(ent != null){
                return Component.translatable("hud.potioneer.target_appraisal_hud", ent.getDisplayName(), lucks);
            }
        }
        return Component.translatable("hud.potioneer.target_appraisal_hud", "Unknown", lucks);
    }

    public static Component getHealthComponent(){
        return Component.translatable("hud.potioneer.target_appraisal_hud_health", Math.ceil(health), Math.ceil(maxHealth));
    }

    public static Component getSanityComponent(){
        return Component.translatable("hud.potioneer.target_appraisal_hud_sanity", Math.ceil(sanity), Math.ceil(maxSanity));
    }

    public static Component getSpiritualityComponent(){
        return Component.translatable("hud.potioneer.target_appraisal_hud_spirituality", Math.ceil(spirituality), Math.ceil(maxSpirituality));
    }

    public static float getPosLuck(){return basePos + tempPos + decayPos;}
    public static float getMaxLuck(){return getPosLuck() + Math.max(0, baseMax + tempMax + decayMax);}
    public static float getMinLuck(){return -getPosLuck() + Math.max(0, baseMin + tempMin + decayMin);}
    public static float getLuck(){return lucks;}

    public static void setAppraisalData(int incomingId, float[] data, boolean luck) {
        if(luck){
            tempMin = data[0];
            tempPos = data[1];
            tempMax = data[2];
            decayMin = data[3];
            decayPos = data[4];
            decayMax = data[5];
            baseMin = data[6];
            basePos = data[7];
            baseMax = data[8];
            lucks = data[9];
        } else {
            health = data[0];
            maxHealth = data[1];
            spirituality = data[2];
            maxSpirituality = data[3];
            sanity = data[4];
            maxSanity = data[5];
        }
        entityId = incomingId;
    }
}
