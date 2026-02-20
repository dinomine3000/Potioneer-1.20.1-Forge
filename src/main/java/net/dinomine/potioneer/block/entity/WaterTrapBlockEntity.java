package net.dinomine.potioneer.block.entity;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class WaterTrapBlockEntity extends BlockEntity implements GeoBlockEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int effectIndex = 0;
    private int sequenceLevel = 8;
    private UUID id = null;

    public WaterTrapBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WATER_TRAP_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void setPlacedByPlayer(UUID uuid, int sequenceLevel){
        this.id = uuid;
        this.sequenceLevel = sequenceLevel;
        if (level != null && !level.isClientSide) {
            setChanged();
        }
    }

    public boolean isOwner(UUID uId, int sequenceId){
        //return(Math.floorDiv(sequenceId, 10) == 1 && Math.floorMod(sequenceId, 10) <= 8 && uId.compareTo(id) == 0);
        //changed to account for using artifacts -> the owner does not have to be of the pathway
        return uId.compareTo(id) == 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        state.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.model.create"));
        return PlayState.CONTINUE;
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        AABB box = new AABB(pPos.offset(2, 1, 2), pPos.offset(-1, -1, -1));
        ArrayList<Entity> entities = new ArrayList<>(pLevel.getEntities((Entity)null, box, entity -> entity instanceof LivingEntity));

        if(!entities.isEmpty()) {
            assert level != null;
            Player caster = level.getPlayerByUUID(id);
            Optional<LivingEntityBeyonderCapability> optCap = caster.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
            for(Entity ent: entities){
                if(!(ent instanceof LivingEntity living)) return;
                if(isEntityAllyOfOwner(living)) return;
                setChanged();
                applyEffectsToEntity(pLevel, pPos, (LivingEntity) ent);
            }
            if(caster != null && optCap.isPresent() && optCap.get().getAbilitiesManager().hasAbilityOrBetter(Abilities.TYRANT_WATER_TRAP.getAblId(), 7)){
                caster.sendSystemMessage(Component.translatable("potioneer.message.water_trap_activated"));
            }

            pLevel.destroyBlock(pPos, false);
        }
    }

    private boolean isEntityAllyOfOwner(LivingEntity ent){
        if(!(level instanceof ServerLevel serverLevel)) return false;
        if(ent instanceof Player player){
            AllySystemSaveData data = AllySystemSaveData.from(serverLevel);
            return data.isPlayerAllyOf(player.getUUID(), id);
        } return false;
    }

    private void applyEffectsToEntity(Level level, BlockPos pos, LivingEntity entity){
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
                if(
                        level.isEmptyBlock(pos.below().below())
                     && level.isEmptyBlock(pos.below().below().below())
                        )
                {
                    BlockPos target = pos.below().below().below();
                    entity.teleportTo(target.getX(), target.getY(), target.getZ());
                    break;
                }
            default:
                Player player = level.getPlayerByUUID(id);
                if(player == null){
                    entity.hurt(level.damageSources().magic(), -1 + (10 - sequenceLevel)*3);
                } else {
                    entity.hurt(level.damageSources().indirectMagic(player, null), -1 + (10 - sequenceLevel)*3);
                }
                break;

        }
    }

    public void incrementIndex(Player player, int sequenceId){
        if(!isOwner(player.getUUID(), sequenceId)) return;
        setChanged();
        effectIndex = Math.floorMod((effectIndex + (player.isCrouching() ? -1 : 1)), 4);
        player.sendSystemMessage(Component.translatable("potioneer.pathway.trap_effect_" + effectIndex));
    }

    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public @org.jetbrains.annotations.Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.effectIndex = pTag.getCompound(Potioneer.MOD_ID).getInt("effect");
        this.sequenceLevel = pTag.getCompound(Potioneer.MOD_ID).getInt("level");
        if(pTag.getCompound(Potioneer.MOD_ID).contains("playerId")) id = pTag.getCompound(Potioneer.MOD_ID).getUUID("playerId");
    }

}
