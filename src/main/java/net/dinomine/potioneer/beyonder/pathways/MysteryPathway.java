package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.mystery.*;

import java.util.ArrayList;
import java.util.List;


public class MysteryPathway extends BeyonderPathway {
    public MysteryPathway(){
        super("Mystery", 0x408040, new int[]{4000, 2744, 1960, 1400, 1000, 700, 420, 300, 140, 100});
    }

    @Override
    public int getX(){
        return 128;
    }

    @Override
    public int getY(){
        return 0;
    }

    @Override
    public int getAbilityX() {
        return 57;
    }

    @Override
    public int getSequenceColorFromLevel(int sequenceLevel) {
        return switch (sequenceLevel%10) {
            case 9 -> 12117700;
            case 8 -> 65294;
            case 7 -> 16121785;
            default -> 0;
        };
    }

    @Override
    public List<Ability> getAbilities(int sequenceLevel) {
        ArrayList<Ability> activeAbilities = new ArrayList<>();

        switch(sequenceLevel%10){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                activeAbilities.add(Abilities.AIR_BULLET.create(sequenceLevel));
                activeAbilities.add(Abilities.PAPER_FIGURINE_SUBSTITUTE.create(sequenceLevel));
                activeAbilities.add(Abilities.PANACEA.create(sequenceLevel));
                activeAbilities.add(Abilities.PUSH.create(sequenceLevel));
            case 8:
                activeAbilities.add(Abilities.CANCEL_FALL_DAMAGE.create(sequenceLevel));
                activeAbilities.add(Abilities.STEP_UP.create(sequenceLevel));
                activeAbilities.add(Abilities.LEAP.create(sequenceLevel));
            case 9:
                activeAbilities.add(Abilities.EXTENDED_REACH.create(sequenceLevel));
                activeAbilities.add(Abilities.DOOR_OPENING.create(sequenceLevel));
                activeAbilities.add(Abilities.MYSTERY_REGEN.create(sequenceLevel));
                activeAbilities.add(Abilities.INVISIBILITY.create(sequenceLevel));
        }
        return activeAbilities;
    }

    @Override
    public String getSequenceNameFromId(int sequenceLevel, boolean show) {
        return show ? getSequenceName(sequenceLevel).replace("_", " ") : getSequenceName(sequenceLevel).toLowerCase();
    }

    @Override
    public float[] getStatsFor(int sequence){
        return switch (sequence%10){
            case 9 -> new float[]{0, 0, 0, 0, 0};
            case 8 -> new float[]{0, 0, 0, 0, 1};
            case 7 -> new float[]{2, 1, 0, 0, 5};
            case 6 -> new float[]{2, 1, 2, 0, 8};
            case 5 -> new float[]{4, 1, 2, 0, 8};
            default -> new float[]{6, 2, 2, 0, 10};
        };
    }

    @Override
    public int getId() {
        return 2;
    }

    private String getSequenceName(int seq){
        return switch (seq%10) {
            case 9 -> "Trickster";
            case 8 -> "Acrobat";
            case 7 -> "Magician";
            case 6 -> "Scribe";
            case 5 -> "Traveler";
            case 4 -> "Space_Parasite";
            default -> "";
        };
    }

}
