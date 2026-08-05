package net.dinomine.potioneer.savedata;

import net.dinomine.potioneer.block.entity.RulePylonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class DimensionChunkSavedData extends SavedData {
    private final HashMap<ChunkPos, PylonProxy> chunkPylonMap = new HashMap<>();

    private int getPylonLevel(ChunkPos chunk, Level level){
        if(!chunkPylonMap.containsKey(chunk)) return 10;

        RulePylonBlockEntity be = (RulePylonBlockEntity) level.getBlockEntity(chunkPylonMap.get(chunk).pylonPos());
        if(be == null){
            removePylon(chunk);
            return 10;
        }
        return be.getSequenceLevel();
    }

    public void removePylon(ChunkPos pos){
        chunkPylonMap.remove(pos);
        setDirty();
    }
    public static List<RulePylonBlockEntity> getBlockEntitiesOwnedBy(ServerLevel dimensionLevel, LivingEntity owner, boolean forceLoad, boolean showAoj){
        DimensionChunkSavedData data = DimensionChunkSavedData.from(dimensionLevel);
        List<RulePylonBlockEntity> beList = new ArrayList<>();
        for(Map.Entry<ChunkPos, PylonProxy> claim: data.chunkPylonMap.entrySet()){
            RulePylonBlockEntity be = data.getBlockEntityOfChunk(dimensionLevel, claim.getValue().pylonPos, forceLoad);
            if(be != null && be.ownedBy(owner, showAoj)) beList.add(be);
        }
        return beList;
    }
    public RulePylonBlockEntity getBlockEntityOfChunk(ServerLevel dimensionLevel, BlockPos pylonPos, boolean forceLoad){
        boolean isLoaded = dimensionLevel.isLoaded(pylonPos);
        if (!isLoaded) {
            if (forceLoad) dimensionLevel.getChunk(pylonPos);
            else return null;
        }
        if (dimensionLevel.getBlockEntity(pylonPos) instanceof RulePylonBlockEntity be) return be;
        return null;
    }
    public RulePylonBlockEntity getBlockEntityOfChunk(ServerLevel dimensionLevel, ChunkPos pos, boolean forceLoad){
        if (dimensionLevel == null || pos == null) return null;
        var pylonEntry = chunkPylonMap.get(pos);
        if (pylonEntry == null || pylonEntry.pylonPos == null) return null;
        BlockPos pylonPos = pylonEntry.pylonPos;

        boolean isLoaded = dimensionLevel.isLoaded(pylonPos);
        if (!isLoaded) {
            if (forceLoad) dimensionLevel.getChunk(pylonPos);
            else return null;
        }

        if (dimensionLevel.getBlockEntity(pylonPos) instanceof RulePylonBlockEntity be) {
            return be;
        }

        removePylon(pos);
        return null;
    }

    public void removePylon(BlockPos pylonPos){
        List<ChunkPos> toRemove = new ArrayList<>();
        for(Map.Entry<ChunkPos, PylonProxy> claim: chunkPylonMap.entrySet()){
            if(pylonPos.equals(claim.getValue().pylonPos)) toRemove.add(claim.getKey());
        }
        for(ChunkPos pos: toRemove) chunkPylonMap.remove(pos);
        setDirty();
    }

    public int getChunkClaimLevel(Level level, ChunkPos pos){
        return chunkPylonMap.containsKey(pos) ? getPylonLevel(pos, level) : 10;
    }

    public boolean canClaimChunk(ServerLevel level, ChunkPos chunk, int sequenceLevel){
        int lvl = getPylonLevel(chunk, level);
        if(lvl <= sequenceLevel) return false;
        return true;
    }

    public boolean claimChunk(ServerLevel level, ChunkPos chunk, BlockPos blockPos, UUID ownerId, int sequenceLevel){
        int lvl = getPylonLevel(chunk, level);
        if(lvl <= sequenceLevel) return false;
        if(chunkPylonMap.containsKey(chunk)){
            RulePylonBlockEntity be = getBlockEntityOfChunk(level, chunk, true);
            be.invalidate(chunk);
        }
        chunkPylonMap.put(chunk, new PylonProxy(blockPos, ownerId));
        setDirty();
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag list = new ListTag();
        chunkPylonMap.forEach((chunkPos, blockPos) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("cx", chunkPos.x);
            entry.putInt("cz", chunkPos.z);
            entry.putInt("px", blockPos.pylonPos.getX());
            entry.putInt("py", blockPos.pylonPos.getY());
            entry.putInt("pz", blockPos.pylonPos.getZ());
            entry.putUUID("id", blockPos.ownerId);
            list.add(entry);
        });
        compoundTag.put("pylons", list);
        return compoundTag;
    }

    public static DimensionChunkSavedData loadPylons(CompoundTag nbt){
        DimensionChunkSavedData data = new DimensionChunkSavedData();
        ListTag list = nbt.getList("pylons", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ChunkPos chunkPos = new ChunkPos(entry.getInt("cx"), entry.getInt("cz"));
            BlockPos blockPos = new BlockPos(entry.getInt("px"), entry.getInt("py"), entry.getInt("pz"));
            UUID ownerId = entry.getUUID("id");
            data.chunkPylonMap.put(chunkPos, new PylonProxy(blockPos, ownerId));
        }
        return data;
    }

    private DimensionChunkSavedData(){
    }

    public static DimensionChunkSavedData from(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(DimensionChunkSavedData::loadPylons,
                DimensionChunkSavedData::new, "potioneer_chunk_data");
    }

    private record PylonProxy(BlockPos pylonPos, UUID ownerId){}

}
