package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.dinomine.potioneer.rituals.spirits.defaultGods.TyrantResponse;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class TyrantPathway extends BeyonderPathway {

    public TyrantPathway(){
        super("Tyrant", 0x404080, new int[]{3400, 2500, 1800, 1300, 1000, 700, 425, 300, 140, 100});
    }

    @Override
    public Deity getDefaultDeity() {
        return new TyrantResponse();
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

    public int getIconX() {
        return 64;
    }

    @Override
    public int isRitualComplete(int sequenceLevel, Player player, Level pLevel) {
        if(sequenceLevel > 5) return 0;
        return 0;
    }

    @Override
    public void applyRitualEffects(Player player, int sequenceLevel) {}

    @Override
    public Component getRitualDescriptionForSequence(int sequenceLevel) {
        return Component.empty();
    }

    @Override
    public float[] getStatsFor(int sequenceLevel){
        return switch (sequenceLevel%10){
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
                abilities.add(Abilities.TYRANT_LIGHTNING_STRIKE.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_THUNDER.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_RAIN.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_LEAP.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_ELECTRIFICATION.create(atSequenceLevel));
            case 8:
                abilities.add(Abilities.TYRANT_WATER_PRISON.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_CREATE_WATER.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_REMOVE_WATER.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_WATER_TRAP.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_DIVINATION.create(atSequenceLevel));
            case 9:
                abilities.add(Abilities.WATER_AFFINITY.create(atSequenceLevel));
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
        return switch (seq%10) {
            case 9 -> "Swimmer";
            case 8 -> "Water_Mage";
            case 7 -> "Hydroborn_Enforcer";
            case 6 -> "Chaotic_Magistrate";
            case 5 -> "Tribunal";
            case 4 -> "Punisher";
            default -> "";
        };
    }

    @Override
    public int getSequenceColorFromLevel(int seq){
        return switch (seq%10) {
            case 9 -> 2146549;
            case 8 -> 8023295;
            case 7 -> 8167853;
            default -> 0;
        };
    }
}
