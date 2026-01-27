package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.CooldownAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeyonderCooldownEffect extends BeyonderEffect {

    private boolean isDefensive;
    public boolean disabledFlag = false;
    private int minCooldown, maxCooldown;

    public BeyonderCooldownEffect withValues(boolean isDefensive, int minCooldown, int maxCooldown){
        this.isDefensive = isDefensive;
        this.minCooldown = minCooldown;
        this.maxCooldown = maxCooldown;
        return this;
    }

    public BeyonderCooldownEffect withValues(boolean isDefensive){
        return withValues(isDefensive, 0, 1);
    }

    public boolean isDefensive(){
        return this.isDefensive;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(!isDefensive)
            disableAbilities(cap, target, maxLife, minCooldown, maxCooldown);
    }

    /**
     * puts abilities on cooldown
     * @param cap
     * @param target
     * @param effectCooldown - the cooldown for applying this effect again, in ticks.
     *                       for this time after the method is called, no one of the same level as this effect can put abilities on cooldown.
     */
    public void disableAbilities(LivingEntityBeyonderCapability cap, LivingEntity target, int effectCooldown, int minCooldown, int maxCooldown){
        if(disabledFlag) return;
        CooldownAbility.disableRandomAbilities(cap, cap.getLuckManager(), target, false, minCooldown, maxCooldown);
        disabledFlag = true;
        this.lifetime = 0;
        this.maxLife = effectCooldown;
    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, LivingEntity attacker,
                                LivingEntityBeyonderCapability victimCap, Optional<LivingEntityBeyonderCapability> optAttackerCap) {
        if(attacker == null || optAttackerCap.isEmpty()) return false;
        if(victim.level().isClientSide()) return false;

        LivingEntityBeyonderCapability attackerCap = optAttackerCap.get();

        if(isDefensive()){
            if(!attackerCap.getLuckManager().passesLuckCheck(1/2f, 0, 0, attacker.getRandom())){
                if(attackerCap.getEffectsManager().hasEffectOrBetter(this)){
                    BeyonderCooldownEffect eff = (BeyonderCooldownEffect) attackerCap.getEffectsManager().getEffect(effectId);
                    if(eff == null) return false;
                    eff.disableAbilities(attackerCap, attacker, 20*5, CooldownAbility.minDefensiveCooldown, CooldownAbility.maxDefensiveCooldown);
                } else {
                    attackerCap.getEffectsManager().addEffectNoRefresh(CooldownAbility.createEffectInstance(
                                    getSequenceLevel(), false, CooldownAbility.minDefensiveCooldown, CooldownAbility.maxDefensiveCooldown,20*5),
                            attackerCap, attacker);
                }
            }
        }

        return false;
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
        nbt.putBoolean("defensive", isDefensive);
        nbt.putInt("minCooldown", minCooldown);
        nbt.putInt("maxCooldown", maxCooldown);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.isDefensive = nbt.getBoolean("defensive");
        this.disabledFlag = nbt.getBoolean("flag");
        this.minCooldown = nbt.getInt("minCooldown");
        this.maxCooldown = nbt.getInt("maxCooldown");
    }
}
