package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.*;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class TyrantPathway extends BeyonderPathway {

    public TyrantPathway(){
        super("Tyrant", 0x404080, new int[]{3400, 2500, 1800, 1300, 1000, 700, 425, 300, 140, 100});
    }

    @Override
    public int getX(){
        return 64;
    }

    @Override
    public int getY(){
        return 0;
    }

    @Override
    public int getAbilityX() {
        return 31;
    }

    @Override
    public float[] getStatsFor(int sequenceLevel){
        return switch (sequenceLevel){
            case 9 -> new float[]{8, 1, 0, 0, 1};
            case 8 -> new float[]{8, 1, 0, 0, 2};
            case 7 -> new float[]{12, 2, 0, 0, 5};
            case 6 -> new float[]{15, 2, 0, 0, 5};
            case 5 -> new float[]{20, 3, 0, 0, 7};
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
                abilities.add(new ThunderStrikeAbility(sequence));
                abilities.add(new ThunderCreateAbility(sequence));
                abilities.add(new RainCreateAbility(sequence));
                abilities.add(new RainLeapAbility(sequence));
                abilities.add(new ElectrificationAbility(sequence));
            case 8:
                abilities.add(new WaterPrisonAbility(sequence));
                abilities.add(new WaterCreateAbility(sequence));
                abilities.add(new WaterRemoveAbility(sequence));
                abilities.add(new WaterTrapAbility(sequence));
                abilities.add(new DivinationAbility(sequence));
            case 9:
                abilities.add(new WaterAffinityAbility(sequence));
                abilities.add(new CogitationAbility(10 + sequence));
        }

        return abilities;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public String getSequenceNameFromId(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    private String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Swimmer";
            case 8 -> "Hydro_Shaman";
            case 7 -> "Tempest";
            case 6 -> "Waterborn";
            case 5 -> "Punisher";
            default -> "";
        };
    }

    public int getSequenceColorFromLevel(int seq){
        return switch (seq) {
            case 9 -> 2146549;
            case 8 -> 8023295;
            case 7 -> 8167853;
            default -> 0;
        };
    }
}
