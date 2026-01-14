package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

public class AbilityKey {
    private String abilityGroup;
    private String abilityId;
    private int sequenceLevel;
    private int hash;

    public AbilityKey(String abilityGroup, String abilityId, int level){
        this.abilityGroup = abilityGroup;
        this.abilityId = abilityId;
        this.sequenceLevel = level;
        doHash();
    }

    public static AbilityKey fromString(String string) {
        if(string == null) return null;
        if(string.isEmpty()) return null;
        String[] id = string.split(":");
        if(id.length != 3) return null;
        return new AbilityKey(id[0], id[1], Integer.parseInt(id[2]));
    }

    @Override
    public String toString() {
        return abilityGroup.concat(":" + abilityId).concat(":" + sequenceLevel);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AbilityKey ablKey)) return false;
        return ablKey.hash == this.hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public String getGroup() {
        return this.abilityGroup;
    }
    
    public String getAbilityId(){
        return this.abilityId;
    }
    
    public int getSequenceLevel(){
        return this.sequenceLevel;
    }
    
    public boolean isOuterId(String outerId){
        if(outerId.split(":").length != 0) return false;
        return this.toString().contains(outerId);
    }
    
    public boolean isSameGroup(String Group){
        return this.abilityGroup.equals(Group);
    }
    
    public boolean isSameAbility(String abilityId){
        return this.abilityId.equals(abilityId);
    }
    
    public boolean isBetterOrEqual(AbilityKey otherKey, boolean checkAbilityGroup){
        return isSameAbility(otherKey.getAbilityId()) 
                && (isSameGroup(otherKey.getGroup()) || !checkAbilityGroup)
                && this.sequenceLevel <= otherKey.sequenceLevel;
    }
    
    public void writeToBuffer(FriendlyByteBuf buffer){
        BufferUtils.writeStringToBuffer(this.abilityGroup, buffer);
        BufferUtils.writeStringToBuffer(this.abilityId, buffer);
        buffer.writeInt(sequenceLevel);
    }
    
    public static AbilityKey readFromBuffer(FriendlyByteBuf buffer){
        String list = BufferUtils.readString(buffer);
        String abilityId = BufferUtils.readString(buffer);
        int level = buffer.readInt();
        return new AbilityKey(list, abilityId, level);
    }

    public void setSequenceLevel(int sequenceLevel) {
        this.sequenceLevel = sequenceLevel;
        doHash();
    }

    private void doHash(){
        this.hash = Objects.hash(abilityGroup, abilityId, sequenceLevel);
    }
}
