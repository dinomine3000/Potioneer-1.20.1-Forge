package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.CooldownRecipientEffect;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class CooldownAbility extends PassiveAbility {
    private static final int effectRadius = 16;
    private static final int minCooldown = 5*20;
    private static final int maxCooldown = 120*20;
    public CooldownAbility(int sequence){
        super(sequence, BeyonderEffects.WHEEL_COOLDOWN_DEFENCE, level -> "aoe_cooldown");
        enabledOnAcquire();
        canFlip();
        this.defaultMaxCooldown = 20*20;
    }

    @Override
    protected BeyonderEffect createEffectInstance(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return effect.createInstance(sequenceLevel, 0, -1, true);
    }

    public static BeyonderEffect createCooldownEffectInstance(int sequenceLevel, int minCooldown, int maxCooldown, int durationTicks){
        CooldownRecipientEffect eff = (CooldownRecipientEffect) BeyonderEffects.WHEEL_COOLDOWN.createInstance(sequenceLevel, 0, durationTicks, true);
        eff.withValues(minCooldown, maxCooldown);
        return eff;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.requestActiveSpiritualityCost(cost());
        ParticleMaker.summonAOEParticles(target.level(), target.position(), 2*effectRadius, effectRadius, ParticleMaker.Preset.AOE_END_ROD);
        List<LivingEntity> victims = AbilityFunctionHelper.getLivingEntitiesAround(target, effectRadius);
        for(LivingEntity ent: victims){
            if(!PotioneerCommonConfig.COOLDOWN_TARGET_ALLIES.get() && ent instanceof Player playerVictim && target instanceof Player playerCaster && playerVictim != playerCaster){
                if(AllySystemSaveData.from((ServerLevel) target.level()).isPlayerAllyOf(playerVictim.getUUID(), playerCaster.getUUID())) continue;
            }
            ent.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(victimCap -> {
                PlayerLuckManager proxyManager = new PlayerLuckManager(cap.getLuckManager().getLuck() - victimCap.getLuckManager().getLuck());
                if(ent.getId() == target.getId()) proxyManager = cap.getLuckManager();
                else cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.GAMBLER_ACTING_COOLDOWN, 7);
                if(PotioneerCommonConfig.COOLDOWN_ABILITY_CAST_COOLDOWN.get()){
                    victimCap.getEffectsManager().addOrReplaceEffect(createCooldownEffectInstance(getSequenceLevel(), minCooldown, maxCooldown, 20*20),
                            victimCap, ent);
                } else {
                    CooldownRecipientEffect.disableRandomAbilities(victimCap, proxyManager, ent, ent.getId() != target.getId(), minCooldown, maxCooldown);
                }
            });
        }
        return true;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        super.primary(cap, target);
        putOnCooldown(20, target);
        if(!target.level().isClientSide())
            target.sendSystemMessage(Component.translatable("ability.potioneer.cooldowns_" + (isEnabled() ? "enabled":"disabled")));
        return false;
    }
}
