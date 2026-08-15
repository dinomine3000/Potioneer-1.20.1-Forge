package net.dinomine.potioneer.entities.custom;

import com.mojang.authlib.GameProfile;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.entities.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static net.dinomine.potioneer.beyonder.abilities.mystery.CloneAbility.MAX_CLONE_DIST;

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
    public enum Type{
        DEATH, //fake out death
        INHABIT, //leave behind while real player explores. in theory, itd be the opposite - the player inhabits the illusion, leaving behind the main body.
        INVISIBLE //leave behind and turn real player invisible
    }
    public Type type = Type.INVISIBLE;

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

    public void setCloneHealth(float maxHealth, float health){
        setMaxHealthAndSync(maxHealth);
        setHealth(health);
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
        if(level().isClientSide()) return;
        if(type == Type.INVISIBLE && tickCount > 20*5) remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void actuallyHurt(DamageSource pDamageSource, float pDamageAmount) {
        super.actuallyHurt(pDamageSource, pDamageAmount);
        if(type == Type.INVISIBLE || type == Type.INHABIT) remove(RemovalReason.DISCARDED);
    }

    @Override
    public void remove(RemovalReason pReason) {
        Player player = getPlayer();
        if(player == null) {
            super.remove(pReason);
            return;
        }
        if(type == Type.INVISIBLE) {
            CapProvider.beyonder(player).ifPresent(cap -> {
                cap.getEffectsManager().removeEffect(BeyonderEffects.MYSTERY_INVISIBLE.getEffectId());
            });
        }
        else if(type == Type.INHABIT){
            CapProvider.beyonder(player).ifPresent(cap -> {
                if(cap.getEffectsManager().removeEffect(BeyonderEffects.MYSTERY_ILLUSION.getEffectId())){
                    if(!level().isClientSide()) {
                        if(AbilityFunctionHelper.teleportEntity(player, (ServerLevel) player.level(), (ServerLevel) level(), getOnPos().above(), getXRot(), getYRot()))
                            player.setHealth(getHealth());
                    }
                }
            });
        }
        super.remove(pReason);
    }

    protected void setMaxHealthAndSync(float maxHealth) {
        var hpAttribute = this.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttribute != null) {
            hpAttribute.setBaseValue(maxHealth);
            this.setHealth(maxHealth);
        }
    }
    public boolean isCloneOf(UUID playerId){
        return getPlayer() != null && getPlayer().getUUID().equals(playerId);
    }

    private @Nullable Player getPlayer(){
        Optional<UUID> optId = entityData.get(ORIGINAL_ID);
        if(optId.isEmpty()) return null;
        return level().getPlayerByUUID(optId.get());
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

    private static final UUID HP_MOD = UUID.fromString("e14d6f63-4978-430f-a02a-904a680f72c4");

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
