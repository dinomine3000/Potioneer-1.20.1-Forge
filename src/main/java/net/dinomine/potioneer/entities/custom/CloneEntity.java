package net.dinomine.potioneer.entities.custom;

import com.mojang.authlib.GameProfile;
import net.dinomine.potioneer.entities.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class CloneEntity extends LivingEntity {
    private static final GameProfile DEFAULT_PROFILE = new GameProfile(UUID.fromString("74dbe981-4682-48d3-876a-760e5b91de6f"), "DevClone");

    public static final EntityDataAccessor<String> ORIGINAL_NAME = SynchedEntityData.defineId(CloneEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Optional<UUID>> ORIGINAL_ID = SynchedEntityData.defineId(CloneEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    @OnlyIn(Dist.CLIENT)
    private ResourceLocation clientSkinLocation;
    @OnlyIn(Dist.CLIENT)
    private boolean clientIsSlim = false;
    @OnlyIn(Dist.CLIENT)
    private boolean skinLoaded = false;

    private GameProfile profile = null;

    public CloneEntity(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setCustomNameVisible(true);
    }


    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getClientSkinLocation() {
        return this.clientSkinLocation;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isClientSlim() {
        return this.clientIsSlim;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSkinLoaded() {
        return this.skinLoaded;
    }

    @OnlyIn(Dist.CLIENT)
    public void setSkinData(ResourceLocation location, boolean isSlim) {
        this.clientSkinLocation = location;
        this.clientIsSlim = isSlim;
        this.skinLoaded = true;
    }

    public static CloneEntity clone(Player player, ServerLevel level){
        CloneEntity clone = new CloneEntity(ModEntities.CLONE_ENTITY.get(), level);
        clone.updateProfile(player.getUUID(), player.getDisplayName().getString());
        clone.setXRot(player.getXRot());
        clone.setYRot(player.getYRot());
        clone.setYHeadRot(player.getYHeadRot());
        return clone;
    }

    private void updateProfile(UUID id, String name){
        profile = new GameProfile(id, name);
        setName(name);
        setCloneId(id);
    }

    public void setCloneId(UUID id){
        entityData.set(ORIGINAL_ID, Optional.of(id));
    }
    public void setName(String name){
        entityData.set(ORIGINAL_NAME, name);
        setCustomNameVisible(true);
        setCustomName(Component.literal(name));
    }

    public GameProfile getProfile(){
        if(profile != null) return profile;
        Optional<UUID> id = entityData.get(ORIGINAL_ID);
        if(id.isEmpty()) return DEFAULT_PROFILE;
        return new GameProfile(id.get(), entityData.get(ORIGINAL_NAME));
    }

    boolean crouching = false;

    @Override
    public void tick() {
        super.tick();
        setShiftKeyDown(crouching);
        if(!level().isClientSide()) return;
        if(tickCount%40 == 0) crouching = !crouching;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ORIGINAL_ID, Optional.empty());
        this.entityData.define(ORIGINAL_NAME, "");
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean shouldShowName() {
        return !entityData.get(ORIGINAL_NAME).isEmpty();
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {}

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }


    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        return 1.6 * (double)(this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + pAttackTarget.getBbWidth());
    }

    public static AttributeSupplier setAttributes(){
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 1)
                .add(Attributes.ATTACK_SPEED, 0.2f).build();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        CompoundTag profileTag = new CompoundTag();
        profileTag.putString("name", entityData.get(ORIGINAL_NAME));
        if(entityData.get(ORIGINAL_ID).isPresent()) profileTag.putUUID("id", entityData.get(ORIGINAL_ID).get());
        pCompound.put("cloneProfile", profileTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        CompoundTag profileTag = pCompound.getCompound("cloneProfile");
        if(!profileTag.contains("id")) return;
        updateProfile(profileTag.getUUID("id"), profileTag.getString("name"));
    }
}
