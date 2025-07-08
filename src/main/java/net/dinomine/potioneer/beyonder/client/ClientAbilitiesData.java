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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientAbilitiesData {
    public static ArrayList<AbilityInfo> getAbilities() {
        return abilities;
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

    public static int getCaretForAbility(String abilityId){
        for (int i = 0; i < abilities.size(); i++) {
            if(abilities.get(i).descId().equalsIgnoreCase(abilityId)) return i;
        }
        return -1;
    }

    public static void setCooldown(String abilityId, int cd, int maxCd){
        cooldowns.put(abilityId, cd);
        int idx = getCaretForAbility(abilityId);
        abilities.set(idx, abilities.get(idx).copy(maxCd));
    }

    public static void setAbilities(List<AbilityInfo> abilities2, boolean changingPath) {
        //diff here is necessary because lists of abilities grow to the left.
        //that is, sequence 8 abilities have the new abilities to the left of the old ones.
        //diff is then used to adjust the old hotbar
        int diff = 0;
        if(!abilities.isEmpty()) diff = abilities2.size() - abilities.size();
        abilities = new ArrayList<>(abilities2);
        if(changingPath) hotbar = new ArrayList<>();
        if(changingPath) quickSelect = -1;
        cooldowns = new HashMap<>();
        for (AbilityInfo ability : abilities) {
            cooldowns.put(ability.descId(), 0);
        }
        if(!hotbar.isEmpty()){
            for(int i = hotbar.size() - 1; i > -1; i--){
                if(hotbar.get(i) + diff < 0){
                    hotbar.remove(i);
                    continue;
                }
                hotbar.set(i, Mth.clamp(hotbar.get(i) + diff ,0, abilities.size()-1));
            }
        }
        setHotbarChanged();
    }

    public static void setEnabledList(Map<String, Boolean> map){
        enabledList = map;
    }

    public static int getQuickAbilityCaret(){
        return quickSelect;
    }

    public static void setQuickAbilityCaret(int caret){
        quickSelect = caret;
    }

    public static ArrayList<Integer> getHotbar() {
        return hotbar;
    }

    public static void setHotbar(ArrayList<Integer> hotbar2) {
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
            for(AbilityInfo ability: abilities){
                if(cooldowns.get(ability.descId()) > 0) cooldowns.put(ability.descId(), cooldowns.get(ability.descId())-1);
            }
            time = 0;
        }
    }

    public static int getCooldown(){
        return getCooldown(caret, true);
    }

    public static int getCooldown(int pos, boolean readInHotbar){
        if(readInHotbar){
            int ablIdx = hotbar.get(Math.floorMod(pos, hotbar.size()));
            return cooldowns.get(abilities.get(ablIdx).descId());
        } else {
            int ablIdx = Math.floorMod(pos, cooldowns.size());
            return cooldowns.get(abilities.get(ablIdx).descId());
        }
    }

    public static int getMaxCooldown(int pos, boolean readInHotbar){
        if(readInHotbar){
            return abilities.get(hotbar.get(Math.floorMod(pos, hotbar.size()))).maxCooldown();
        } else {
            return abilities.get(Math.floorMod(pos, abilities.size())).maxCooldown();
        }
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

    public static int getMaxCooldown(){
        return getMaxCooldown(caret, true);
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
    private static ArrayList<AbilityInfo> abilities = new ArrayList<>();
    private static Map<String, Integer> cooldowns = new HashMap<>(0);
    private static Map<String, Boolean> enabledList = new HashMap<>(0);
    private static ArrayList<Integer> hotbar;
    private static int quickSelect = -1;
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

    public static boolean isEnabled(int pos, boolean readInHotbar){
        if(enabledList.isEmpty()){
            System.out.println("enabled list is empty");
            return false;
        }
        int ablIdx;
        if(readInHotbar){
            ablIdx = hotbar.get(Math.floorMod(pos, hotbar.size()));
        } else {
            ablIdx = Math.floorMod(pos, enabledList.size());
        }
        return enabledList.get(abilities.get(ablIdx).descId());
    }

    public static boolean hasAbilities(){
        return !abilities.isEmpty();
    }

    public static boolean useQuickAbility(Player player){
        return useAbility(player, quickSelect == -1 ? caret : quickSelect, quickSelect == -1);
    }

    public static boolean useAbility(Player player){
        return useAbility(player, caret, true);
    }

    public static boolean useAbility(Player player, int newCaret, boolean inHotbar){
        if(abilities.isEmpty() || newCaret < 0) return false;
        if(inHotbar){
            if(hotbar.isEmpty()) return false;
            newCaret = hotbar.get(newCaret);
        }
        if(cooldowns.get(abilities.get(newCaret).descId()) == 0){
            if(ClientStatsData.getPlayerSpirituality() >= abilities.get(newCaret).cost()){
                int position = newCaret;
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
//                    System.out.println(caret);
//                    ClientStatsData.setSpirituality(ClientStatsData.getPlayerSpirituality() - abilities.get(caret).cost());
                    cap.getAbilitiesManager().useAbility(cap, player, position);
                    enabledList.put(abilities.get(position).descId(), false);
                });
            } else {
                player.sendSystemMessage(Component.literal("Not enough spirituality to cast ability"));
            }
        } else {
            player.sendSystemMessage(Component.literal("Ability still on cooldown..."));
        }
        return true;
    }

}
