package net.dinomine.potioneer.savedata;

import net.dinomine.potioneer.beyonder.abilities.tyrant.RulePylonAbility;
import net.dinomine.potioneer.block.entity.RulePylonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.*;

public class DimensionChunkSavedData extends SavedData {
    private final HashMap<ChunkPos, PylonProxy> chunkPylonMap = new HashMap<>();
    private final HashMap<UUID, Integer> playerMaxPylons = new HashMap<>();

    private int getPylonLevel(ChunkPos chunk, Level level){
        if(!chunkPylonMap.containsKey(chunk)) return 10;

        RulePylonBlockEntity be = (RulePylonBlockEntity) level.getBlockEntity(chunkPylonMap.get(chunk).pylonPos);
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

    public static void collectAojDataForOwner(MinecraftServer server, LivingEntity owner, List<BlockPos> centers, List<Integer> sideLengths, List<String> dimensions){
        UUID ownerId = owner.getUUID();
        for(ServerLevel dimensionLevel: server.getAllLevels()){
            DimensionChunkSavedData data = DimensionChunkSavedData.from(dimensionLevel);
            for(Map.Entry<ChunkPos, PylonProxy> claim: data.chunkPylonMap.entrySet()){
                PylonProxy proxy = claim.getValue();
                if (ownerId.equals(proxy.ownerId) && proxy.extendsAoj) {
                    centers.add(claim.getKey().getMiddleBlockPosition(0));
                    sideLengths.add(16);
                    dimensions.add(dimensionLevel.dimension().location().toString());
                }
            }
        }
    }
    public static Set<BlockPos> getAllPylonPositionsOwnedBy(MinecraftServer server, LivingEntity owner){
        UUID ownerId = owner.getUUID();
        Set<BlockPos> res = new HashSet<>();
        for(ServerLevel dimensionLevel: server.getAllLevels()){
            DimensionChunkSavedData data = DimensionChunkSavedData.from(dimensionLevel);
            for(Map.Entry<ChunkPos, PylonProxy> claim: data.chunkPylonMap.entrySet()){
                PylonProxy proxy = claim.getValue();
                if (ownerId.equals(proxy.ownerId)) {
                    res.add(proxy.pylonPos);
                }
            }
        }
        return res;
    }
    public static @Nullable RulePylonBlockEntity getRulingPylon(ServerLevel dimensionLevel, BlockPos testPosition){
        ChunkPos pos = new ChunkPos(testPosition);
        DimensionChunkSavedData data = DimensionChunkSavedData.from(dimensionLevel);
        if(!data.chunkPylonMap.containsKey(pos)) return null;
        return (RulePylonBlockEntity) dimensionLevel.getBlockEntity(data.chunkPylonMap.get(pos).pylonPos);
    }

    public static Set<BlockPos> getPylonPositionsInDimensionOwnedBy(ServerLevel dimensionLevel, LivingEntity owner){
        Set<BlockPos> res = new HashSet<>();
        UUID ownerId = owner.getUUID();
        DimensionChunkSavedData data = DimensionChunkSavedData.from(dimensionLevel);
        for(Map.Entry<ChunkPos, PylonProxy> claim: data.chunkPylonMap.entrySet()){
            PylonProxy proxy = claim.getValue();
            if (ownerId.equals(proxy.ownerId)) {
                res.add(proxy.pylonPos);
            }
        }
        return res;
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

    public void setAojStatus(BlockPos pylonPos, boolean aojStatus){
        List<ChunkPos> toRemove = new ArrayList<>();
        for(Map.Entry<ChunkPos, PylonProxy> claim: chunkPylonMap.entrySet()){
            if(pylonPos.equals(claim.getValue().pylonPos)) claim.getValue().setExtendsAoj(aojStatus);
        }
        setDirty();
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

    //returns the number of pylons that owner owns at the given sequence level or lower (between the given value and 9)
    private List<PylonProxy> getPylonList(UUID ownerId, int sequenceLevelToTest){
        return chunkPylonMap.values().stream().filter(pylon -> pylon.ownerId.equals(ownerId) && pylon.sequenceLevel>=sequenceLevelToTest).toList();
    }

    //if the owner has more pylons than is allowed by the given sequence level, clear out the extra ones, starting with the lowest level ones (level9)
    private void unclaimExtraChunks(UUID ownerId, int sequenceLevelToClear){
        List<PylonProxy> relevantPylons = new ArrayList<>(getPylonList(ownerId, sequenceLevelToClear));
        int count = relevantPylons.size();
        int max = RulePylonAbility.getMaxPylons(sequenceLevelToClear);
        if(count <= max) return;
        relevantPylons.sort(Comparator.comparingInt(p -> p.sequenceLevel));
        Collections.reverse(relevantPylons);
        for(int i = 0; i < count - max; i++){
            removePylon(relevantPylons.get(i).pylonPos);
        }
    }

    public boolean claimChunk(ServerLevel level, ChunkPos chunk, BlockPos blockPos, UUID ownerId, int sequenceLevel){
        int lvl = getPylonLevel(chunk, level);
        if(lvl <= sequenceLevel) return false;
        if(getPylonList(ownerId, sequenceLevel).size() >= RulePylonAbility.getMaxPylons(sequenceLevel)) return false;
        unclaimExtraChunks(ownerId, sequenceLevel);
        if(chunkPylonMap.containsKey(chunk)){
            RulePylonBlockEntity be = getBlockEntityOfChunk(level, chunk, true);
            be.invalidate(chunk);
        }
        chunkPylonMap.put(chunk, new PylonProxy(blockPos, ownerId, sequenceLevel));
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
            entry.putBoolean("aoj", blockPos.extendsAoj);
            entry.putInt("level", blockPos.sequenceLevel);
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
            boolean aoj = entry.getBoolean("aoj");
            int level = entry.getInt("level");
            data.chunkPylonMap.put(chunkPos, new PylonProxy(blockPos, ownerId, level, aoj));
        }
        return data;
    }

    private DimensionChunkSavedData(){
    }

    public static DimensionChunkSavedData from(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(DimensionChunkSavedData::loadPylons,
                DimensionChunkSavedData::new, "potioneer_chunk_data");
    }

    public void updateAoj(BlockPos pylonPos, boolean newAoj){
        for(PylonProxy proxy: chunkPylonMap.values())
            if(proxy.pylonPos.equals(pylonPos)) proxy.extendsAoj = newAoj;
    }

    private static class PylonProxy{
        public BlockPos pylonPos;
        public UUID ownerId;
        public boolean extendsAoj = true;
        public int sequenceLevel;
        public PylonProxy(BlockPos pylonPos, UUID ownerId, int sequenceLevel){
            this(pylonPos, ownerId, sequenceLevel, true);
        }
        public PylonProxy(BlockPos pylonPos, UUID ownerId, int sequenceLevel, boolean aoj){
            this.pylonPos = pylonPos;
            this.ownerId = ownerId;
            this.sequenceLevel = sequenceLevel;
            this.extendsAoj = aoj;
        }

        public void setSequenceLevel(int sequenceLevel) {
            this.sequenceLevel = sequenceLevel;
        }

        public void setExtendsAoj(boolean extendsAoj){this.extendsAoj = extendsAoj;}
    }

}
