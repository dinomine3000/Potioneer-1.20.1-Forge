package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientAbilitiesData {
    public static ArrayList<AbilityInfo> getAbilities() {
        return abilities;
    }

    public static void setCooldown(int caret, int cd){
        cooldowns.set(caret, cd);
    }

    public static void setAbilities(List<AbilityInfo> abilities2) {
        abilities = new ArrayList<>(abilities2);
        hotbar = new ArrayList<>();
        cooldowns = new ArrayList<>();
        for(int i = 0; i < abilities.size(); i++){
            //TODO hotbar is stand in for debugging. the hotbar should not automatically be filled on client side
            hotbar.add(i);
            cooldowns.add(0);
        }
        caret = Math.min(caret, hotbar.size()-1);
    }

    public static void setEnabledList(ArrayList<Boolean> list){
        enabledList = list;
    }

    public static ArrayList<Integer> getHotbar() {
        return hotbar;
    }

    public static void setHotbar(ArrayList<Integer> hotbar2) {
        hotbar = hotbar2;
    }

    public static void tick(float dt){
        time += dt;
        if(time > 1){
            for(int i = 0; i < cooldowns.size(); i++){
                if(cooldowns.get(i) > 0) cooldowns.set(i, cooldowns.get(i)-1);
            }
            time = 0;
        }
    }

    public static int getCooldown(){
        return getCooldown(caret);
    }

    public static int getCooldown(int pos){
        return cooldowns.get(Math.floorMod(pos, cooldowns.size()));
    }

    public static int getMaxCooldown(int pos){
        return abilities.get(Math.floorMod(pos, abilities.size())).maxCooldown();
    }

    public static int getMaxCooldown(){
        return getMaxCooldown(caret);
    }

    public static void animationTick(float dt){
        if(animationTime > 0) animationTime = Math.max(animationTime - dt, 0);
        if(animationTime < 0) animationTime = Math.min(animationTime + dt, 0);
    }

    public static final float maxAnimationtime = 0.65f*20;
    public static float animationTime = 0;
    private static float time = 0;
    private static ArrayList<AbilityInfo> abilities;
    private static ArrayList<Integer> cooldowns = new ArrayList<>(0);
    private static ArrayList<Boolean> enabledList = new ArrayList<>(0);
    private static ArrayList<Integer> hotbar;
    private static int caret = 0;
    public static boolean showHotbar = false;

    public static void changeCaret(int diff){
        if(animationTime != 0 || hotbar.isEmpty()) return;
        caret = Math.floorMod(caret + diff, hotbar.size());
        animationTime = diff < 0 ? -maxAnimationtime : maxAnimationtime;
    }

    public static int getCaret(){
        return caret;
    }

    public static AbilityInfo getCurrentAbility(){
        return getAbilityAt(caret);
    }

    public static AbilityInfo getAbilityAt(int caretPos){
        return abilities.get(hotbar.get(Math.floorMod(caretPos, hotbar.size())));
    }

    public static boolean isEnabled(int pos){
        return enabledList.get(Math.floorMod(pos, enabledList.size()));
    }

    public static boolean hasAbilities(){
        return !abilities.isEmpty();
    }

    public static boolean useAbility(Player player){
        if(abilities.isEmpty()) return false;
        if(cooldowns.get(caret) == 0){
            if(ClientStatsData.getPlayerSpirituality() >= abilities.get(caret).cost()){
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    System.out.println(caret);
                    ClientStatsData.setSpirituality(ClientStatsData.getPlayerSpirituality() - abilities.get(caret).cost());
                    cap.getAbilitiesManager().useAbility(cap, player, caret);
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
