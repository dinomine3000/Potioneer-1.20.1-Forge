package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.*;

import java.util.ArrayList;
import java.util.List;

public class WheelOfFortunePathway extends BeyonderPathway {

    public WheelOfFortunePathway() {
        super("Wheel_of_Fortune", 0x808080, new int[]{2500, 1500, 1200, 900, 600, 450, 375, 250, 200, 100});
    }

    @Override
    public int getX(){
        return 0;
    }

    @Override
    public int getY(){
        return 0;
    }

    @Override
    public int getAbilityX(){return 5;}

    @Override
    public float[] getStatsFor(int sequence){
        return switch (sequence){
            case 9 -> new float[]{0, 0, 2, 0, 0};
            case 8 -> new float[]{0, 0, 4, 0, 2};
            case 7 -> new float[]{4, 0, 6, 2, 2};
            case 6 -> new float[]{5, 0, 8, 2, 4};
            case 5 -> new float[]{6, 0, 8, 2, 4};
            default -> new float[]{0, 0, 0, 0, 0};
        };
    }

    @Override
    public List<Ability> getAbilities(int sequence){
        ArrayList<Ability> abilities = new ArrayList<>();
        switch(sequence){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                abilities.add(new WheelBoneMealAbility(sequence));
                abilities.add(new PatienceAbility(sequence));
                abilities.add(new LuckDamageReductionAbility(sequence));
                abilities.add(new CalamityIncreaseAbility(sequence));
            case 8:
                abilities.add(new BlockSniffAbility(sequence));
                abilities.add(new LuckBoostAbility(sequence));
                abilities.add(new CheckLuckAbility(sequence));
                abilities.add(new FortuneAbility(sequence));
                abilities.add(new SilkTouchAbility(sequence));

            case 9:
                abilities.add(new MinerLightAbility(sequence));
                abilities.add(new ConjurePickaxeAbility(sequence));
                abilities.add(new MiningSpeedAbility(sequence));
                abilities.add(new CogitationAbility(sequence));
        }

        return abilities;
//        mng.setPathwayAbilities(activeAbilities);
    }

    @Override
    public String getSequenceNameFromId(int seq, boolean show){
        return show ? getSequenceNameFromId(seq).replace("_", " ") : getSequenceNameFromId(seq).toLowerCase();
    }

    private String getSequenceNameFromId(int seq){
        return switch (seq) {
            case 9 -> "Miner";
            case 8 -> "Appraiser";
            case 7 -> "Gambler";
            case 6 -> "Lucky_One";
            case 5 -> "Source_of_Misfortune";
            default -> "";
        };
    }

    public int getSequenceColorFromLevel(int seq){
        return switch (seq) {
            case 9 -> 10724259;
            case 8 -> 16383885;
            case 7 -> 14989311;
            default -> 0;
        };
    }

    @Override
    public int getId() {
        return 0;
    }

}
