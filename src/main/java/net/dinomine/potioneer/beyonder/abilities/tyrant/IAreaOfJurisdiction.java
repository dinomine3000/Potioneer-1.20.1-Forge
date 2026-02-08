package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.minecraft.core.BlockPos;

import java.util.List;

public interface IAreaOfJurisdiction {
    List<BlockPos> getCenters();
    List<Integer> getRadius();
}
