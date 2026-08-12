package net.dinomine.potioneer.block.entity;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.RulePylonAbility.*;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.TyrantPathway;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.RulePylonMessage;
import net.dinomine.potioneer.savedata.DimensionChunkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
    private boolean canSeeTheSky = true;
    private boolean working = true;

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                this.worldPosition.getX(),
                this.worldPosition.getY(),
                this.worldPosition.getZ(),
                this.worldPosition.getX() + 1,
                1024, // Cover up to MAX_RENDER_Y
                this.worldPosition.getZ() + 1
        );
    }

    public void setAojExtension(boolean extendsAoj){
        this.extendsAoj = extendsAoj;
        DimensionChunkSavedData.from((ServerLevel) level).updateAoj(getBlockPos(), extendsAoj);
        setChanged();
    }
    public int getSequenceLevel(){return sequenceLevel;}
    public boolean isWorking(){return working;}

    public Set<ChunkPos> getClaimedChunks(){return claimedChunks;}

    public void setPlacedByPlayer(ServerLevel level, UUID ownerId, int sequenceLevel){
        this.ownerId = ownerId;
        this.sequenceLevel = sequenceLevel;
        attemptClaimChunks(level, true, true);
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

    public void brokeRule(Rule ruleBroken, LivingEntity ruleBreaker){
        if(ruleBreaker.level().isClientSide()) return;
        brokeRule(ruleBroken, ruleBreaker, (ServerLevel) ruleBreaker.level());
    }
    public void brokeRule(Rule ruleBroken, LivingEntity ruleBreaker, ServerLevel level){
        if(!rulePunishmentMap.containsKey(ruleBroken)) return;
        rulePunishmentMap.get(ruleBroken).execution().execute(ruleBreaker, ruleBreaker.getCapability(CapProvider.BEYONDER_STATS).resolve().get(), AbilityFunctionHelper.getEntityAcrossDimensions(level, ownerId), sequenceLevel);
        if(ruleBreaker instanceof Player player)
            player.displayClientMessage(Component.translatable("message.potioneer.rule_broken", ruleBroken.title()), true);
        LivingEntity owner = getOwner();
        if(owner == null) return;
        CapProvider.beyonder(owner).ifPresent(cap -> cap.getCharacteristicManager().progressActing(TyrantPathway.TRIBUNAL_ACTING_PYLON_RULE, 15));
    }

    public int tickCount = 0;
    public void tick(ServerLevel pLevel, BlockPos pPos, BlockState pState) {
        if(tickCount++ >= (20*5)) gatherAndSyncData(pLevel);
        if(!working) return;
        for(Law law: laws) {
            law.execution().execute(this);
            LivingEntity owner = getOwner();
            if(owner != null) CapProvider.beyonder(owner).ifPresent(cap -> cap.getCharacteristicManager().progressActing(TyrantPathway.TRIBUNAL_ACTING_PYLON_LAW, 15));
        }
        //add an effect to help keep track of rule breaking.
        AbilityFunctionHelper.getLivingEntitiesAround(getBlockPos(), level, 9).forEach(ent -> {
            ent.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.TYRANT_MAIN_HAND_RULE.createInstance(0, 20, false), cap, ent));
        });
    }

    public boolean hasLaw(Law testLaw){return laws.contains(testLaw);}

    private void gatherAndSyncData(ServerLevel sLevel){
        if(ownerId == null){
            sLevel.destroyBlock(getBlockPos(), false, null, 0);
            return;
        }
        tickCount = 0;
        //sequence level
        Entity ent = AbilityFunctionHelper.getEntityAcrossDimensions(sLevel, ownerId);
        if(ent instanceof LivingEntity livingEntity){
            BeyonderCapability cap = livingEntity.getCapability(CapProvider.BEYONDER_STATS).resolve().get();
            sequenceLevel = cap.getAbilitiesManager().getSequenceLevelOfAbility(Abilities.RULE_PYLON.getAblId());
            if(sequenceLevel < 0 || sequenceLevel == 10) sequenceLevel = 9;

        }
        boolean workingO = working;
        boolean shouldWork = sLevel.canSeeSky(getBlockPos());
        attemptClaimChunks(sLevel, workingO, shouldWork);
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    private void setExtendsAoj(ServerLevel serverLevel, boolean extendsAoj){
        this.extendsAoj = extendsAoj;
        DimensionChunkSavedData.from(serverLevel).setAojStatus(getBlockPos(), extendsAoj);
    }

    private void attemptClaimChunks(ServerLevel level, boolean workingBefore, boolean workingNow){
        claimedChunks.clear();
        ChunkPos centerChunk = new ChunkPos(getBlockPos());
        Set<ChunkPos> surroundingChunks = new HashSet<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                surroundingChunks.add(new ChunkPos(centerChunk.x + dx, centerChunk.z + dz));
            }
        }
        if (workingBefore && workingNow) {
            setClaimTo(level, surroundingChunks);
        }
        else if(workingBefore) {
            DimensionChunkSavedData.from(level).removePylon(getBlockPos());
            working = false;
        }
        else if(workingNow){
            if(setClaimTo(level, Set.of(new ChunkPos(getBlockPos())))) working = true;
        }
    }

    private boolean setClaimTo(ServerLevel level, Set<ChunkPos> chunksWantsToClaim){
        DimensionChunkSavedData data = DimensionChunkSavedData.from(level);
        data.removePylon(getBlockPos());
        boolean flag = false;
        for(ChunkPos pos: chunksWantsToClaim){
            if(data.claimChunk(level, pos, getBlockPos(), ownerId, sequenceLevel)){
                claimedChunks.add(new ChunkPos(pos.x, pos.z));
                flag = true;
            }
        }
        if(flag) data.updateAoj(getBlockPos(), extendsAoj);
        return flag;
    }

    public void openScreen(ServerPlayer pPlayer) {
        PacketHandler.sendMessageSTC(new RulePylonMessage(
                rulePunishmentMap,
                new ArrayList<>(laws),
                getBlockPos(),
                extendsAoj
        ), pPlayer);
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

    public List<LivingEntity> getEntities() {
        assert level != null;
        return AbilityFunctionHelper.getLivingEntitiesInChunk(level, new ChunkPos(getBlockPos()));
    }

    AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        //state.setAnimation(RawAnimation.begin().thenLoop("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public boolean isOwner(Player pPlayer) {
        return pPlayer.getUUID().equals(ownerId);
    }

    public @Nullable LivingEntity getOwner() {
        return (LivingEntity) AbilityFunctionHelper.getEntityAcrossDimensions((ServerLevel) level, ownerId);
    }
}
