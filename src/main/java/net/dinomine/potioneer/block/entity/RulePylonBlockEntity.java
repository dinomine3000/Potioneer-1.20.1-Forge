package net.dinomine.potioneer.block.entity;

import net.dinomine.potioneer.beyonder.abilities.tyrant.RulePylonAbility.*;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.savedata.DimensionChunkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.*;

public class RulePylonBlockEntity extends BlockEntity implements GeoBlockEntity {
    private Map<Rule, Punishment> rulePunishmentMap = new HashMap<>();
    private Set<Law> laws = new HashSet<>();
    private HashSet<ChunkPos> claimedChunks = new HashSet<>();
    private int sequenceLevel = 10;
    private boolean extendsAoj = true;
    private UUID ownerId = null;
    private Set<String> casterGroups = new HashSet<>();
    private boolean canSeeTheSky = true;
    private boolean working = true;

    public void setAojExtension(boolean extendsAoj){this.extendsAoj = extendsAoj;setChanged();}
    public int getSequenceLevel(){return sequenceLevel;}
    public boolean isWorking(){return working;}

    public Set<ChunkPos> getClaimedChunks(){return claimedChunks;}

    public void setPlacedByPlayer(ServerLevel level, UUID ownerId, int sequenceLevel){
        this.ownerId = ownerId;
        this.sequenceLevel = sequenceLevel;
        attemptClaimChunks(level);
    }

    public void setRules(Map<Rule, Punishment> rules){this.rulePunishmentMap = rules;}
    public void setLaws(Set<Law> laws){this.laws = laws;}

    public void invalidate(ChunkPos chunkToInvalidate){
        claimedChunks.remove(chunkToInvalidate);
        setChanged();
    }

    public RulePylonBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RULE_PYLON_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void brokeRule(Rule ruleBroken, LivingEntity ruleBreaker, ServerLevel level){
        if(!rulePunishmentMap.containsKey(ruleBroken)) return;
        rulePunishmentMap.get(ruleBroken).execution().execute(ruleBreaker, ruleBreaker.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get(), level.getEntity(ownerId));
    }

    private int tickCount = 0;
    public void tick(ServerLevel pLevel, BlockPos pPos, BlockState pState) {
        if(tickCount++ > (20*10)) gatherAndSyncData(pLevel);
        for(Law law: laws) law.execution().execute(this);
    }

    public boolean hasLaw(Law testLaw){return laws.contains(testLaw);}

    private void gatherAndSyncData(ServerLevel sLevel){
        if(ownerId == null){
            sLevel.destroyBlock(getBlockPos(), false, null, 0);
            return;
        }
        tickCount = 0;
        working = sLevel.canSeeSky(getBlockPos());
        if(ownerId != null){
            if(sLevel.getEntity(ownerId) != null){
                //sequence level
                LivingEntityBeyonderCapability cap = sLevel.getEntity(ownerId).getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
                sequenceLevel = cap.getSequenceLevel();
            }
        } else {
            sequenceLevel = 5;
        }
        attemptClaimChunks(sLevel);
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    private void attemptClaimChunks(ServerLevel level){
        claimedChunks.clear();
        setClaimTo(level, Set.of(new ChunkPos(getBlockPos())));
    }

    private void setClaimTo(ServerLevel level, Set<ChunkPos> chunksWantsToClaim){
        DimensionChunkSavedData data = DimensionChunkSavedData.from(level);
        data.removePylon(getBlockPos());
        for(ChunkPos pos: chunksWantsToClaim){
            if(data.claimChunk(level, pos, getBlockPos(), ownerId, sequenceLevel)) claimedChunks.add(pos);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    //called by above to bundle up relevant data to be present on client.
    //also called when first loading a chunk on the client.
    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("sequenceLevel", sequenceLevel);
        tag.putUUID("ownerId", ownerId);
        tag.putBoolean("extendsAoJ", extendsAoj);
        tag.putBoolean("isWorking", working);

        ListTag groupsTag = new ListTag();
        for (String group : casterGroups) {
            groupsTag.add(StringTag.valueOf(group));
        }
        tag.put("groups", groupsTag);

        ListTag rulesTag = new ListTag();
        for (Map.Entry<Rule, Punishment> entry : rulePunishmentMap.entrySet()) {
            CompoundTag pair = new CompoundTag();
            pair.putString("rule", entry.getKey().id());
            pair.putString("punishment", entry.getValue().id());
            rulesTag.add(pair);
        }
        tag.put("rules", rulesTag);

        ListTag lawsTag = new ListTag();
        for (Law law : laws) {
            lawsTag.add(StringTag.valueOf(law.id()));
        }
        tag.put("laws", lawsTag);
        return tag;
    }

    //receives the update packet for live syncing
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) handleUpdateTag(tag);
    }

