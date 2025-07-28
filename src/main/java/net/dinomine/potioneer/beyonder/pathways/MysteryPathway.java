package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.mystery.*;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;

public class MysteryPathway extends BeyonderPathway {

    public static final int MAX_SAP_DISTANCE = 4;

    public MysteryPathway(int sequence){
        super(sequence, "Mystery");
        this.color = 0x408040;
        this.maxSpirituality = new int[]{4000, 2744, 1960, 1400, 1000, 700, 420, 300, 140, 100};
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
            case 7 -> new float[]{2, 1, 0, 0, 5};
            case 6 -> new float[]{2, 1, 2, 0, 8};
            case 5 -> new float[]{4, 1, 2, 0, 8};
            default -> new float[]{6, 2, 2, 0, 10};
        };
    }

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
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
                activeAbilities.add(new FigurineSubstituteAbility(sequence));
                activeAbilities.add(new PlagueAbility(sequence));
                activeAbilities.add(new PanaceaAbility(sequence));
                activeAbilities.add(new PushAbility(sequence));
            case 8:
                activeAbilities.add(new ReduceFallDamageAbility(sequence));
                activeAbilities.add(new StepUpAbility(sequence));
                activeAbilities.add(new LeapAbility(sequence));
            case 9:
                activeAbilities.add(new ReachAbility(sequence));
                activeAbilities.add(new DoorOpeningAbility(sequence));
                activeAbilities.add(new SpiritualityRegenAbility(sequence));
                activeAbilities.add(new InvisibilityAbility(sequence));
                activeAbilities.add(new CogitationAbility(20 + sequence));
        }
        mng.setPathwayActives(activeAbilities);
        //mng.setPathwayPassives(passiveAbilities);
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
            case 9 -> "Trickster";
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
