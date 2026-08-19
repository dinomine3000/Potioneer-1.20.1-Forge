package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFactory;
import net.dinomine.potioneer.beyonder.player.BeyonderStats;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

public class ParagonPathway extends BeyonderPathway {

    public ParagonPathway(){
        super("Paragon", 0x908020, new int[]{3500, 2744, 1960, 1400, 1000, 700, 500, 350, 250, 100});
    }

    @Override
    public int getX(){
        return 0;
    }

    @Override
    public int getY(){
        return 64;
    }

    @Override
    public int getAbilityX(){
        return 109;
    }

    public int getIconY() {
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
    public Map<BeyonderStats.StatType, Float> getStatsFor(int sequence) {
        Map<BeyonderStats.StatType, Float> stats = new EnumMap<>(BeyonderStats.StatType.class);

        switch (sequence % 10) {
            case 9 -> setStats(stats, 0, 0, 0, 5f);
            case 8 -> setStats(stats, 2, 0, 0, 4.5f);
            case 7 -> setStats(stats, 6, 1, 2, 4f);
            case 6 -> setStats(stats, 8, 2, 2, 3.5f);
            case 5 -> setStats(stats, 10, 4, 7, 3f);
            default -> setStats(stats, 15, 6, 9, 2.5f);
        }
        return stats;
    }

    /*@Override
    public float[] getStatsFor(int sequence){
        return switch (sequence%10){
            case 9 -> new float[]{0, 0, 0, 0};
            case 8 -> new float[]{0, 0, 0, 1};
            case 7 -> new float[]{1, 0, 1, 5};
            case 6 -> new float[]{2, 0, 2, 5};
            case 5 -> new float[]{2, 1, 2, 5};
            default -> new float[]{4, 2, 0, 10};
        };
    }*/

    @Override
    public List<AbilityFactory> getAbilities(int ofSequenceLevel) {
        ArrayList<AbilityFactory> abilities = new ArrayList<>();

        /*switch(ofSequenceLevel%10){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                abilities.add(Abilities.XP_COST_REDUCE);
                abilities.add(Abilities.REMOVE_ENCHANTMENT.create(atSequenceLevel));
            case 8:
                abilities.add(Abilities.ANVIL_GUI.create(atSequenceLevel));
                abilities.add(Abilities.CONJURER_CONTAINER.create(atSequenceLevel));
                abilities.add(Abilities.CRAFTER_BONE_MEAL.create(atSequenceLevel));
                abilities.add(Abilities.ENDER_CHEST.create(atSequenceLevel));
            case 9:
                abilities.add(Abilities.CRAFTING_SPIRITUALITY.create(atSequenceLevel));
                abilities.add(Abilities.CRAFTING_GUI.create(atSequenceLevel));
                abilities.add(Abilities.FUEL_CREATE.create(atSequenceLevel));
                abilities.add(Abilities.DURABILITY_REGEN.create(atSequenceLevel));
        }
        Collections.reverse(abilities);*/
        return abilities;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public String getSequenceNameFromId(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    private String getSequenceName(int seq){
        return switch (seq%10) {
            case 9 -> "Crafter";
            case 8 -> "Conjurer";
            case 7 -> "Enchanter";
            case 6 -> "Artisan";
            case 5 -> "Alchemist";
            default -> "";
        };
    }

    @Override
    public int getSequenceColorFromLevel(int seq){
        return switch (seq%10) {
            case 9 -> 16770989;
            case 8 -> 28791;
            case 7 -> 10107903;
            default -> 0;
        };
    }

    @Override
    public AbilityFactory getCogitationAbility() {
        return Abilities.COGITATION_PA.get();
    }
}
