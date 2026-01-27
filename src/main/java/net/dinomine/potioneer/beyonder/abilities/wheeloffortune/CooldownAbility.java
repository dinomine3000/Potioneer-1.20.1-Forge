package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderCooldownEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderMiningSpeedEffect;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

public class CooldownAbility extends PassiveAbility {
    private static final int effectRadius = 16;
    private static final int minCooldown = 5*20;
    private static final int maxCooldown = 120*20;
    public static final int minDefensiveCooldown = 5*20;
    public static final int maxDefensiveCooldown = 60*20;
    public CooldownAbility(int sequence){
        super(sequence, BeyonderEffects.WHEEL_COOLDOWN, level -> "aoe_cooldown");
        enabledOnAcquire();
        canFlip();
        this.defaultMaxCooldown = 20*20;
    }

    @Override
    protected BeyonderEffect createEffectInstance(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return createEffectInstance(getSequenceLevel(), true, 0, 1, -1);
    }

    public static BeyonderEffect createEffectInstance(int sequenceLevel, boolean isDefensive, int minCooldown, int maxCooldown, int durationTicks){
        BeyonderCooldownEffect eff = (BeyonderCooldownEffect) BeyonderEffects.WHEEL_COOLDOWN.createInstance(sequenceLevel, 0, durationTicks, true);
        eff.withValues(isDefensive, minCooldown, maxCooldown);
        return eff;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.requestActiveSpiritualityCost(cost());
        if(target.level().isClientSide()){
            ParticleMaker.particleExplosionGrid(target.level(), effectRadius, target.position().x, target.position().y, target.position().z);
            return true;
        }
        PacketHandler.sendMessageToClientsAround(target, effectRadius*2, new GeneralAreaEffectMessage(target.getOnPos(), effectRadius));
        List<LivingEntity> victims = AbilityFunctionHelper.getLivingEntitiesAround(target, effectRadius);
        for(LivingEntity ent: victims){
            if(!PotioneerCommonConfig.COOLDOWN_TARGET_ALLIES.get() && ent instanceof Player playerVictim && target instanceof Player playerCaster && playerVictim != playerCaster){
                if(AllySystemSaveData.from((ServerLevel) target.level()).isPlayerAllyOf(playerVictim.getUUID(), playerCaster.getUUID())) continue;
            }
            ent.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(victimCap -> {
                PlayerLuckManager proxyManager = new PlayerLuckManager(cap.getLuckManager().getLuck() - victimCap.getLuckManager().getLuck());
                if(ent.getId() == target.getId()) proxyManager = cap.getLuckManager();
                disableRandomAbilities(victimCap, proxyManager, ent, true);
//                if(!victimCap.getEffectsManager().hasEffectOrBetter(effect.getEffectId(), getSequenceLevel())){
//                    //if the victim doesnt have an effect of this level or lower -> just apply the effect
//                    victimCap.getEffectsManager().addEffectNoRefresh(createEffectInstance(false, minCooldown, maxCooldown, effectLifetime), victimCap, ent);
//                } else {
//                    //the target has the effect -> get it and just disable abilities.
//                    BeyonderCooldownEffect cdEffect = (BeyonderCooldownEffect) victimCap.getEffectsManager().getEffect(effect.getEffectId(), getSequenceLevel());
//                    if(cdEffect == null) return; // effect found is null -> means they have this effect but its of a higher level, so we dont do anything.
//                    //this could be because its a WoF beyonder of a higher level, in which case they resist it, or someone on the WoF pathway already applied a similar effect
//                    cdEffect.disableAbilities(victimCap, ent, effectLifetime, minCooldown, maxCooldown);
//                }
            });
        }
        return true;
    }

    public static void disableRandomAbilities(LivingEntityBeyonderCapability victimCapability, PlayerLuckManager luck, LivingEntity victim, boolean casterPespective){
        disableRandomAbilities(victimCapability, luck, victim, casterPespective, minCooldown, maxCooldown);
    }

    public static void disableRandomAbilities(LivingEntityBeyonderCapability victimCapability, PlayerLuckManager luck, LivingEntity victim, boolean casterPespective, int minCooldown, int maxCooldown){
        int numToDisable = luck.getRandomNumber(0, 4, casterPespective, victim.getRandom());
        List<AbilityKey> keys = new ArrayList<>(victimCapability.getAbilitiesManager().getAbilityKeys());
        if(keys.isEmpty()) return;
        if(numToDisable > 0) WheelOfFortunePathway.playSound(victim.level(), victim.getOnPos(), WheelOfFortunePathway.UNLUCK);
        for(int i = 0; i < numToDisable; i++){
            if(keys.isEmpty()) break;
            victimCapability.getLuckManager().grantLuck(10);
            //bigger is better here because, generally, the last abilities in the list are the higher level sequence ones.
            AbilityKey key = keys.get(luck.getRandomNumber(0, keys.size(), casterPespective, victim.getRandom()));
            victim.sendSystemMessage(Component.translatableWithFallback("ability.potioneer.cooldown_put", "%s has been put on cooldown.", key.getNameComponent()));
            victimCapability.getAbilitiesManager().putAbilityOnCooldown(key, luck.getRandomNumber(minCooldown, maxCooldown, casterPespective, victim.getRandom()), victim);
            keys.remove(key);
        }
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return super.primary(cap, target);
    }
}
