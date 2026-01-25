package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ReputationCriteria extends ResponseCriteria{

    private final int minimumReputation;
    private final int pathwayId;

    public ReputationCriteria(int rep, int pathwayId) {
        this.minimumReputation = rep;
        this.pathwayId = pathwayId;
    }

    @Override
    public boolean checkCondition(RitualInputData input, Level level) {
        Player player = level.getPlayerByUUID(input.caster());
        if(player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent() && player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().isPresent()){
            return player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get().getReputation(pathwayId) >= minimumReputation;
        }
        return false;
    }

    @Override
    public CompoundTag saveToNBT() {
        return new CompoundTag();
    }
}
