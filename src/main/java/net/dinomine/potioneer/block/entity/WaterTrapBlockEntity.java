package net.dinomine.potioneer.block.entity;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.ParticleMaker;
import net.dinomine.potioneer.util.misc.ModCompoundTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WaterTrapBlockEntity extends BlockEntity implements GeoBlockEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int effectIndex = 0;
    private int sequenceLevel = 8;
    private boolean isInAOJ = false;
    private List<String> casterAllyGroups = new ArrayList<>();
    private int numberOfChainsBelow = 0;
    private UUID id = null;
    private boolean diffused = false;
    private boolean exploded = false;

    public List<String> getCasterAllyGroups(){return casterAllyGroups;}
    public boolean isInAOJ() {return isInAOJ;}
    public int getChains(){return numberOfChainsBelow;}

    public WaterTrapBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WATER_TRAP_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void setPlacedByPlayer(UUID uuid, int sequenceLevel){
        this.id = uuid;
        this.sequenceLevel = sequenceLevel;
        if (level != null && !level.isClientSide) {
            gatherAndSyncData();
            setChanged();
        }
    }

    public boolean isOwner(UUID uId){
        return id != null && uId != null && uId.compareTo(id) == 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        state.setAnimation(RawAnimation.begin().thenLoop("idle"));
        return PlayState.CONTINUE;
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private int tickCount = 0;
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(tickCount++ % 5 == 0) return;
        tryToExplode(true);
        if(tickCount%100 == 0){
            gatherAndSyncData();
            tickCount = 0;
        }
    }

    private boolean isEntityAllyOfOwner(LivingEntity ent){
        if(!(level instanceof ServerLevel serverLevel) || id == null) return false;
        if(ent instanceof Player player){
            AllySystemSaveData data = AllySystemSaveData.from(serverLevel);
            return data.isPlayerAllyOf(player.getUUID(), id);
        } return false;
    }

    private void tryToExplode(boolean destroy){
        if(!(level instanceof ServerLevel sLevel)) return;
        if(exploded) return;
        ArrayList<LivingEntity> entities = AbilityFunctionHelper.getLivingEntitiesAround(getBlockPos(), level, 2, ent -> !isEntityAllyOfOwner(ent));
        if(!entities.isEmpty()) {
            assert level != null;
            for(LivingEntity ent: entities){
                setChanged();
                applyEffectsToEntity(sLevel, getBlockPos(), ent);
            }
            if(id != null){
                Entity caster = sLevel.getEntity(id);
                if(caster != null){
                    LivingEntityBeyonderCapability cap = caster.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
                    if(cap.getAbilitiesManager().hasAbilityOrBetter(Abilities.TYRANT_WATER_TRAP.getAblId(), 7)){
                        caster.sendSystemMessage(Component.translatable("message.potioneer.water_trap_activated"));
                    }
                }
            }
            exploded = true;
            if(destroy) level.destroyBlock(getBlockPos(), false);
        }
    }

    private void applyEffectsToEntity(ServerLevel level, BlockPos pos, LivingEntity entity){
        switch (effectIndex){
            case 0:
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30*20, 10 - sequenceLevel));
                break;
            case 1:
                entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.TYRANT_WATER_PRISON.createInstance(sequenceLevel, 0, 20*30, true), cap, entity);
                });
                break;
            case 2:
                if(level.isEmptyBlock(pos.below().below())
                     && level.isEmptyBlock(pos.below().below().below()))
                {
                    BlockPos target = pos.below().below().below();
                    entity.teleportTo(target.getX(), target.getY(), target.getZ());
                    break;
                }
            default:
                Player player = id == null ? null: level.getPlayerByUUID(id);
                entity.invulnerableTime = 0;
                entity.hurt(PotioneerDamage.water_trap(level, player), -1 + (10 - sequenceLevel)*3);
                break;

        }
    }

    public void incrementIndex(Player player){
        if(!isOwner(player.getUUID())) return;
        setChanged();
        effectIndex = Math.floorMod((effectIndex + (player.isCrouching() ? -1 : 1)), 4);
        player.sendSystemMessage(Component.translatable("pathway.potioneer.trap_effect_" + effectIndex));
    }

    public void gatherAndSyncData(){
        if(level != null && level.isClientSide) return;
        if(!(level instanceof ServerLevel sLevel)) return;
        //number of chains to render
        numberOfChainsBelow = 0;
        BlockPos pos = getBlockPos();
        for(int y = pos.getY() - 1; y > level.getMinBuildHeight(); y--){
            if(isPassable(level, pos.atY(y))) numberOfChainsBelow++;
            else break;
        }
        if(id != null && sLevel.getEntity(id) != null){
            //sequence level
            LivingEntityBeyonderCapability cap = sLevel.getEntity(id).getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
            this.sequenceLevel = cap.getSequenceLevel();

            //AOJ status
            isInAOJ = AreaOfJurisdictionAbility.isPosInAOJ(pos, cap, 0);

            //ally group list
            AllySystemSaveData allyData = AllySystemSaveData.from(sLevel);
            casterAllyGroups = new ArrayList<>(allyData.getGroupNamesPlayerIsIn(id));
        } else {
            casterAllyGroups = new ArrayList<>();
            isInAOJ = false;
            this.sequenceLevel = 8;
        }
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public static boolean isPassable(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return true;
        if(state.is(ModBlocks.WATER_TRAP_BLOCK.get()))
            return false;

        VoxelShape shape = state.getCollisionShape(level, pos, CollisionContext.empty());

        if (shape.isEmpty() || state.canBeReplaced()) {
            return true;
        }

        AABB centerRay = new AABB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
        boolean blocksCenter = shape.toAabbs().stream().anyMatch(aabb -> aabb.intersects(centerRay));

        return !blocksCenter;
    }

    public void markForAbsorption() {
        diffused = true;
    }

    public void onDestroy(){
        if(!diffused) tryToExplode(false);
        //confetti here.

        if(diffused){
            PacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> level.dimension()), new GeneralAreaEffectMessage(ParticleMaker.Preset.WATER_IMPLOSION, getBlockPos().getCenter().toVector3f(), 2));
            level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1, (float) level.random.triangle(1, 0.2f));
        }
        else{
            PacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> level.dimension()), new GeneralAreaEffectMessage(ParticleMaker.Preset.WATER_TRAP, getBlockPos().getCenter().toVector3f(), 2));
            level.playSound(null, getBlockPos(), ModSounds.WATER_TRAP.get(), SoundSource.BLOCKS, 1, (float) level.random.triangle(1, 0.2f));
        }

    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("chains", numberOfChainsBelow);
        tag.putBoolean("aoj", isInAOJ);
        ModCompoundTags.writeStringList(tag, "groups", casterAllyGroups);
        if(id != null) tag.putUUID("casterId", id);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.numberOfChainsBelow = tag.getInt("chains");
        this.isInAOJ = tag.getBoolean("aoj");
        this.casterAllyGroups = ModCompoundTags.readStringList(tag, "groups");
        if(tag.contains("casterId")) id = tag.getUUID("casterId"); else id = null;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) handleUpdateTag(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        CompoundTag modData = new CompoundTag();
        modData.putInt("effect", this.effectIndex);
        modData.putInt("level", this.sequenceLevel);
        if(id != null){
            modData.putUUID("playerId", id);
        }
        pTag.put(Potioneer.MOD_ID, modData);
        pTag.put("otherData", getUpdateTag());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.effectIndex = pTag.getCompound(Potioneer.MOD_ID).getInt("effect");
        this.sequenceLevel = pTag.getCompound(Potioneer.MOD_ID).getInt("level");
        handleUpdateTag(pTag.getCompound("otherData"));
        if(pTag.getCompound(Potioneer.MOD_ID).contains("playerId")) id = pTag.getCompound(Potioneer.MOD_ID).getUUID("playerId");
    }
}
