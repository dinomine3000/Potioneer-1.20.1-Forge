package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.*;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;

public class WheelOfFortunePathway extends BeyonderPathway {

    public WheelOfFortunePathway(int sequence) {
        super(sequence, "Wheel_of_Fortune");
        this.color = 0x808080;
        this.maxSpirituality = new int[]{2500, 1500, 1200, 900, 600, 450, 375, 250, 200, 100};
    }

    public static int getX(){
        return 0;
    }

    public static int getY(){
        return 0;
    }

    public static float[] getStatsFor(int sequence){
        return switch (sequence){
            case 9 -> new float[]{0, 0, 2, 0, 0};
            case 8 -> new float[]{0, 0, 4, 0, 2};
            case 7 -> new float[]{4, 0, 6, 2, 2};
            case 6 -> new float[]{5, 0, 8, 2, 4};
            case 5 -> new float[]{6, 0, 8, 2, 4};
            default -> new float[]{0, 0, 0, 0, 0};
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
                activeAbilities.add(new WheelBoneMealAbility(sequence));
                activeAbilities.add(new GamblingAbility(sequence));
                activeAbilities.add(new LuckDamageReductionAbility(sequence));
                activeAbilities.add(new CalamityIncreaseAbility(sequence));
            case 8:
                activeAbilities.add(new BlockSniffAbility(sequence));
                activeAbilities.add(new LuckBoostAbility(sequence));
                activeAbilities.add(new CheckLuckAbility(sequence));
                activeAbilities.add(new FortuneAbility(sequence));
                activeAbilities.add(new SilkTouchAbility(sequence));

            case 9:
                activeAbilities.add(new MinerLightAbility(sequence));
                activeAbilities.add(new ConjurePickaxeAbility(sequence));
                activeAbilities.add(new MiningSpeedAbility(sequence));
                activeAbilities.add(new CogitationAbility(sequence));
        }

        mng.setPathwayActives(activeAbilities);
    }

    public static String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    public static String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Miner";
            case 8 -> "Appraiser";
            case 7 -> "Gambler";
            case 6 -> "Lucky_One";
            case 5 -> "Source_of_Misfortune";
            default -> "";
        };
    }

    public static int getSequenceColor(int seq){
        return switch (seq) {
            case 9 -> 10724259;
            case 8 -> 16383885;
            case 7 -> 14989311;
            default -> 0;
        };
    }

    @Override
    public int getId() {
        return sequence;
    }

}