    //called by above to deserialize client data
    //also called on first loading a chunk.
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.ownerId = tag.getUUID("ownerId");
        this.extendsAoj = tag.getBoolean("extendsAoJ");
        this.working = tag.getBoolean("isWorking");

        this.casterGroups.clear();
        if (tag.contains("groups", Tag.TAG_LIST)) {
            ListTag groupsTag = tag.getList("groups", Tag.TAG_STRING);
            for (int i = 0; i < groupsTag.size(); i++) {
                this.casterGroups.add(groupsTag.getString(i));
            }
        }

        this.rulePunishmentMap.clear();
        if (tag.contains("rules", Tag.TAG_LIST)) {
            ListTag rulesTag = tag.getList("rules", Tag.TAG_COMPOUND);
            for (int i = 0; i < rulesTag.size(); i++) {
                CompoundTag pair = rulesTag.getCompound(i);
                Rule rule = Rule.byId(pair.getString("rule"));
                Punishment punishment = Punishment.byId(pair.getString("punishment"));
                if (rule != null && punishment != null) {
                    this.rulePunishmentMap.put(rule, punishment);
                }
            }
        }

        this.laws.clear();
        if (tag.contains("laws", Tag.TAG_LIST)) {
            ListTag lawsTag = tag.getList("laws", Tag.TAG_STRING);
            for (int i = 0; i < lawsTag.size(); i++) {
                Law law = Law.byId(lawsTag.getString(i));
                if (law != null) {
                    this.laws.add(law);
                }
            }
        }
    }

    //these 2 are used to write and read to and from disk.
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("otherData", getUpdateTag());
        pTag.putBoolean("sky", canSeeTheSky);

        ListTag chunkList = new ListTag();
        for (ChunkPos pos : this.claimedChunks) {
            CompoundTag chunkEntry = new CompoundTag();
            chunkEntry.putInt("x", pos.x);
            chunkEntry.putInt("z", pos.z);
            chunkList.add(chunkEntry);
        }
        pTag.put("claimedChunks", chunkList);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        handleUpdateTag(pTag.getCompound("otherData"));
        this.canSeeTheSky = pTag.getBoolean("sky");

        this.claimedChunks.clear();
        if (pTag.contains("claimedChunks", Tag.TAG_LIST)) {
            ListTag chunkList = pTag.getList("claimedChunks", Tag.TAG_COMPOUND);
            for (int i = 0; i < chunkList.size(); i++) {
                CompoundTag chunkEntry = chunkList.getCompound(i);
                int x = chunkEntry.getInt("x");
                int z = chunkEntry.getInt("z");
                this.claimedChunks.add(new ChunkPos(x, z));
            }
        }
    }

    public boolean ownedBy(LivingEntity owner, boolean aojFlag) {
        boolean ownerCheck = owner.getUUID().equals(ownerId);
        boolean aojCheck = extendsAoj || !aojFlag;
        return ownerCheck && aojCheck;
    }

    public void onDestroy(ServerLevel level){
        DimensionChunkSavedData data = DimensionChunkSavedData.from(level);
        data.removePylon(getBlockPos());
    }

    public List<BlockPos> getCenters() {
        return claimedChunks.stream().map(chunk -> chunk.getMiddleBlockPosition(0)).toList();
    }

    public List<Integer> getSides() {
        return claimedChunks.stream().map(ign -> 16).toList();
    }

    AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        //state.setAnimation(RawAnimation.begin().thenLoop("idle"));
        return PlayState.STOP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RulePylonBlockEntity that = (RulePylonBlockEntity) o;
        return Objects.equals(this.worldPosition, that.worldPosition);
    }

    @Override
    public int hashCode() {
        return this.worldPosition != null ? this.worldPosition.hashCode() : 0;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
