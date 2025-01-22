package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.redpriest.WeaponProficiencyAbility;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;

public class MysteryPathway extends Beyonder {

    public MysteryPathway(int sequence){
        super(sequence, "Mystery");
        this.color = 0x408040;
    }

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
        ArrayList<Ability> passiveAbilities9 = new ArrayList<>();
        ArrayList<Ability> activeAbilities9 = new ArrayList<>();

        mng.setPathwayActives(activeAbilities9);
        mng.setPathwayPassives(passiveAbilities9);
    }


    @Override
    public int getId() {
        return 20 + this.sequence;
    }

    public static String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    public static String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Trickmaster";
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
