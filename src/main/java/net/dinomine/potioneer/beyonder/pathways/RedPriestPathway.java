package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;

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
        return getAbilities(sequence%10, sequence%10);
    }

    @Override
    public List<Ability> getAbilities(int ofSequenceLevel, int atSequenceLevel){
        ArrayList<Ability> abilities = new ArrayList<>();

        switch(ofSequenceLevel%10){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                abilities.add(Abilities.FIRE_AURA.create(atSequenceLevel));
                abilities.add(Abilities.FIRE_BUFF.create(atSequenceLevel));
                abilities.add(Abilities.FIRE_BALL.create(atSequenceLevel));
                abilities.add(Abilities.FIRE_SWORD.create(atSequenceLevel));
            case 8:
                abilities.add(Abilities.PRIEST_LIGHT.create(atSequenceLevel));
                abilities.add(Abilities.LIGHT_BUFF.create(atSequenceLevel));
                abilities.add(Abilities.HEALING.create(atSequenceLevel));
                abilities.add(Abilities.MELT_ABILITY.create(atSequenceLevel));
                abilities.add(Abilities.PURIFICATION.create(atSequenceLevel));
            case 9:
                abilities.add(Abilities.WEAPON_PROFICIENCY.create(atSequenceLevel));
        }

        return abilities;
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
