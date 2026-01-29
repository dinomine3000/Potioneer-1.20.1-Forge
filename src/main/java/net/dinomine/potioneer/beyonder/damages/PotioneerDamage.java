package net.dinomine.potioneer.beyonder.damages;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * i did all the damage type / damage sources thanks to the open source code of the Draconic Evolution mod.
 * I do not claim to have made this by myself. and by this i mean anything to do with damage source/damage type and damage tags creation or alteration.
 */
public class PotioneerDamage {
    private static Map<ResourceKey<DamageType>, DamageSource> SOURCES = new HashMap<>();

    public static final ResourceKey<DamageType> CRIT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "crit"));
    public static final ResourceKey<DamageType> CHRYON_PIERCE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "chryon_pierce"));
    public static final ResourceKey<DamageType> LOW_SANITY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "low_sanity"));
    public static final ResourceKey<DamageType> LOW_SANITY_KILL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "low_sanity_kill"));
    public static final ResourceKey<DamageType> LOW_SPIRITUALITY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "low_spirituality"));
    public static final ResourceKey<DamageType> ASTEROID = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "asteroid"));
//    public static final ResourceKey<DamageType> MENTAL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "mental"));
//    public static final ResourceKey<DamageType> ANNIHILATION = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "annihilation"));

    public static DamageSource crit(ServerLevel level, LivingEntity attacker) {
        return getSource(level, CRIT, attacker);
    }

    public static DamageSource crit(ServerLevel level) {
        return getSource(level, CRIT);
    }

    public static DamageSource asteroid(ServerLevel level) {
        return getSource(level, ASTEROID);
    }

    public static DamageSource chryon_pierce(ServerLevel level, LivingEntity attacker) {
        return getSource(level, CHRYON_PIERCE, attacker);
    }

    public static DamageSource low_spirituality(ServerLevel level) {
        return getSource(level, LOW_SPIRITUALITY);
    }

    public static DamageSource low_sanity(ServerLevel level) {
        return getSource(level, LOW_SANITY);
    }

    public static DamageSource low_sanity_kill(ServerLevel level) {
        return getSource(level, LOW_SANITY_KILL);
    }

    private static DamageSource getSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), attacker);
    }

    private static DamageSource getSource(Level level, ResourceKey<DamageType> type, @Nullable Entity projectile, @Nullable Entity owner) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), projectile, owner);
    }


    private static DamageSource getSource(Level level, ResourceKey<DamageType> type) {
        return SOURCES.computeIfAbsent(type, e -> new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(e)));
    }


    public static class Tags {
        public static final TagKey<DamageType> ABSOLUTE = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "absolute"));
        public static final TagKey<DamageType> MENTAL = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "mental"));
        public static final TagKey<DamageType> ANNIHILATION = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "annihilation"));
    }
}
