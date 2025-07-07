package net.dinomine.potioneer.entities.custom;

import net.dinomine.potioneer.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class PlaceableItemEntity extends Entity {
    public static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(PlaceableItemEntity.class, EntityDataSerializers.ITEM_STACK);

    public PlaceableItemEntity(EntityType<? extends Entity> pEntityType, Level pLevel, boolean physics, boolean invulnerable, boolean horizontalCollision, ItemStack item) {
        super(pEntityType, pLevel);
        this.horizontalCollision = horizontalCollision;
        this.setInvulnerable(invulnerable);
        this.noPhysics = physics;
        entityData.set(ITEM, item);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        return new ItemStack(entityData.get(ITEM).getItem());
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        if(pPlayer.isCrouching()){
            System.out.println("Interacting with placeable entity " + pPlayer.level().isClientSide());
            pPlayer.addItem(entityData.get(ITEM));
            this.kill();
            pPlayer.level().playSound(pPlayer, this.getOnPos(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS);
            return InteractionResult.SUCCESS;
        }
        return onPlayerRClick(pPlayer, pHand);
    }

    /**
     * Method called after the class has decided that the player does not want to pick up the item
     * (if the player is crouching it assumes they want to pick it up)
     * if you want a different behavious, override interact(). otherwise, just fill in this method
     * @param pPlayer
     * @param pHand
     * @return
     */
    protected abstract InteractionResult onPlayerRClick(Player pPlayer, InteractionHand pHand);

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        entityData.set(ITEM, ItemStack.of(compoundTag.getCompound("item_info")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.put("item_info", entityData.get(ITEM).save(new CompoundTag()));
    }

    @Override
    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        return true;
    }
}
