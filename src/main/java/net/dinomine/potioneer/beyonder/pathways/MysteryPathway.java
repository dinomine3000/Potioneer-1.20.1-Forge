package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;

import java.util.ArrayList;

public class MysteryPathway extends Beyonder {

    private static ArrayList<Ability> passiveAbilities9;

    public MysteryPathway(int sequence){
        super(sequence, "Mystery");
        this.color = 0x408040;
    }

    public static void init(){
        passiveAbilities9 = new ArrayList<>();
    }

    public static ArrayList<Ability> getPassiveAbilities(int sequence) {
        return passiveAbilities9;
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
