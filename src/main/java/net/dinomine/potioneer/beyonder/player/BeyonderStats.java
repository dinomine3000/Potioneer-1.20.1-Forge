package net.dinomine.potioneer.beyonder.player;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.beyonder.ModAttributes;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerMiningSpeedSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class BeyonderStats {

    /**
     * Enum defining all manageable stats, their order in Legacy int[]/float[] arrays,
     * unique Modifier UUIDs, and attribute mappings.
     */
    public enum StatType {
        HEALTH(0, "60377805-43e1-4c53-966b-1f279744716b", "potioneer health mod", () -> Attributes.MAX_HEALTH),
        DAMAGE(1, "f73a5318-f269-4a29-900a-51d10838c33c", "potioneer attack mod", () -> Attributes.ATTACK_DAMAGE),
        RESISTANCE(2, "5adc375c-e334-4eba-96a0-52bbc84b5b6c", "potioneer resistance mod", ModAttributes.RESISTANCE::get),
        STAMINA(3, "d6085650-f859-4600-8af6-357d76104b8c", "potioneer stamina mod", ModAttributes.STAMINA::get);

        private final int index;
        private final UUID modifierId;
        private final String modifierName;
        private final Supplier<Attribute> attributeSupplier;

        StatType(int index, String uuid, String name, Supplier<Attribute> attributeSupplier) {
            this.index = index;
            this.modifierId = UUID.fromString(uuid);
            this.modifierName = name;
            this.attributeSupplier = attributeSupplier;
        }

        public int getIndex() { return index; }
        public UUID getModifierId() { return modifierId; }
        public String getModifierName() { return modifierName; }
        public Attribute getAttribute() { return attributeSupplier.get(); }

        public static StatType byIndex(int index) {
            for (StatType type : values()) {
                if (type.index == index) return type;
            }
            return HEALTH;
        }
    }

    private float miningSpeedMult = 1;
    private boolean mayFly = false;
    private final Map<StatType, Float> stats = new EnumMap<>(StatType.class);
    private BeyonderStats tempEffectStats = null;

    BeyonderStats() {
        this(true);
    }

    BeyonderStats(boolean idk) {
        resetStats();
        if (idk) {
            tempEffectStats = new BeyonderStats(false);
        }
    }

    private BeyonderStats(BeyonderStats other) {
        for (StatType type : StatType.values()) {
            this.stats.put(type, other.stats.getOrDefault(type, 0f));
        }
    }

    // --- Stat Modifiers ---

    public void addDamage(float dmg) {
        addStat(StatType.DAMAGE, dmg);
    }

    public void addHealth(float i) {
        addStat(StatType.HEALTH, i);
    }

    public void addResistance(float i) {
        addStat(StatType.RESISTANCE, i);
    }

    public void addStamina(float i) {
        addStat(StatType.STAMINA, i);
    }

    /*public void addKnockbackRes(int i) {
        addStat(StatType.KNOCKBACK_RES, i);
    }*/

    public void addStat(StatType type, float amount) {
        this.stats.put(type, this.stats.getOrDefault(type, 0f) + amount);
    }
    public void setStat(StatType type, float amount) {
        this.stats.put(type, amount);
    }

    public float getStatValue(StatType type) {
        float base = stats.getOrDefault(type, 0f);
        float temp = tempEffectStats != null ? tempEffectStats.stats.getOrDefault(type, 0f) : 0f;
        return base + temp;
    }

    // --- General Abilities ---

    public void enableFlight() {
        mayFly = true;
    }

    public boolean canFly() {
        return mayFly;
    }

    public void setMiningSpeed(float mult) {
        this.miningSpeedMult = mult;
    }

    public void getMiningSpeed(PlayerEvent.BreakSpeed event) {
        event.setNewSpeed(event.getOriginalSpeed() * miningSpeedMult);
    }

    public float getMiningSpeed() {
        return miningSpeedMult;
    }

    public void multMiningSpeed(float mult) {
        this.miningSpeedMult *= mult;
    }

    private void updateClientIfMiningSpeedChanged(ServerPlayer player, float newSpeed) {
        if (player.level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new PlayerMiningSpeedSync(newSpeed), player);
    }

    public void resetStats() {
        miningSpeedMult = 1;
        mayFly = false;
        for (StatType type : StatType.values()) {
            stats.put(type, 0f);
        }
    }

    public void resetAndApplyStats(LivingEntity entity, boolean heal) {
        resetStats();
        if (entity instanceof Player player) applyStats(player, heal);
    }

    public void setEffects(BeyonderStats otherStats, LivingEntity target) {
        if (otherStats.miningSpeedMult != getMiningSpeed() && target instanceof ServerPlayer player) {
            updateClientIfMiningSpeedChanged(player, otherStats.miningSpeedMult);
        }
        this.miningSpeedMult = otherStats.miningSpeedMult;
        this.mayFly = otherStats.mayFly;
    }

    private boolean hasSameStatsAs(BeyonderStats otherStats) {
        for (StatType type : StatType.values()) {
            if (!this.stats.getOrDefault(type, 0f).equals(otherStats.stats.getOrDefault(type, 0f))) {
                return false;
            }
        }
        return true;
    }

    public void addStatsAndApplyIfChanged(BeyonderStats otherStats, Player target) {
        BeyonderStats other = new BeyonderStats(otherStats);
        if (other.hasSameStatsAs(tempEffectStats)) return;
        tempEffectStats = other;
        applyStats(target, false);
    }

    public void setAttributes(float[] atts) {
        for (int i = 0; i < atts.length && i < StatType.values().length; i++) {
            this.stats.put(StatType.byIndex(i), atts[i]);
        }
    }

    public void applyEffects(Player player, BeyonderStats statsHolder) {
        player.getAbilities().mayfly = player.isCreative() || player.isSpectator() || statsHolder.mayFly;
    }

    private float getStat(int idx) {
        return getStatValue(StatType.byIndex(idx));
    }

    // --- Attribute Application ---

    public void applyStats(LivingEntity player, boolean heal) {
        float maxHealthO = player.getMaxHealth();

        // Clear existing modifiers
        for (StatType type : StatType.values()) {
            player.getAttributes().removeAttributeModifiers(createModifierMap(type, 1f));
        }

        // Apply new non-zero stats
        for (StatType type : StatType.values()) {
            float val = getStatValue(type);
            if (val != 0f) {
                player.getAttributes().addTransientAttributeModifiers(createModifierMap(type, val));
            }
        }

        if (heal && maxHealthO < player.getMaxHealth()) {
            player.heal(player.getMaxHealth() - maxHealthO);
        }
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    /**
     * Helper to build dynamic attribute modifiers cleanly.
     */
    private static Multimap<Attribute, AttributeModifier> createModifierMap(StatType type, float val) {
        AttributeModifier modifier = new AttributeModifier(
                type.getModifierId(),
                type.getModifierName(),
                val,
                AttributeModifier.Operation.ADDITION
        );
        return ImmutableMultimap.of(type.getAttribute(), modifier);
    }

    public void copyFrom(BeyonderStats beyonderStats) {
        this.stats.clear();
        this.stats.putAll(beyonderStats.stats);
        this.mayFly = beyonderStats.mayFly;
        this.miningSpeedMult = beyonderStats.miningSpeedMult;
    }
}