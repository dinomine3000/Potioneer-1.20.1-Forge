package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class CooldownRecipientEffect extends BeyonderEffect {

    public boolean disabledFlag = false;
    private int minCooldown = 20*20, maxCooldown = 20*60;

    public void withValues(int minCooldown, int maxCooldown){
        this.minCooldown = minCooldown;
        this.maxCooldown = maxCooldown;
    }

    @Override
    public boolean canAdd(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_COOLDOWN_DEFENCE.getEffectId())){
            if(cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_COOLDOWN_DEFENCE.getEffectId()).getSequenceLevel() < getSequenceLevel()){
                endEffectWhenPossible();
                return false;
            }
        }
        return super.canAdd(cap, target);
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(target instanceof ServerPlayer player && player.connection == null) return;
        disableAbilities(cap, target, maxLife, minCooldown, maxCooldown);
        if(PotioneerCommonConfig.COOLDOWN_EFFECT_STACKS.get()){
           endEffectWhenPossible();
        }
    }

    @Override
    public void refreshTime(LivingEntityBeyonderCapability cap, LivingEntity target, BeyonderEffect effect) {
        if(!(effect instanceof CooldownRecipientEffect cdEffect))
            super.refreshTime(cap, target, effect);
        else
            disableAbilities(cap, target, cdEffect.maxLife, cdEffect.minCooldown, cdEffect.maxCooldown);
    }

    /**
     * puts abilities on cooldown
     * @param cap
     * @param target
     * @param effectCooldown - the cooldown for applying this effect again, in ticks.
     *                       for this time after the method is called, no one of the same level as this effect can put abilities on cooldown.
     */
    private void disableAbilities(LivingEntityBeyonderCapability cap, LivingEntity target, int effectCooldown, int minCooldown, int maxCooldown){
        if(!PotioneerCommonConfig.COOLDOWN_EFFECT_STACKS.get() && disabledFlag) return;
        disableRandomAbilities(cap, cap.getLuckManager(), target, false, minCooldown, maxCooldown);
        disabledFlag = true;
        this.lifetime = 0;
        this.maxLife = effectCooldown;
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
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("flag", disabledFlag);
        nbt.putInt("minCooldown", minCooldown);
        nbt.putInt("maxCooldown", maxCooldown);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.disabledFlag = nbt.getBoolean("flag");
        this.minCooldown = nbt.getInt("minCooldown");
        this.maxCooldown = nbt.getInt("maxCooldown");
    }
}
