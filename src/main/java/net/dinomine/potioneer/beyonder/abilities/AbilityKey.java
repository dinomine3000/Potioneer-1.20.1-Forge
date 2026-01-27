package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.UUID;

public class AbilityKey {
    private String abilityGroup;
    private String abilityId;
    private int sequenceLevel;
    private UUID artifactId;
    private int hash;

    public AbilityKey(String abilityId, int sequenceLevel) {
        this("", abilityId, sequenceLevel);
    }

    public boolean isArtifactKey(){
        return artifactId != null;
    }

    public UUID getArtifactId(){
        return artifactId;
    }

    public AbilityKey(Ability abl, String abilityGroup){
        this(abilityGroup, abl.getAbilityId(), abl.getSequenceLevel());
    }

    public AbilityKey(){
        this.abilityGroup = "";
        this.abilityId= "";
        this.sequenceLevel = -1;
        this.artifactId = null;
        doHash();
    }

    public AbilityKey(String abilityGroup, String abilityId, int level){
        this.abilityGroup = abilityGroup;
        this.abilityId = abilityId;
        this.sequenceLevel = level;
        this.artifactId = null;
        doHash();
    }

    public AbilityKey(String abilityGroup, String abilityId, int level, UUID artifactId){
        this(abilityGroup, abilityId, level);
        this.artifactId = artifactId;
        doHash();
    }

    public AbilityKey(String abilityId, int level, UUID artifactId){
        this(PlayerAbilitiesManager.AbilityList.ARTIFACT.name(), abilityId, level);
        this.artifactId = artifactId;
        doHash();
    }

    public AbilityKey onArtifact(UUID artifactId){
        this.abilityGroup = PlayerAbilitiesManager.AbilityList.ARTIFACT.name();
        this.artifactId = artifactId;
        doHash();
        return this;
    }

    public static AbilityKey fromString(String string) {
        if(string == null) return new AbilityKey();
        if(string.isEmpty()) return new AbilityKey();
        String[] id = string.split(":");
        if(id.length == 2) return new AbilityKey(id[0], Integer.parseInt(id[1]));
        if(id.length == 3) return new AbilityKey(id[0], id[1], Integer.parseInt(id[2]));
        if(id.length == 4) return new AbilityKey(id[0], id[1], Integer.parseInt(id[2]), UUID.fromString(id[3]));
        return new AbilityKey();
    }

    public AbilityKey withArtifactInfo(UUID artifactId){
        return new AbilityKey(abilityId, sequenceLevel, artifactId);
    }

    @Override
    public String toString() {
        if(isEmpty()) return "Empty Key";
        if(isArtifactKey())
            return abilityGroup.concat(":" + abilityId).concat(":" + sequenceLevel).concat(":" + artifactId.toString());
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

    public Component getNameComponent(){
        return Abilities.getAbilityInstance(abilityId, sequenceLevel).getAbilityInfo().getNameComponent();
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
        if(abilityId.isEmpty()){
            buffer.writeBoolean(true);
        } else {
            buffer.writeBoolean(false);
            BufferUtils.writeStringToBuffer(this.abilityGroup, buffer);
            BufferUtils.writeStringToBuffer(this.abilityId, buffer);
            buffer.writeInt(sequenceLevel);
            if(isArtifactKey()){
                buffer.writeBoolean(true);
                buffer.writeUUID(artifactId);
            } else
                buffer.writeBoolean(false);
        }
    }
    
    public static AbilityKey readFromBuffer(FriendlyByteBuf buffer){
        if(buffer.readBoolean()){
            return new AbilityKey();
        }
        String list = BufferUtils.readString(buffer);
        String abilityId = BufferUtils.readString(buffer);
        int level = buffer.readInt();
        if(buffer.readBoolean()){
            UUID artId = buffer.readUUID();
            return new AbilityKey(list, abilityId, level, artId);
        }
        return new AbilityKey(list, abilityId, level);
    }

    public boolean isEmpty(){
        return abilityId.isEmpty();
    }

    public void setSequenceLevel(int sequenceLevel) {
        this.sequenceLevel = sequenceLevel;
        doHash();
    }

    private void doHash(){
        this.hash = Objects.hash(abilityGroup, abilityId, sequenceLevel, artifactId);
    }

    public AbilityKey withoutArtifactId() {
        return new AbilityKey(abilityId, sequenceLevel);
    }
}
