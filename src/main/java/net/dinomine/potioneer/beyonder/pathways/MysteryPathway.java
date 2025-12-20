package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.mystery.*;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;
import java.util.List;

public class MysteryPathway extends BeyonderPathway {

    public static final int MAX_SAP_DISTANCE = 4;

    public MysteryPathway(int sequence){
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
        return switch (sequenceLevel) {
            case 9 -> 12117700;
            case 8 -> 65294;
            case 7 -> 16121785;
            default -> 0;
        };
    }

    @Override
    public List<Ability> getAbilities(int sequenceLevel) {
        ArrayList<Ability> activeAbilities = new ArrayList<>();

        switch(sequenceLevel){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                activeAbilities.add(new AirBulletAbility(sequenceLevel));
                activeAbilities.add(new FigurineSubstituteAbility(sequenceLevel));
                activeAbilities.add(new PlagueAbility(sequenceLevel));
                activeAbilities.add(new PanaceaAbility(sequenceLevel));
                activeAbilities.add(new PushAbility(sequenceLevel));
            case 8:
                activeAbilities.add(new ReduceFallDamageAbility(sequenceLevel));
                activeAbilities.add(new StepUpAbility(sequenceLevel));
                activeAbilities.add(new LeapAbility(sequenceLevel));
            case 9:
                activeAbilities.add(new ReachAbility(sequenceLevel));
                activeAbilities.add(new DoorOpeningAbility(sequenceLevel));
                activeAbilities.add(new SpiritualityRegenAbility(sequenceLevel));
                activeAbilities.add(new InvisibilityAbility(sequenceLevel));
                activeAbilities.add(new CogitationAbility(20 + sequenceLevel));
        }
        return activeAbilities;
    }

    @Override
    public String getSequenceNameFromId(int sequenceLevel, boolean show) {
        return show ? getSequenceName(sequenceLevel).replace("_", " ") : getSequenceName(sequenceLevel).toLowerCase();
    }

    @Override
    public float[] getStatsFor(int sequence){
        return switch (sequence){
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

    public String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Trickster";
            case 8 -> "Acrobat";
            case 7 -> "Voodoo_Assassin";
            case 6 -> "Parasite";
            case 5 -> "Traveler";
            default -> "";
        };
    }

}
