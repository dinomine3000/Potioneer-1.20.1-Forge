package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFactory;
import net.dinomine.potioneer.beyonder.player.BeyonderStats;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<AbilityFactory> getAbilities(int sequenceLevel) {
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
    public Map<BeyonderStats.StatType, Float> getStatsFor(int sequence) {
        return setStats(new HashMap<>(), 0, 0, 0, 5);
    }

    @Override
    public int isRitualComplete(int sequenceLevel, Player player, Level pLevel) {
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
        return Abilities.COGITATION_WOF.get();
    }
}
