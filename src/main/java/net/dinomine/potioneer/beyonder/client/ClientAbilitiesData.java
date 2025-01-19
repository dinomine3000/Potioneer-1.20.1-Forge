package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
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

    public static void setAbilities(List<AbilityInfo> abilities2) {
        abilities = new ArrayList<>(abilities2);
        hotbar = new ArrayList<>();
        for(int i = 0; i < abilities.size(); i++){
            hotbar.add(i);
        }
    }

    public static ArrayList<Integer> getHotbar() {
        return hotbar;
    }

    public static void setHotbar(ArrayList<Integer> hotbar2) {
        hotbar = hotbar2;
    }

    private static ArrayList<AbilityInfo> abilities;
    private static ArrayList<Integer> hotbar;
    private static int caret = 0;
    public static boolean showHotbar = false;

    public static void changeCaret(int diff){
        caret = Math.floorMod(caret + diff, hotbar.size());
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

    public static void useAbility(Player player){
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            System.out.println(caret);
            cap.getAbilitiesManager().useAbility(cap, player, caret);
        });
    }

}
