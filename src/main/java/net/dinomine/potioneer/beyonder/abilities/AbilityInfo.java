package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class AbilityInfo {
    private int posY;
    private int pathwayId;
    private int cost;
    private String descId;
    private boolean enabled;
    private int cooldown;
    private int maxCd;
    private String innerAbilityId;
    private AbilityKey key = new AbilityKey();
    private boolean hasSecondaryFunction;
    private CompoundTag abilityData = new CompoundTag();

    public AbilityInfo(int posY, int pathwayId, int cost, int cooldown, int maxCooldown, boolean enabled, String descId, String innerId, boolean hasSecondaryFunction) {
        this.posY = posY;
        this.pathwayId = pathwayId;
        this.cost = cost;
        this.cooldown = cooldown;
        this.maxCd = maxCooldown;
        this.descId = descId;
        this.enabled = enabled;
        this.innerAbilityId = innerId;
        this.hasSecondaryFunction = hasSecondaryFunction;
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
        buffer.writeInt(posY);
        buffer.writeInt(pathwayId);
        buffer.writeInt(cost);
        buffer.writeInt(cooldown);
        buffer.writeInt(maxCd);
        buffer.writeBoolean(enabled);
        BufferUtils.writeStringToBuffer(descId, buffer);
        BufferUtils.writeStringToBuffer(innerAbilityId, buffer);
        key.writeToBuffer(buffer);
        buffer.writeNbt(abilityData);
        buffer.writeBoolean(hasSecondaryFunction);
    }

    public static AbilityInfo decode(FriendlyByteBuf buffer){
        int y = buffer.readInt();
        int pathwayId = buffer.readInt();
        int cost = buffer.readInt();
        int cooldown = buffer.readInt();
        int maxCd = buffer.readInt();
        boolean enabled = buffer.readBoolean();
        String descId = BufferUtils.readString(buffer);
        String innerId = BufferUtils.readString(buffer);
        AbilityKey key = AbilityKey.readFromBuffer(buffer);
        CompoundTag tag = buffer.readAnySizeNbt();
        boolean secondary = buffer.readBoolean();
        return new AbilityInfo(y, pathwayId, cost, cooldown, maxCd, enabled, descId, innerId, secondary).withKey(key).withData(tag);
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

    public int getPosY(){
        return posY;
    }

    public boolean hasSecondaryFunction(){
        return hasSecondaryFunction;
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
