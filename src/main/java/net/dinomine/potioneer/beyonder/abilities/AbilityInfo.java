package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class AbilityInfo {
    private final int pathwayId;
    private final String descId;
    private boolean enabled;
    private int cooldown;
    private int maxCd;
    private final String innerAbilityId;
    private AbilityKey key = new AbilityKey();
    private CompoundTag abilityData = new CompoundTag();

    public AbilityInfo(int pathwayId, int cooldown, int maxCooldown, boolean enabled, String descId, String innerId) {
        this.pathwayId = pathwayId;
        this.cooldown = cooldown;
        this.maxCd = maxCooldown;
        this.descId = descId;
        this.enabled = enabled;
        this.innerAbilityId = innerId;
    }

    public AbilityInfo withKey(AbilityKey key){
        this.key = key;
        return this;
    }

    public AbilityKey getKey(){
        return this.key;
    }

//    public AbilityInfo(int posX, int posY, String name, int sequenceId, int cost, int maxCooldown, String descId){
//
//    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(pathwayId);
        buffer.writeInt(cooldown);
        buffer.writeInt(maxCd);
        buffer.writeBoolean(enabled);
        BufferUtils.writeStringToBuffer(descId, buffer);
        BufferUtils.writeStringToBuffer(innerAbilityId, buffer);
        key.writeToBuffer(buffer);
        buffer.writeNbt(abilityData);
    }

    public static AbilityInfo decode(FriendlyByteBuf buffer){
        int pathwayId = buffer.readInt();
        int cooldown = buffer.readInt();
        int maxCd = buffer.readInt();
        boolean enabled = buffer.readBoolean();
        String descId = BufferUtils.readString(buffer);
        String innerId = BufferUtils.readString(buffer);
        AbilityKey key = AbilityKey.readFromBuffer(buffer);
        CompoundTag tag = buffer.readAnySizeNbt();
        return new AbilityInfo(pathwayId, cooldown, maxCd, enabled, descId, innerId).withKey(key).withData(tag);
    }

    public String innerId(){
        return innerAbilityId;
    }

    public String descId(){
        return descId;
    }

    public int maxCooldown() {
        return this.maxCd;
    }

    public int getPathwayId(){
        return pathwayId;
    }

    public void setEnabled(boolean state){
        enabled = state;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public int getCooldown(){
        return cooldown;
    }

    public void tickCooldown(){
        if(cooldown > 0) cooldown--;
    }

    public AbilityInfo withCooldown(int cd, int maxCd){
        this.cooldown = cd;
        this.maxCd = maxCd;
        return this;
    }

    public AbilityInfo withData(CompoundTag abilityData) {
        this.abilityData = abilityData;
        return this;
    }

    public CompoundTag getData() {
        return abilityData;
    }
}
