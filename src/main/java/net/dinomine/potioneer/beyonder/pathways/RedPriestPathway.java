package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.player.PlayerBeyonderStats;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RedPriestPathway extends Beyonder{

    private static ArrayList<BiConsumer<Player, PlayerBeyonderStats>> passiveAbilities = new ArrayList<>();

    public RedPriestPathway(int sequence){
        super(sequence, "Red_Priest");
        this.color = 0x804040;
    }

    public static void init(){
    }

    public static ArrayList<BiConsumer<Player, PlayerBeyonderStats>> getPassiveAbilities(int sequence) {
        return passiveAbilities;
    }


    @Override
    public int getId() {
        return 30 + this.sequence;
    }

    @Override
    public String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Warrior";
            case 8 -> "Pyromaniac";
            case 7 -> "Priest_of_Light";
            case 6 -> "Sun-Blessed";
            case 5 -> "Hunter";
            case 4 -> "Ice_Duke";
            case 3 -> "Prosperous_Prince";
            case 2 -> "Nature_Dictator";
            case 1 -> "Cataclysm_King";
            case 0 -> "Tyrant";
            default -> "";
        };
    }

}
