package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;

public class AbilityInfo {
    private final int pathwayId;
    private final String descId;
    private final LinkedHashSet<String> allDescIds;
    private boolean enabled;
    private int cooldown;
    private int maxCd;
    private final String innerAbilityId;
    private AbilityKey key = new AbilityKey();
    private CompoundTag abilityData = new CompoundTag();
    private boolean isDownside = false;

    public AbilityInfo(int pathwayId, int cooldown, int maxCooldown, boolean enabled, String descId, LinkedHashSet<String> allDescIds, String innerId) {
        this.pathwayId = pathwayId;
        this.cooldown = cooldown;
        this.maxCd = maxCooldown;
        this.descId = descId;
        this.allDescIds = allDescIds;
        this.enabled = enabled;
        this.innerAbilityId = innerId;
    }

    public AbilityInfo markDownside(){
        return this.markDownside(true);
    }

    protected AbilityInfo markDownside(boolean isDownside){
        this.isDownside = isDownside;
        return this;
    }

    public boolean isDownside(){
        return isDownside;
    }

    public AbilityInfo withKey(AbilityKey key){
        this.key = key;
        return this;
    }

    public AbilityKey getKey(){
        return this.key;
    }

    public int getSequenceLevel(){
        return this.key.getSequenceLevel();
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

        buffer.writeVarInt(allDescIds.size());
        for (String id : allDescIds) {
            BufferUtils.writeStringToBuffer(id, buffer);
        }

        BufferUtils.writeStringToBuffer(innerAbilityId, buffer);
        key.writeToBuffer(buffer);
        buffer.writeNbt(abilityData);
        buffer.writeBoolean(isDownside);
    }

    public static AbilityInfo decode(FriendlyByteBuf buffer){
        int pathwayId = buffer.readInt();
        int cooldown = buffer.readInt();
        int maxCd = buffer.readInt();
        boolean enabled = buffer.readBoolean();
        String descId = BufferUtils.readString(buffer);

        int count = buffer.readVarInt();
        LinkedHashSet<String> allDescIds = new LinkedHashSet<>(count);
        for (int i = 0; i < count; i++) {
            allDescIds.add(BufferUtils.readString(buffer));
        }

        String innerId = BufferUtils.readString(buffer);
        AbilityKey key = AbilityKey.readFromBuffer(buffer);
        CompoundTag tag = buffer.readAnySizeNbt();
        boolean downside = buffer.readBoolean();
        return new AbilityInfo(pathwayId, cooldown, maxCd, enabled, descId, allDescIds, innerId).withKey(key).withData(tag).markDownside(downside);
    }
    public String innerId(){
        return innerAbilityId;
    }

    public String descId(){
        return descId;
    }

    public LinkedHashSet<String> allDescIds(){return allDescIds;}

    public Component getNameComponent(){
        return Component.translatableWithFallback("ability.potioneer_name." + descId(), StringUtils.capitalize(descId.replace("_", " ")));
    }

    public MutableComponent getMutableNameComponent(){
        return Component.translatableWithFallback("ability.potioneer_name." + descId(), StringUtils.capitalize(descId.replace("_", " ")));
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
