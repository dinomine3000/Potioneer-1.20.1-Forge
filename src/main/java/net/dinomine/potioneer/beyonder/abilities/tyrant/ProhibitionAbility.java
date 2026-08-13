package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AbilityProhibitionEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.GeneralProhibitionEffect;
import net.dinomine.potioneer.beyonder.pathways.TyrantPathway;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.util.misc.ModNbtUtils;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class ProhibitionAbility extends AbilityWithOptions {
    private static final Supplier<Integer> GENERAL_DURATION = PotioneerAbilityConfig.PROHIBITION_GENERAL_DURATION;
    public static final Supplier<Integer> PROHIBITION_RADIUS = PotioneerAbilityConfig.PROHIBITION_RADIUS;
    public static final Supplier<Integer> ABILITY_PROHIBITION_WINDOW = PotioneerAbilityConfig.PROHIBITION_ABILITY_WINDOW;
    public static final Supplier<Integer> ABILITY_PROHIBITION_DURATION = PotioneerAbilityConfig.PROHIBITION_ABILITY_DURATION;

    public ProhibitionAbility(int sequenceLevel) {
        super(sequenceLevel);
        defaultMaxCooldown = PotioneerAbilityConfig.PROHIBITION_COOLDOWN.get();
        withCost(PotioneerAbilityConfig.PROHIBITION_COST.get());

        AbilityOptions sOptions = new AbilityOptions()
                .addEmptyOption("flying", Component.literal("Flying"))
                .addEmptyOption("sprinting", Component.literal("Sprinting"))
                .addEmptyOption("fate", Component.literal("Fate"))
                .addEmptyOption("teleporting", Component.literal("Teleporting"))
                .addEmptyOption("artifact", Component.literal("Artifact Abilities"))
                .addEmptyOption("intrinsic", Component.literal("Extra Abilities"));
        setSecondaryOptions(sOptions);
    }

    public void clearEffectForEveryone(Level level, LivingEntity caster){
        CompoundTag dataTag = getData();
        List<Integer> ids = ModNbtUtils.fromIntListTag(dataTag.getList("ids", Tag.TAG_INT));
        for(int id: ids){
            if(level.getEntity(id) instanceof LivingEntity ent){
                ent.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
                    cap.getEffectsManager().removeEffect(BeyonderEffects.TYRANT_ABILITY_PROHIBITION.getEffectId());
                });
            }
        }
        setData(new CompoundTag(), caster);
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide) return true;
        List<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesAround(target, PROHIBITION_RADIUS.get());
        for(LivingEntity hit: hits){
            AbilityProhibitionEffect eff = AbilityProhibitionEffect.createInstance(0, target.getUUID());
            hit.getCapability(CapProvider.BEYONDER_STATS).ifPresent(hitCap -> {
                hitCap.getEffectsManager().addOrRefreshEffect(eff, hitCap, hit);
            });
        }
        ListTag idsTag = ModNbtUtils.toNumberListTag(hits.stream().map(Entity::getId).toList());
        CompoundTag fullTag = new CompoundTag();
        fullTag.put("ids", idsTag);
        setData(fullTag, target);
        return true;
    }

    @Override
    protected boolean secondaryWithArgument(BeyonderCapability cap, LivingEntity target, String args) {
        //apply other prohibition types
        //prohibit flying, teleporting, fate manipulation, artifacts, non-intrinsic abilities, sprinting
        if(target.level().isClientSide()) return true;
        List<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesAround(target, PROHIBITION_RADIUS.get());
        for(LivingEntity ent: hits){
            ent.getCapability(CapProvider.BEYONDER_STATS).ifPresent(entCap -> {
                GeneralProhibitionEffect eff = (GeneralProhibitionEffect) BeyonderEffects.TYRANT_GENERAL_PROHIBITION.createInstance(sequenceLevel, 0, GENERAL_DURATION.get(), true);
                eff.type = args;
                entCap.getEffectsManager().addOrReplaceEffect(eff, entCap, ent);
            });
        }
        if(!hits.isEmpty()) cap.getCharacteristicManager().progressActing(TyrantPathway.TRIBUNAL_ACTING_PROHIBITION, 15);
        return false;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "prohibition";
    }
}
