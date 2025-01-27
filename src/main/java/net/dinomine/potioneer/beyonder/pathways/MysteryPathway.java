package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.mystery.*;
import net.dinomine.potioneer.beyonder.abilities.redpriest.WeaponProficiencyAbility;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;

public class MysteryPathway extends Beyonder {

    public MysteryPathway(int sequence){
        super(sequence, "Mystery");
        this.color = 0x408040;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 1000, 1000, 1000, 1000, 500, 100};
    }

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
//        AirBulletAbility abl = new AirBulletAbility(sequence);
        InvisibilityAbility abl = new InvisibilityAbility(sequence);
        DoorOpeningAbility door = new DoorOpeningAbility(sequence);
        ReachAbility reach = new ReachAbility(sequence);
        SpiritualityRegenAbility regen = new SpiritualityRegenAbility(sequence);

        ArrayList<Ability> passiveAbilities9 = new ArrayList<>();
        ArrayList<Ability> activeAbilities9 = new ArrayList<>();
        activeAbilities9.add(reach);
        activeAbilities9.add(abl);
        activeAbilities9.add(door);
        activeAbilities9.add(regen);
        passiveAbilities9.add(regen);

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
