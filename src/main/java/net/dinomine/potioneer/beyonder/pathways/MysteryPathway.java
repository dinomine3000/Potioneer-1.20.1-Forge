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
        ArrayList<Ability> passiveAbilities = new ArrayList<>();
        ArrayList<Ability> activeAbilities9 = new ArrayList<>();

        switch(sequence){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                activeAbilities9.add(new AirBulletAbility(sequence));
            case 9:
                activeAbilities9.add(new ReachAbility(sequence));
                activeAbilities9.add(new DoorOpeningAbility(sequence));
                activeAbilities9.add(new SpiritualityRegenAbility(sequence));
                activeAbilities9.add(new InvisibilityAbility(sequence));
                passiveAbilities.add(new SpiritualityRegenAbility(sequence));
        }
        mng.setPathwayActives(activeAbilities9);
        mng.setPathwayPassives(passiveAbilities);
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
            case 8 -> "Acrobat";
            case 7 -> "Voodoo_Assassin";
            case 6 -> "Parasite";
            case 5 -> "Traveler";
            default -> "";
        };
    }

}
