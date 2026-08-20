package net.dinomine.potioneer.beyonder.abilities;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class AbilityInfo {

    @OnlyIn(Dist.CLIENT)
    public void tickCooldown() {
        if(cooldown > 0) cooldown--;
    }

    public MutableComponent getNameComponent() {
        return Ability.getNameComponent(getDescId());
    }

    public int getPathwayId() {
        return Abilities.getFactory(getAbilityId()).get().getPathwayId();
    }
    public int getPosY() {
        return 32 + 24*Abilities.getFactory(getAbilityId()).get().getPosY();
    }

    public enum Group {
        INTRINSIC,
        ARTIFACT,
        CONTRACT,
        RECORDED,
        STOLEN
    }

    private ResourceLocation abilityId;
    private UUID instanceId;
    private ItemStack artifactStack = ItemStack.EMPTY;
    private Group group = Group.INTRINSIC;

    private String descId = "";
    private LinkedHashSet<String> allDescIds = new LinkedHashSet<>();
    private boolean enabled = true;
    private boolean revoked;
    private int cooldown;
    private int maxCd;
    private CompoundTag abilityData = new CompoundTag();
    private int sequenceLevel;
    private int trueSequenceLevel;
    private boolean hasSecondary;

    public AbilityInfo(ResourceLocation abilityId) {
        this.abilityId = Objects.requireNonNull(abilityId, "abilityId cannot be null");
        this.instanceId = UUID.randomUUID();
    }

    public void setAbilityId(ResourceLocation abilityId) {
        this.abilityId = Objects.requireNonNull(abilityId, "abilityId cannot be null");
    }

    public void setInstanceId(UUID instanceId) {
        this.instanceId = instanceId != null ? instanceId : UUID.randomUUID();
    }

    public void setArtifactStack(ItemStack artifactStack) {
        this.artifactStack = artifactStack != null ? artifactStack : ItemStack.EMPTY;
    }

    public void setGroup(Group group) {
        this.group = group != null ? group : Group.INTRINSIC;
    }

    public void setDescId(String descId) {
        this.descId = descId != null ? descId : "";
    }

    public void setAllDescIds(LinkedHashSet<String> allDescIds) {
        this.allDescIds = allDescIds != null ? new LinkedHashSet<>(allDescIds) : new LinkedHashSet<>();
    }

    public void setCooldown(int cooldown) {
        this.cooldown = Math.max(-1, cooldown);
    }

    public void setMaxCd(int maxCd) {
        this.maxCd = Math.max(1, maxCd);
    }

    public boolean setAbilityData(CompoundTag abilityData) {
        if (abilityData == null) return false;
        if (this.abilityData.equals(abilityData)) return false;
        this.abilityData = abilityData.copy();
        return true;
    }

    public void copyFrom(AbilityInfo source) {
        if (source == null) return;

        this.abilityId = source.abilityId;
        this.instanceId = source.instanceId;
        this.artifactStack = source.artifactStack.copy();
        this.group = source.group;

        this.descId = source.descId;
        this.allDescIds = new LinkedHashSet<>(source.allDescIds);
        this.enabled = source.enabled;
        this.revoked = source.revoked;
        this.cooldown = source.cooldown;
        this.maxCd = source.maxCd;
        this.abilityData = source.abilityData != null ? source.abilityData.copy() : new CompoundTag();
        this.sequenceLevel = source.sequenceLevel;
        this.trueSequenceLevel = source.trueSequenceLevel;
        this.hasSecondary = source.hasSecondary;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("AbilityId", abilityId.toString());
        tag.putUUID("InstanceId", instanceId);
        tag.put("ArtifactStack", artifactStack.serializeNBT());
        tag.putString("Group", group.name());

        tag.putString("DescId", descId);
        ListTag descList = new ListTag();
        for (String id : allDescIds) {
            descList.add(StringTag.valueOf(id));
        }
        tag.put("AllDescIds", descList);

        tag.putBoolean("Enabled", enabled);
        tag.putBoolean("Revoked", revoked);
        tag.putInt("Cooldown", cooldown);
        tag.putInt("MaxCd", maxCd);
        tag.put("AbilityData", abilityData.copy());
        tag.putInt("SequenceLevel", sequenceLevel);
        tag.putInt("TrueSequenceLevel", trueSequenceLevel);
        tag.putBoolean("HasSecondary", hasSecondary);
        return tag;
    }

    public static AbilityInfo deserializeNBT(CompoundTag tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("AbilityId"));
        AbilityInfo info = new AbilityInfo(id);

        info.instanceId = tag.hasUUID("InstanceId") ? tag.getUUID("InstanceId") : UUID.randomUUID();
        info.artifactStack = tag.contains("ArtifactStack", Tag.TAG_COMPOUND) ? ItemStack.of(tag.getCompound("ArtifactStack")) : ItemStack.EMPTY;
        if (tag.contains("Group", Tag.TAG_STRING)) {
            try {
                info.group = Group.valueOf(tag.getString("Group"));
            } catch (IllegalArgumentException e) {
                info.group = Group.INTRINSIC;
            }
        }

        info.descId = tag.getString("DescId");
        LinkedHashSet<String> descIds = new LinkedHashSet<>();
        ListTag descList = tag.getList("AllDescIds", Tag.TAG_STRING);
        for (int i = 0; i < descList.size(); i++) {
            descIds.add(descList.getString(i));
        }
        info.allDescIds = descIds;

        info.enabled = tag.getBoolean("Enabled");
        info.revoked = tag.getBoolean("Revoked");
        info.cooldown = tag.getInt("Cooldown");
        info.maxCd = tag.getInt("MaxCd");
        info.abilityData = tag.getCompound("AbilityData").copy();
        info.sequenceLevel = tag.getInt("SequenceLevel");
        info.trueSequenceLevel = tag.getInt("TrueSequenceLevel");
        info.hasSecondary = tag.getBoolean("HasSecondary");
        return info;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(abilityId);
        buf.writeUUID(instanceId);
        buf.writeItem(artifactStack);
        buf.writeEnum(group);

        buf.writeUtf(descId);
        buf.writeVarInt(allDescIds.size());
        for (String id : allDescIds) {
            buf.writeUtf(id);
        }

        buf.writeBoolean(enabled);
        buf.writeBoolean(revoked);
        buf.writeVarInt(cooldown);
        buf.writeVarInt(maxCd);
        buf.writeNbt(abilityData);
        buf.writeVarInt(sequenceLevel);
        buf.writeVarInt(trueSequenceLevel);
        buf.writeBoolean(hasSecondary);
    }

    public static AbilityInfo decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        AbilityInfo info = new AbilityInfo(id);

        info.instanceId = buf.readUUID();
        info.artifactStack = buf.readItem();
        info.group = buf.readEnum(Group.class);

        info.descId = buf.readUtf();
        int descCount = buf.readVarInt();
        LinkedHashSet<String> descIds = new LinkedHashSet<>();
        for (int i = 0; i < descCount; i++) {
            descIds.add(buf.readUtf());
        }
        info.allDescIds = descIds;

        info.enabled = buf.readBoolean();
        info.revoked = buf.readBoolean();
        info.cooldown = buf.readVarInt();
        info.maxCd = buf.readVarInt();
        info.abilityData = buf.readNbt();
        if (info.abilityData == null) {
            info.abilityData = new CompoundTag();
        }
        info.sequenceLevel = buf.readVarInt();
        info.trueSequenceLevel = buf.readVarInt();
        info.hasSecondary = buf.readBoolean();
        return info;
    }
}