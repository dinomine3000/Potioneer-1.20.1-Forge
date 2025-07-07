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
        this.maxSpirituality = new int[]{2500, 2500, 1700, 1600, 1600, 1500, 700, 600, 500, 100};
    }

    public static int getX(){
        return 128;
    }

    public static int getY(){
        return 0;
    }

    public static float[] getStatsFor(int sequence){
        return switch (sequence){
            case 9 -> new float[]{0, 0, 0, 0, 0};
            case 8 -> new float[]{0, 0, 0, 0, 1};
            case 7 -> new float[]{0, 1, 0, 0, 2};
            case 6, 5 -> new float[]{0, 2, 0, 0, 3};
            default -> new float[]{0, 3, 0, 0, 4};
        };
    }

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
        ArrayList<Ability> passiveAbilities = new ArrayList<>();
        ArrayList<Ability> activeAbilities = new ArrayList<>();

        switch(sequence){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                activeAbilities.add(new AirBulletAbility(sequence));
                FigurineSubstituteAbility figure = new FigurineSubstituteAbility(sequence);
                activeAbilities.add(figure);
                passiveAbilities.add(figure);
                activeAbilities.add(new PlagueAbility(sequence));
                activeAbilities.add(new PanaceaAbility(sequence));
                activeAbilities.add(new PushAbility(sequence));
            case 8:
//                activeAbilities.add(new RandomTeleportAbility(sequence));
                ReduceFallDamageAbility fall = new ReduceFallDamageAbility(sequence);
                StepUpAbility step = new StepUpAbility(sequence);

                activeAbilities.add(fall);
                passiveAbilities.add(fall);
                activeAbilities.add(step);
                passiveAbilities.add(step);
                activeAbilities.add(new LeapAbility(sequence));
            case 9:
                SpiritualityRegenAbility regen = new SpiritualityRegenAbility(sequence);
                ReachAbility reach = new ReachAbility(sequence);
                activeAbilities.add(reach);
                activeAbilities.add(new DoorOpeningAbility(sequence));
                activeAbilities.add(regen);
                activeAbilities.add(new InvisibilityAbility(sequence));
                passiveAbilities.add(regen);
                passiveAbilities.add(reach);
        }
        mng.setPathwayActives(activeAbilities);
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

    public static int getSequenceColor(int seq){
        return switch (seq) {
            case 9 -> 12117700;
            case 8 -> 65294;
            case 7 -> 16121785;
            default -> 0;
        };
    }

}
