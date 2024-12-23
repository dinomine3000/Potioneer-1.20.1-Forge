package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.player.PlayerBeyonderStats;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class ParagonPathway extends Beyonder{

    private static ArrayList<BiConsumer<Player, PlayerBeyonderStats>> passiveAbilities = new ArrayList<>();

    public ParagonPathway(int sequence){
        super(sequence, "Paragon");
        this.color = 0x908020;
    }

    public static void init(){
    }

    public static ArrayList<BiConsumer<Player, PlayerBeyonderStats>> getPassiveAbilities(int sequence) {
        return passiveAbilities;
    }


    @Override
    public int getId() {
        return 40 + this.sequence;
    }

    @Override
    public String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Crafter";
            case 8 -> "Enchanter";
            case 7 -> "Conjurer";
            case 6 -> "Artisan";
            case 5 -> "Alchemist";
            case 4 -> "Mechanical_Creationist";
            case 3 -> "Knowledge_Magister";
            case 2 -> "";
            case 1 -> "";
            case 0 -> "Paragon";
            default -> "";
        };
    }

    public static void giveArmor(Player player, PlayerBeyonderStats cap){
    }

}
