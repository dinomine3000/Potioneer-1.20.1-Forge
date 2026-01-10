package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerSyncHotbarMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ClientAbilitiesData {
    public static ArrayList<AbilityInfo> getAbilities() {
        return new ArrayList<>(abilities.values().stream().toList());
    }

    public static void setShowHotbar(boolean val){
        if(configScreenOpenAnimation) return;
        if(!showHotbar && val && !hotbar.isEmpty()){
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("potioneer.ability_name." + abilities.get(hotbar.get(caret)).descId()), true);
            openAnimation = true;
            openingAnimationPercent = 0;
            showHotbar = true;
        }
        if(openAnimation && !val){
            openAnimation = false;
            openingAnimationPercent = 1;
        }
    }

    public static void showHotbarOnConfigScreen(boolean show){
        if(hotbar.isEmpty()) return;
        if(!configScreenOpenAnimation && show){
            configScreenOpenAnimation = true;
            openingAnimationPercent = 0;
        }
        if(configScreenOpenAnimation && !show){
            configScreenOpenAnimation = false;
            openingAnimationPercent = 1;
        }
    }

    public static void updateCaret(){
        if(hotbar.isEmpty()){
            caret = 0;
            return;
        }
        caret = Mth.clamp(caret, 0, hotbar.size() - 1);
    }

    public static void setCooldown(String cAblId, int cd, int maxCd){
        cooldowns.put(cAblId, cd);
        abilities.put(cAblId, abilities.get(cAblId).withMaxCd(maxCd));
    }

    public static void setAbilities(LinkedHashMap<String, AbilityInfo> abilities2, boolean changingPath) {
        abilities = new LinkedHashMap<>();
        for (String cAblId : abilities2.keySet()) {
            AbilityInfo ability = abilities2.get(cAblId).withCompleteId(cAblId);
            if (ability != null && !cAblId.isEmpty()) {
                abilities.put(cAblId, ability);
            }
        }
        if(changingPath) hotbar = new ArrayList<>();
        if(changingPath) quickSelect = "";
        cooldowns = new HashMap<>();
        for (String abilityId : abilities.keySet()) {
            cooldowns.put(abilityId, 0);
        }
        if(!hotbar.isEmpty()){
            hotbar.removeIf(cAblId -> !abilities.containsKey(cAblId));
        }
        if(!quickSelect.isEmpty() && !abilities.containsKey(quickSelect)) quickSelect = "";
        setHotbarChanged();
    }

    public static void setEnabledList(Map<String, Boolean> map){
        enabledList = map;
    }

    public static String getQuickAbilityCaret(){
        return quickSelect;
    }

    public static void setQuickAbilityCaret(String id){
        quickSelect = id;
    }

    public static ArrayList<String> getHotbar() {
        return hotbar;
    }

    public static void setHotbar(ArrayList<String> hotbar2) {
        hotbar = hotbar2;
        if(!hotbar.isEmpty()){
            caret = Mth.clamp(caret, 0, hotbar.size() - 1);
        }
    }

    public static void tick(float dt){
        scaleAnimationTime += dt;
        if(scaleAnimationTime > 5){
            scaleAnimationTime = -5;
        }
        if(Minecraft.getInstance().isSingleplayer() && Minecraft.getInstance().isPaused()) return;

        time += dt;
        if(time > 1){
            for(Map.Entry<String, AbilityInfo> entry: abilities.entrySet()){
                if(cooldowns.get(entry.getKey()) > 0) cooldowns.put(entry.getKey(), cooldowns.get(entry.getKey())-1);
            }
            time = 0;
        }
    }

    public static int getCooldown(){
        return getCooldown(caret);
    }

    public static int getCooldown(String cAblId){
        return cooldowns.getOrDefault(cAblId, 0);
    }

    public static int getCooldown(int pos){
        if(hotbar.isEmpty()) return 0;
        String cAblId = hotbar.get(Math.floorMod(pos, hotbar.size()));
        return cooldowns.get(cAblId);
    }

    public static int getMaxCooldown(String cAblId){
        if(!abilities.containsKey(cAblId)) return 1;
        return abilities.get(cAblId).maxCooldown();
    }

    public static int getMaxCooldown(int pos){
        if(hotbar.isEmpty()) return 1;
        return abilities.get(hotbar.get(Math.floorMod(pos, hotbar.size()))).maxCooldown();
    }

    public static void setHotbarChanged(){
        if(!hotbar.isEmpty()){
            caret = Mth.clamp(caret, 0, hotbar.size() - 1);
        } else {
            caret = 0;
        }
        PacketHandler.INSTANCE.sendToServer(new PlayerSyncHotbarMessage(getHotbar(), getQuickAbilityCaret()));
//        Minecraft.getInstance().player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
//            cap.getAbilitiesManager().clientHotbar = new ArrayList<>(hotbar);
//        });
    }

    public static void animationTick(float dt){
        if(animationTime > 0) animationTime = Math.max(animationTime - dt, 0);
        if(animationTime < 0) animationTime = Math.min(animationTime + dt, 0);
        float diff = (openAnimation || configScreenOpenAnimation) ? dt : -dt;
        openingAnimationPercent = Mth.clamp(openingAnimationPercent + diff/10, 0, 1);
        if(openingAnimationPercent <= 0){
            showHotbar = false;
        }
    }

    public static boolean openAnimation = false;
    public static boolean configScreenOpenAnimation = false;
    public static float openingAnimationPercent = 0;
    public static final float maxAnimationtime = 0.65f*20;
    public static float animationTime = 0;
    private static float time = 0;
    public static float scaleAnimationTime = 0;
    private static HashMap<String, AbilityInfo> abilities = new HashMap<>();
    //private static ArrayList<String> abilitiesByIndex;
    private static Map<String, Integer> cooldowns = new HashMap<>(0);
    private static Map<String, Boolean> enabledList = new HashMap<>(0);
    private static ArrayList<String> hotbar;
    private static String quickSelect = "";
    /**
     * caret refers to the index in the hotbar -> current selected ability in hotbar
     */
    private static int caret = 0;
    public static boolean showHotbar = false;

    public static void changeCaret(int diff){
        if(animationTime != 0 || hotbar.isEmpty()) return;
        caret = Math.floorMod(caret + diff, hotbar.size());
        animationTime = diff < 0 ? -maxAnimationtime : maxAnimationtime;
        if(Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.displayClientMessage(Component.translatable("potioneer.ability_name." + abilities.get(hotbar.get(caret)).descId()), true);
    }

    public static int getCaret(){
        return caret;
    }

    public static AbilityInfo getCurrentAbility(){
        return getAbilityAt(caret);
    }

    public static AbilityInfo getAbilityAt(int caretPos){
        if(hotbar.isEmpty()) return null;
        return abilities.get(hotbar.get(Math.floorMod(caretPos, hotbar.size())));
    }

    public static boolean isEnabled(String cAblId){
        return enabledList.getOrDefault(cAblId, true);
    }

    public static boolean isEnabled(int pos){
        if(enabledList.isEmpty()){
            System.out.println("enabled list is empty");
            return false;
        }
        String cAblId = hotbar.get(Math.floorMod(pos, hotbar.size()));
        return enabledList.get(cAblId);
    }

    public static boolean useQuickAbility(Player player){
        if(quickSelect.isEmpty() && hotbar.isEmpty()) return false;
        return useAbility(player, quickSelect.isEmpty() ? hotbar.get(caret) : quickSelect, quickMode);
    }

    public static boolean useAbility(Player player, boolean primary){
        return useAbility(player, hotbar.get(Math.floorMod(caret, hotbar.size())), primary);
    }

    public static boolean useAbility(Player player, String cAblId, boolean primary){
        if(abilities.isEmpty() || cAblId.isEmpty()) return false;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
//                    System.out.println(caret);
//                    ClientStatsData.setSpirituality(ClientStatsData.getPlayerSpirituality() - abilities.get(caret).cost());
            cap.getAbilitiesManager().useAbility(cap, player, cAblId, true, false);
            enabledList.put(cAblId, false);
        });
//        if(cooldowns.get(cAblId) == 0){
//        } else if(cooldowns.get(cAblId) > 0){
//            player.sendSystemMessage(Component.translatable("potioneer.message.ability_cooldown"));
//        } else {
//            player.sendSystemMessage(Component.translatable("potioneer.message.ability_disabled"));
//        }
        return true;
    }

}
