package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFactory;
import net.dinomine.potioneer.beyonder.player.BeyonderStats;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;


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

    public int getIconX() {
        return 128;
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
    public AbilityFactory getCogitationAbility() {
        return Abilities.COGITATION_MY.get();
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
    public List<AbilityFactory> getAbilities(int ofSequenceLevel) {
        ArrayList<AbilityFactory> abilities = new ArrayList<>();

        switch(ofSequenceLevel%10){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                abilities.add(Abilities.VOID_ABILITY.get());
                abilities.add(Abilities.FLIGHT.get());
                abilities.add(Abilities.SPIRIT_WORLD.get());
                abilities.add(Abilities.BUG.get());
            case 6:
                abilities.add(Abilities.ELYTRA.get());
                abilities.add(Abilities.RECORDING.get());
                abilities.add(Abilities.RANMA.get());
                abilities.add(Abilities.CONCEPT_THEFT.get());
            case 7:
                abilities.add(Abilities.AIR_BULLET.get());
                abilities.add(Abilities.TRICKS.get());
                abilities.add(Abilities.BLINK.get());
                abilities.add(Abilities.CLONE.get());
                abilities.add(Abilities.FAKE_DEATH.get());
                abilities.add(Abilities.MAGIC_TOOLS.get());
                abilities.add(Abilities.CLEANSE.get());
            case 8:
                abilities.add(Abilities.STEP_UP.get());
                abilities.add(Abilities.GYMNASTICS.get());
                abilities.add(Abilities.DODGE.get());
                abilities.add(Abilities.UNSEEN_HAND.get());
                abilities.add(Abilities.AERIAL_DOMAIN.get());
            case 9:
                //abilities.add(Abilities.EXTENDED_REACH.get());
                abilities.add(Abilities.DOOR_OPENING.get());
                abilities.add(Abilities.MYSTERY_JAB.get());
                abilities.add(Abilities.MYSTERY_SAP.get());
                abilities.add(Abilities.THEFT.get());
        }
        Collections.reverse(abilities);
        return abilities;
    }

    @Override
    public String getSequenceNameFromId(int sequenceLevel, boolean show) {
        return show ? getSequenceName(sequenceLevel).replace("_", " ") : getSequenceName(sequenceLevel).toLowerCase();
    }

    @Override
    public Map<BeyonderStats.StatType, Float> getStatsFor(int sequence) {
        Map<BeyonderStats.StatType, Float> stats = new EnumMap<>(BeyonderStats.StatType.class);

        switch (sequence % 10) {
            case 9 -> setStats(stats, 0, 0, 0, 6, 0.25f);
            case 8 -> setStats(stats, 2, 0, 0, 5.5f, 0.25f);
            case 7 -> setStats(stats, 4, 2, 1, 5f, 0.5f);
            case 6 -> setStats(stats, 6, 4, 2, 4.5f, 0.5f);
            case 5 -> setStats(stats, 10, 8, 8, 4f, 0.75f);
            default -> setStats(stats, 12, 12, 10, 3.5f, 1f);
        }
        return stats;
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
