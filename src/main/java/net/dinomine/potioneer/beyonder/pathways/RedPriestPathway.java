package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.*;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;
import java.util.List;

public class RedPriestPathway extends BeyonderPathway {

    public RedPriestPathway(){
        super("Red_Priest", 0x804040, new int[]{2000, 1500, 1200, 1000, 800, 500, 320, 150, 100, 50});
    }

    @Override
    public int getX(){
        return 192;
    }

    @Override
    public int getY(){
        return 0;
    }

    @Override
    public int getAbilityX() {
        return 83;
    }

    @Override
    public float[] getStatsFor(int sequence){
        return switch (sequence%10){
            case 9 -> new float[]{4, 1, 0, 0, 0};
            case 8 -> new float[]{4, 1, 0, 0, 2};
            case 7 -> new float[]{6, 3, 3, 0, 4};
            case 6 -> new float[]{8, 3, 3, 1, 5};
            case 5 -> new float[]{12, 5, 4, 2, 5};
            default -> new float[]{0, 0, 0, 0, 0};
        };
    }

    @Override
    public List<Ability> getAbilities(int sequence){
        //ArrayList<Ability> passiveAbilities = new ArrayList<>();
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
                abilities.add(Abilities.FIRE_AURA.create(sequence));
                abilities.add(Abilities.FIRE_BUFF.create(sequence));
                abilities.add(Abilities.FIRE_BALL.create(sequence));
                abilities.add(Abilities.FIRE_SWORD.create(sequence));
            case 8:
                abilities.add(Abilities.PRIEST_LIGHT.create(sequence));
                abilities.add(Abilities.LIGHT_BUFF.create(sequence));
                abilities.add(Abilities.HEALING.create(sequence));
                abilities.add(Abilities.MELT_ABILITY.create(sequence));
                abilities.add(Abilities.PURIFICATION.create(sequence));
            case 9:
                abilities.add(Abilities.WEAPON_PROFICIENCY.create(sequence));
        }

        return abilities;
//        mng.setPathwayAbilities(activeAbilities);
        //mng.setPathwayPassives(passiveAbilities);
    }

    @Override
    public int getId() {
        return 3;
    }


    @Override
    public String getSequenceNameFromId(int seq, boolean show){
        return show ? getSequenceNameFromId(seq).replace("_", " ") : getSequenceNameFromId(seq).toLowerCase();
    }

    private String getSequenceNameFromId(int seq){
        return switch (seq%10) {
            case 9 -> "Warrior";
            case 8 -> "Fire_Priest";
            case 7 -> "Pyromaniac";
            case 6 -> "Sun-Blessed";
            case 5 -> "Hunter";
            default -> "";
        };
    }

    @Override
    public int getSequenceColorFromLevel(int seq){
        return switch (seq%10) {
            case 9 -> 10251629;
            case 8 -> 16749056;
            case 7 -> 16775936;
            default -> 0;
        };
    }

}
