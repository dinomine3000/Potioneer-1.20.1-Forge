package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.TyrantPathway;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class BeyonderWaterAffinityEffect extends BeyonderEffect {

    public BeyonderWaterAffinityEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        this.sequenceLevel = level;
        this.cost = cost;
        this.maxLife = time;
        this.ID = id;
        this.lifetime = 0;
        this.active = active;
        this.name = "Tyrant Affinity";
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(!target.hasEffect(MobEffects.WATER_BREATHING) || !target.hasEffect(MobEffects.NIGHT_VISION)){
            target.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, -1, 0, false, false));
            target.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false));
        }

        if(target instanceof Player player){
            float f = 1f;
            if(TyrantPathway.isInWater(player)){
                if (!target.onGround() && cap.getSequenceLevel() < 9) {
                    f *= 5.0F;
                }
                if (player.isUnderWater()) {
                    if(!EnchantmentHelper.hasAquaAffinity(player)){
                        f *= 5.0F;
                    }
                }
            }
            cap.getBeyonderStats().multMiningSpeed(f);
        }
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
        if(target.hasEffect(MobEffects.WATER_BREATHING)){
            target.removeEffect(MobEffects.WATER_BREATHING);
        }
        if(target.hasEffect(MobEffects.NIGHT_VISION)){
            target.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}
