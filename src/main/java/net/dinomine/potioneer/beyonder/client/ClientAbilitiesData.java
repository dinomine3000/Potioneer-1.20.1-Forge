package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerSyncHotbarMessage;
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

    public static void updateCaret(){
        if(hotbar.isEmpty()){
            caret = 0;
            return;
        }
        caret = Mth.clamp(caret, 0, hotbar.size() - 1);
    }

    public static void setCooldown(String abilityId, int cd, int maxCd){
        cooldowns.put(abilityId, cd);
        abilities.put(abilityId, abilities.get(abilityId).copy(maxCd));
    }

    public static void setAbilities(List<AbilityInfo> abilities2, boolean changingPath) {
        abilities = new LinkedHashMap<>();
        for (AbilityInfo ability : abilities2) {
            if (ability != null && !ability.normalizedId().isEmpty()) {
                abilities.put(ability.normalizedId(), ability);
            }
        }
        if(changingPath) hotbar = new ArrayList<>();
        if(changingPath) quickSelect = "";
        cooldowns = new HashMap<>();
        for (String abilityId : abilities.keySet()) {
            cooldowns.put(abilityId, 0);
        }
        if(!hotbar.isEmpty()){
            hotbar.removeIf(ablId -> !abilities.containsKey(ablId));
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

    public static int getCooldown(String ablId){
        return cooldowns.getOrDefault(ablId, 0);
    }

    public static int getCooldown(int pos){
        if(hotbar.isEmpty()) return 0;
        String ablId = hotbar.get(Math.floorMod(pos, hotbar.size()));
        return cooldowns.get(ablId);
    }

    public static int getMaxCooldown(String ablId){
        if(!abilities.containsKey(ablId)) return 1;
        return abilities.get(ablId).maxCooldown();
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
        openingAnimationPercent = Mth.clamp(openingAnimationPercent + (openAnimation ? dt : -dt)/10, 0, 1);
        if(openingAnimationPercent <= 0){
            showHotbar = false;
        }
    }

    public static boolean openAnimation = false;
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

    public static boolean isEnabled(String ablId){
        return enabledList.getOrDefault(ablId, true);
    }

    public static boolean isEnabled(int pos){
        if(enabledList.isEmpty()){
            System.out.println("enabled list is empty");
            return false;
        }
        String ablId = hotbar.get(Math.floorMod(pos, hotbar.size()));
        return enabledList.get(ablId);
    }

    public static boolean useQuickAbility(Player player){
        if(quickSelect.isEmpty() && hotbar.isEmpty()) return false;
        return useAbility(player, quickSelect.isEmpty() ? hotbar.get(caret) : quickSelect);
    }

    public static boolean useAbility(Player player){
        return useAbility(player, hotbar.get(Math.floorMod(caret, hotbar.size())));
    }

    public static boolean useAbility(Player player, String ablId){
        if(abilities.isEmpty() || ablId.isEmpty()) return false;
        if(cooldowns.get(ablId) == 0){
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
//                    System.out.println(caret);
//                    ClientStatsData.setSpirituality(ClientStatsData.getPlayerSpirituality() - abilities.get(caret).cost());
                cap.getAbilitiesManager().useAbility(cap, player, ablId, true, false);
                enabledList.put(ablId, false);
            });
        } else if(cooldowns.get(ablId) > 0){
            player.sendSystemMessage(Component.translatable("potioneer.message.ability_cooldown"));
        } else {
            player.sendSystemMessage(Component.translatable("potioneer.message.ability_disabled"));
        }
        return true;
    }

}
