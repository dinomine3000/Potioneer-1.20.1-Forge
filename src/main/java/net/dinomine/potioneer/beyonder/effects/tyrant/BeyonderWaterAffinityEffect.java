package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class BeyonderWaterAffinityEffect extends BeyonderEffect {

    private boolean levelUp;
    public BeyonderWaterAffinityEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        levelUp = sequenceLevel < 8;
        this.name = "Tyrant Affinity";
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            float f = 1f;
            if(player.isInWater() || (levelUp && target.level().isRaining())){
                if(player.isInWater() && !player.hasEffect(MobEffects.WATER_BREATHING)){
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, -1, 0, false, false));
                }
                if(!player.hasEffect(MobEffects.NIGHT_VISION)){
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false));
                }
                if(!player.hasEffect(MobEffects.REGENERATION) && player.getHealth() < player.getMaxHealth()){
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, false, false));
                    cap.requestActiveSpiritualityCost(this.cost/2f);
                }
                if(player.getFoodData().needsFood() && target.getRandom().nextFloat() <= 0.01){
                    cap.requestActiveSpiritualityCost(this.cost/4f);
                    player.getFoodData().eat(1, 1);
                }
                if (target.isInWater() && !target.onGround()) {
                    f *= 5.0F;
                }
                if (player.isUnderWater() && !EnchantmentHelper.hasAquaAffinity(player)) {
                    f *= 5.0F;
                }
                cap.getEffectsManager().statsHolder.multMiningSpeed(f);
                if(levelUp){
                    if(player.isInWater()){
                        cap.getEffectsManager().statsHolder.enableFlight();
                    }
                }
            } else {
                stopEffects(cap, target);
            }
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.removeEffect(MobEffects.WATER_BREATHING);
        target.removeEffect(MobEffects.NIGHT_VISION);
    }
}
