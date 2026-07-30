package net.dinomine.potioneer.beyonder.player;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerMiningSpeedSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class BeyonderStats {
    private float miningSpeedMult = 1;
    private boolean mayFly = false;
    private float[] playerAttributes;
    private BeyonderStats tempEffectStats = null;

    BeyonderStats(){
        this(true);
    }
    BeyonderStats(boolean idk){
        resetStats();
        if(idk)
            tempEffectStats = new BeyonderStats(false);
    }

    public void addDamage(float dmg){
        playerAttributes[1] += dmg;
    }

    public void addHealth(int i) {
        playerAttributes[0] += i;
    }

    public void addArmor(int i) {
        playerAttributes[2] += i;
    }

    public void addToughness(int i) {
        playerAttributes[3] += i;
    }

    /*public void addKnockbackRes(int i) {
        playerAttributes[4] += i;
    }*/

    public void enableFlight(){
        mayFly = true;
    }

    public boolean canFly(){
        return mayFly;
    }

    public void setMiningSpeed(float mult){
        this.miningSpeedMult = mult;
    }

    public void getMiningSpeed(PlayerEvent.BreakSpeed event){
        event.setNewSpeed(event.getOriginalSpeed()*miningSpeedMult);
    }

    public float getMiningSpeed(){
        return miningSpeedMult;
    }

    public void multMiningSpeed(float mult){this.miningSpeedMult *= mult;}

    private void updateClientIfMiningSpeedChanged(ServerPlayer player, float newSpeed){
        if(player.level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new PlayerMiningSpeedSync(newSpeed), player);
    }

    public void resetStats(){
        miningSpeedMult = 1;
        mayFly = false;
        playerAttributes = new float[]{0, 0, 0, 0, 0};
    }

    public void resetAndApplyStats(LivingEntity entity, boolean heal) {
        resetStats();
        if(entity instanceof Player player) applyStats(player, heal);
    }

    public void setEffects(BeyonderStats otherStats, LivingEntity target){
        if(otherStats.miningSpeedMult != getMiningSpeed() && target instanceof ServerPlayer player){
            updateClientIfMiningSpeedChanged(player, otherStats.miningSpeedMult);
        }
        this.miningSpeedMult = otherStats.miningSpeedMult;
        this.mayFly = otherStats.mayFly;
        //this.playerEffectAttributes = oldStore.playerAttributes;
    }

    private BeyonderStats(BeyonderStats other){
        int i = 0;
        this.playerAttributes = new float[]{other.playerAttributes[i++], other.playerAttributes[i++], other.playerAttributes[i++], other.playerAttributes[i++], other.playerAttributes[i++]};
    }

    private boolean hasSameStatsAs(BeyonderStats otherStats){
        int i = 0;
        return this.playerAttributes[i] == otherStats.playerAttributes[i++] &&
                this.playerAttributes[i] == otherStats.playerAttributes[i++] &&
                this.playerAttributes[i] == otherStats.playerAttributes[i++] &&
                this.playerAttributes[i] == otherStats.playerAttributes[i++] &&
                this.playerAttributes[i] == otherStats.playerAttributes[i++];
    }

    public void addStatsAndApplyIfChanged(BeyonderStats otherStats, Player target){
        BeyonderStats other = new BeyonderStats(otherStats);
        if(other.hasSameStatsAs(tempEffectStats)) return;
        tempEffectStats = other;
        applyStats(target, false);
    }
    public int[] getIntStats(){
        int hp = (int) (playerAttributes[0] + tempEffectStats.playerAttributes[0]);
        int dmg = (int) (playerAttributes[1] + tempEffectStats.playerAttributes[1]);
        int arm = (int) (playerAttributes[2] + tempEffectStats.playerAttributes[2]);
        int tou = (int) (playerAttributes[3] + tempEffectStats.playerAttributes[3]);
        int kno = (int) (playerAttributes[4] + tempEffectStats.playerAttributes[4]);
        return new int[]{hp, dmg, arm, tou, kno};
    }

    public void setAttributes(float[] atts){
        this.playerAttributes = atts;
    }

    public void setAttributes(int[] atts){
        float hp = (float) atts[0];
        float dmg = (float) atts[1];
        float arm = (float) atts[2];
        float tou = (float) atts[3];
        float kno = (float) atts[4];
        this.playerAttributes = new float[]{hp, dmg, arm, tou, kno};
    }

    public void applyEffects(Player player, BeyonderStats statsHolder) {
        player.getAbilities().mayfly = player.isCreative() || player.isSpectator() || statsHolder.mayFly;
    }

    private float getStat(int idx){
        return playerAttributes[idx] + tempEffectStats.playerAttributes[idx];
    }

    public void applyStats(Player player, boolean heal){
        float maxHealthO = player.getMaxHealth();
        player.getAttributes().removeAttributeModifiers(getHealthModifier(1));
        player.getAttributes().removeAttributeModifiers(getAttackModifier(2));
        player.getAttributes().removeAttributeModifiers(getArmorModifier(2));
        player.getAttributes().removeAttributeModifiers(getToughnessModifier(2));
        //player.getAttributes().removeAttributeModifiers(getKnockbackModifier(2));
//        System.out.println("Removed attributes.");
        float hpStat = getStat(0);
        float atkStat = getStat(1);
        float armStat = getStat(2);
        float touStat = getStat(3);
        float knockStat = getStat(4);

        if(hpStat != 0){
//            System.out.println("Added health.");
            player.getAttributes().addTransientAttributeModifiers(getHealthModifier(hpStat));
        }
        if(atkStat != 0){
//            System.out.println("Added attack.");
            player.getAttributes().addTransientAttributeModifiers(getAttackModifier(atkStat));
        }
        if(armStat != 0){
//            System.out.println("Added armor.");
            player.getAttributes().addTransientAttributeModifiers(getArmorModifier(armStat));
        }
        if(touStat != 0){
//            System.out.println("Added toughness.");
            player.getAttributes().addTransientAttributeModifiers(getToughnessModifier(touStat));
        }
        /*if(knockStat != 0){
//            System.out.println("Added knockback res.");
            player.getAttributes().addTransientAttributeModifiers(getKnockbackModifier(knockStat));
        }*/
        if(heal && maxHealthO < player.getMaxHealth()){
            player.heal(player.getMaxHealth() - maxHealthO);
        }
    }

    //Credit to the create mod
    private static Multimap<Attribute, AttributeModifier> getHealthModifier(float val){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(UUID.fromString("60377805-43e1-4c53-966b-1f279744716b"),
                        "potioneer health mod", val,

                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> hpMod = Suppliers.memoize(() ->
                ImmutableMultimap.of(Attributes.MAX_HEALTH, singleRangeAttributeModifier));
        return hpMod.get();
    }

    private static Multimap<Attribute, AttributeModifier> getAttackModifier(float val){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(UUID.fromString("f73a5318-f269-4a29-900a-51d10838c33c"),
                        "potioneer attack mod", val,

                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> attackMod = Suppliers.memoize(() ->
                ImmutableMultimap.of(Attributes.ATTACK_DAMAGE, singleRangeAttributeModifier));
        return attackMod.get();
    }

    private static Multimap<Attribute, AttributeModifier> getArmorModifier(float val){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(UUID.fromString("2d7b02cd-d93c-4abb-aa82-da8897bdd0c6"),
                        "potioneer armor mod", val,

                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> armorMod = Suppliers.memoize(() ->
                ImmutableMultimap.of(Attributes.ARMOR, singleRangeAttributeModifier));
        return armorMod.get();
    }

    private static Multimap<Attribute, AttributeModifier> getToughnessModifier(float val){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(UUID.fromString("5adc375c-e334-4eba-96a0-52bbc84b5b6c"),
                        "potioneer toughness mod", val,

                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> toughMod = Suppliers.memoize(() ->
                ImmutableMultimap.of(Attributes.ARMOR_TOUGHNESS, singleRangeAttributeModifier));
        return toughMod.get();
    }

    private static Multimap<Attribute, AttributeModifier> getKnockbackModifier(float val){
        AttributeModifier singleRangeAttributeModifier =
                new AttributeModifier(UUID.fromString("d6085650-f859-4600-8af6-357d76104b8c"),
                        "potioneer knockback mod", val,

                        AttributeModifier.Operation.ADDITION);
        Supplier<Multimap<Attribute, AttributeModifier>> knockbackMod = Suppliers.memoize(() ->
                ImmutableMultimap.of(Attributes.KNOCKBACK_RESISTANCE, singleRangeAttributeModifier));
        return knockbackMod.get();
    }

    public void copyFrom(BeyonderStats beyonderStats) {
        this.playerAttributes = beyonderStats.playerAttributes;
        this.mayFly = beyonderStats.mayFly;
        this.miningSpeedMult = beyonderStats.miningSpeedMult;
    }
}
