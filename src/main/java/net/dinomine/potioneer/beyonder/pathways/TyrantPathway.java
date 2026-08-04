package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStats;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.dinomine.potioneer.rituals.spirits.defaultGods.TyrantResponse;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

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
        if(sequenceLevel > 5) return Component.empty();
        return switch (sequenceLevel){
            case 5 -> Component.translatable("ritual.potioneer.source_of_misfortune");
            default -> Component.translatable("ritual.potioneer.source_of_misfortune");
        };
    }


    /*@Override
    public float[] getStatsFor(int sequenceLevel){
        return switch (sequenceLevel%10){
            case 9 -> new float[]{8, 0, 0, 1};
            case 8 -> new float[]{8, 0, 0, 2};
            case 7 -> new float[]{12, 2, 0, 5};
            case 6 -> new float[]{15, 2, 0, 5};
            case 5 -> new float[]{20, 3, 0, 7};
            default -> new float[]{0, 0, 0, 0};
        };
    }*/

    @Override
    public Map<BeyonderStats.StatType, Float> getStatsFor(int sequence) {
        Map<BeyonderStats.StatType, Float> stats = new EnumMap<>(BeyonderStats.StatType.class);

        switch (sequence % 10) {
            case 9 -> setStats(stats, 4, 1, 1, 4f);
            case 8 -> setStats(stats, 6, 1, 1, 3.5f);
            case 7 -> setStats(stats, 16, 4, 3, 3f);
            case 6 -> setStats(stats, 22, 6, 4, 2.5f);
            case 5 -> setStats(stats, 28, 10, 9, 2f);
            default -> setStats(stats, 35, 13, 11, 1.5f);
        }
        return stats;
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
                abilities.add(Abilities.AMPLIFICATION.create(atSequenceLevel));
                abilities.add(Abilities.EXILE.create(atSequenceLevel));
                abilities.add(Abilities.CONTRACT.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_CALAMITY.create(atSequenceLevel));
                abilities.add(Abilities.BERSERK_RAGE.create(atSequenceLevel));
                abilities.add(Abilities.ANCHOR_BLINKING.create(atSequenceLevel));
            case 7:
                abilities.add(Abilities.AOJ.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_AURA.create(atSequenceLevel));
                abilities.add(Abilities.ARREST.create(atSequenceLevel));
                abilities.add(Abilities.MIST.create(atSequenceLevel));
                abilities.add(Abilities.MIST_BLINKING.create(atSequenceLevel));
                abilities.add(Abilities.SENSE_OF_ORDER.create(atSequenceLevel));
            case 8:
                abilities.add(Abilities.TYRANT_DIVINATION.create(atSequenceLevel));
                abilities.add(Abilities.TYRANT_WATER_SPELLS.create(atSequenceLevel));
            case 9:
                abilities.add(Abilities.WATER_AFFINITY.create(atSequenceLevel));
                abilities.add(Abilities.WATER_SCALES.create(atSequenceLevel));
                abilities.add(Abilities.OCEAN_ORDER.create(atSequenceLevel));
        }
        Collections.reverse(abilities);
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
            default -> "";
        };
    }

    @Override
    public int getSequenceColorFromLevel(int seq){
        return switch (seq%10) {
            case 9 -> 0x8AF6FF;
            case 8 -> 0x82A1FF;
            case 7 -> 0x4814FC;
            case 6 -> 0x910DD4;
            case 5 -> 0xA6AD05;
            default -> 0;
        };
    }

    @Override
    public List<String> canCraftEffectCharms(int sequenceLevel) {
        List<String> res = new ArrayList<>();
        switch(sequenceLevel){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                res.addAll(List.of(BeyonderEffects.TYRANT_WATER_AFFINITY.getEffectId(), BeyonderEffects.TYRANT_WATER_PRISON.getEffectId()));
        }
        return res;
    }
}
