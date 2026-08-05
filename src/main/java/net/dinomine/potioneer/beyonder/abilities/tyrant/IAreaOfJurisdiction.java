package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.List;

public interface IAreaOfJurisdiction {
    List<BlockPos> getCenters(String dimensionLocation);
    List<Integer> getSides(String dimensionLocation);

    default List<BlockPos> getCentersFromTag(CompoundTag tag) {
        List<BlockPos> centers = new ArrayList<>();
        if (tag.contains("centers", Tag.TAG_LIST)) {
            ListTag centersTag = tag.getList("centers", Tag.TAG_COMPOUND);
            for (int i = 0; i < centersTag.size(); i++) {
                centers.add(NbtUtils.readBlockPos(centersTag.getCompound(i)));
            }
        }
        return centers;
    }

    default List<Integer> getSideFromTag(CompoundTag tag) {
        List<Integer> radii = new ArrayList<>();
        if (tag.contains("sides", Tag.TAG_LIST)) {
            ListTag radiiTag = tag.getList("sides", Tag.TAG_INT);
            for (int i = 0; i < radiiTag.size(); i++) {
                radii.add(radiiTag.getInt(i));
            }
        }
        return radii;
    }
    default CompoundTag getCompoundTag(List<BlockPos> centers, List<Integer> radii){
        return getCompoundTag(new CompoundTag(), centers, radii);
    }

    default CompoundTag getCompoundTag(CompoundTag resTag, List<BlockPos> centers, List<Integer> radii){
        ListTag centersTag = new ListTag();
        for (BlockPos pos : centers) {
            centersTag.add(NbtUtils.writeBlockPos(pos));
        }
        resTag.put("centers", centersTag);

        ListTag radiiTag = new ListTag();
        for (int r : radii) {
            radiiTag.add(IntTag.valueOf(r));
        }
        resTag.put("sides", radiiTag);
        return resTag;
    }
}
