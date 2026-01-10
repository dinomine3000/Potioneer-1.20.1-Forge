package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.network.FriendlyByteBuf;

public class AbilityInfo {
    private int posY;
    private int pathwayId;
    private int cost;
    private float cooldownPercentage;
    private String descId;
    private boolean enabled;
    private int maxCd;
    private String innerAbilityId;
    private String cAblId = null;
    private boolean hasSecondaryFunction;

    public AbilityInfo(int posY, int pathwayId, int cost, float cooldown, boolean enabled, String descId, String innerId, boolean hasSecondaryFunction) {
        this.posY = posY;
        this.pathwayId = pathwayId;
        this.cost = cost;
        this.cooldownPercentage = cooldown;
        this.descId = descId;
        this.enabled = enabled;
        this.innerAbilityId = innerId;
        this.hasSecondaryFunction = hasSecondaryFunction;
    }

    public AbilityInfo withCompleteId(String cAblId){
        this.cAblId = cAblId;
        return this;
    }

    public String getCompleteId(){
        return this.cAblId;
    }

//    public AbilityInfo(int posX, int posY, String name, int sequenceId, int cost, int maxCooldown, String descId){
//
//    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(posY);
        buffer.writeInt(pathwayId);
        buffer.writeInt(cost);
        buffer.writeFloat(cooldownPercentage);
        buffer.writeBoolean(enabled);
        BufferUtils.writeStringToBuffer(descId, buffer);
        BufferUtils.writeStringToBuffer(innerAbilityId, buffer);
        buffer.writeBoolean(hasSecondaryFunction);
    }

    public static AbilityInfo decode(FriendlyByteBuf buffer){
        int y = buffer.readInt();
        int pathwayId = buffer.readInt();
        int cost = buffer.readInt();
        float cooldown = buffer.readFloat();
        boolean enabled = buffer.readBoolean();
        String descId = BufferUtils.readString(buffer);
        String innerId = BufferUtils.readString(buffer);
        boolean secondary = buffer.readBoolean();
        return new AbilityInfo(y, pathwayId, cost, cooldown, enabled, descId, innerId, secondary);
    }

    public String innerId(){
        return innerAbilityId;
    }

    public String descId(){
        return descId;
    }

    public AbilityInfo withMaxCd(int maxCd) {
        this.maxCd = maxCd;
        return this;
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
}
