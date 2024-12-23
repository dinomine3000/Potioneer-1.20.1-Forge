package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.PlayerBeyonderStats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MysteryPathway extends Beyonder{

    private static ArrayList<BiConsumer<Player, PlayerBeyonderStats>> passiveAbilities = new ArrayList<>();

    public MysteryPathway(int sequence){
        super(sequence, "Mystery");
        this.color = 0x408040;
    }

    public static void init(){
    }

    public static ArrayList<BiConsumer<Player, PlayerBeyonderStats>> getPassiveAbilities(int sequence) {
        return passiveAbilities;
    }


    @Override
    public int getId() {
        return 20 + this.sequence;
    }

    @Override
    public String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Tormenter";
            case 8 -> "Voodoo_Assassin";
            case 7 -> "Plague_Doctor";
            case 6 -> "Parasite";
            case 5 -> "Flyer";
            case 4 -> "Ice_Duke";
            case 3 -> "Prosperous_Prince";
            case 2 -> "Nature_Dictator";
            case 1 -> "Cataclysm_King";
            case 0 -> "Tyrant";
            default -> "";
        };
    }

}
