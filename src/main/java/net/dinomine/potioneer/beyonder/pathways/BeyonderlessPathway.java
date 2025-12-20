package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;

import java.util.List;

public class BeyonderlessPathway extends BeyonderPathway{

    public BeyonderlessPathway(){
        super("Beyonderless", 0x404040, new int[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100});
    }

    @Override
    public int getX() {
        return 64;
    }

    @Override
    public int getY() {
        return 64;
    }

    @Override
    public int getAbilityX() {
        return -1;
    }

    @Override
    public List<Ability> getAbilities(int sequenceLevel) {
        return List.of();
    }

    @Override
    public int getSequenceColorFromLevel(int sequenceLevel) {
        return 16742143;
    }

    @Override
    public String getSequenceNameFromId(int seq, boolean show) {
        return show ? "None" : "none";
    }

    @Override
    public float[] getStatsFor(int sequence) {
        return new float[]{0, 0, 0, 0, 0};
    }
}
