package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.redpriest.StatBonusAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.WeaponProficiencyAbility;
import net.dinomine.potioneer.beyonder.effects.redpriest.BeyonderWeaponProficiencyEffect;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;

public class RedPriestPathway extends Beyonder {

    public RedPriestPathway(int sequence){
        super(sequence, "Red_Priest");
        this.color = 0x804040;
    }

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
        WeaponProficiencyAbility abl = new WeaponProficiencyAbility(sequence);
//        StatBonusAbility stats = new StatBonusAbility(sequence);

        ArrayList<Ability> passiveAbilities9 = new ArrayList<>();
        passiveAbilities9.add(abl);
        ArrayList<Ability> activeAbilities9 = new ArrayList<>();
        activeAbilities9.add(abl);
//        activeAbilities9.add(stats);

        mng.setPathwayActives(activeAbilities9);
        mng.setPathwayPassives(passiveAbilities9);
    }

    @Override
    public int getId() {
        return 30 + this.sequence;
    }


    public static String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    public static String getSequenceName(int seq){
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
