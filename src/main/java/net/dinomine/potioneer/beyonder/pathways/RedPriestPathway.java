package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.*;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;

public class RedPriestPathway extends BeyonderPathway {

    public RedPriestPathway(int sequence){
        super(sequence, "Red_Priest");
        this.color = 0x804040;
        this.maxSpirituality = new int[]{2000, 1500, 1200, 1000, 800, 500, 320, 150, 100, 50};
    }

    public static int getX(){
        return 192;
    }

    public static int getY(){
        return 0;
    }

    public static float[] getStatsFor(int sequence){
        return switch (sequence){
            case 9 -> new float[]{4, 1, 0, 0, 0};
            case 8 -> new float[]{4, 1, 0, 0, 2};
            case 7 -> new float[]{6, 3, 3, 0, 4};
            case 6 -> new float[]{8, 3, 3, 1, 5};
            case 5 -> new float[]{12, 5, 4, 2, 5};
            default -> new float[]{0, 0, 0, 0, 0};
        };
    }

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
        //ArrayList<Ability> passiveAbilities = new ArrayList<>();
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
                activeAbilities.add(new FireAuraAbility(sequence));
                activeAbilities.add(new FireBuffAbility(sequence));
                activeAbilities.add(new FireBallAbility(sequence));
                activeAbilities.add(new ConjureFireSwordAbility(sequence));
            case 8:
                activeAbilities.add(new PriestLightAbility(sequence));
                activeAbilities.add(new LightBuffAbility(sequence));
                activeAbilities.add(new HealAbility(sequence));
                activeAbilities.add(new MeltAbility(sequence));
                activeAbilities.add(new PurificationAbility(sequence));
            case 9:
                activeAbilities.add(new WeaponProficiencyAbility(sequence));
                if(sequence < 8) activeAbilities.add(new CogitationAbility(30 + sequence));
        }

        mng.setPathwayAbilities(activeAbilities);
        //mng.setPathwayPassives(passiveAbilities);
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
            case 8 -> "Fire_Priest";
            case 7 -> "Pyromaniac";
            case 6 -> "Sun-Blessed";
            case 5 -> "Hunter";
            default -> "";
        };
    }

    public static int getSequenceColor(int seq){
        return switch (seq) {
            case 9 -> 10251629;
            case 8 -> 16749056;
            case 7 -> 16775936;
            default -> 0;
        };
    }

}
