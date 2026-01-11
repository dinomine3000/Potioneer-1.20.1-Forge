package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
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
        return switch (sequence%10){
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
        switch(sequence%10){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                abilities.add(Abilities.MINER_BONE_MEAL.create(sequence));
                abilities.add(Abilities.PATIENCE.create(sequence));
                abilities.add(Abilities.DODGE_DAMAGE.create(sequence));
                abilities.add(Abilities.CALAMITY_INCREASE.create(sequence));
            case 8:
                abilities.add(Abilities.BLOCK_SNIFF.create(sequence));
                abilities.add(Abilities.LUCK_BOOST.create(sequence));
                abilities.add(Abilities.CHECK_LUCK.create(sequence));
                abilities.add(Abilities.FORTUNE_ABILITY.create(sequence));
                abilities.add(Abilities.SILK_TOUCH_ABILITY.create(sequence));

            case 9:
                abilities.add(Abilities.MINER_LIGHT.create(sequence));
                abilities.add(Abilities.MINING_SPEED.create(sequence));
                abilities.add(Abilities.MINING_SPEED.create(sequence));
        }

        return abilities;
//        mng.setPathwayAbilities(activeAbilities);
    }

    @Override
    public String getSequenceNameFromId(int seq, boolean show){
        return show ? getSequenceNameFromId(seq).replace("_", " ") : getSequenceNameFromId(seq).toLowerCase();
    }

    private String getSequenceNameFromId(int seq){
        return switch (seq%10) {
            case 9 -> "Miner";
            case 8 -> "Appraiser";
            case 7 -> "Nimble_Gambler";
            case 6 -> "Lucky_One";
            case 5 -> "Source_of_Misfortune";
            case 4 -> "Commander_of_Fate";
            default -> "";
        };
    }

    @Override
    public int getSequenceColorFromLevel(int seq){
        return switch (seq%10) {
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
