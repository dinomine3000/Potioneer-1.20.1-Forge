package net.dinomine.potioneer.beyonder.effects.tyrant;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.TyrantPathway;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.jar.Attributes;

public class BeyonderWaterAffinityEffect extends BeyonderEffect {

    public BeyonderWaterAffinityEffect(){
        this(0, 0f, 0, false, BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY);
    }

    public BeyonderWaterAffinityEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Tyrant Affinity";
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(target instanceof Player player){
            if(TyrantPathway.isInWater(player)){
                float f = 1f;
                if(!player.hasEffect(MobEffects.WATER_BREATHING)){
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, -1, 0, false, false));
                }
                if(!player.hasEffect(MobEffects.NIGHT_VISION)){
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false));
                }
                if(!player.hasEffect(MobEffects.REGENERATION) && player.getHealth() < player.getMaxHealth()){
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, false, false));
                    cap.requestActiveSpiritualityCost(this.cost);
                }
                if(player.getFoodData().needsFood() && Math.random() < 0.01){
                    cap.requestActiveSpiritualityCost(this.cost/3);
                    player.getFoodData().eat(1, 1);
                }
                if (!target.onGround()) {
                    f *= 5.0F;
                }
                if (player.isUnderWater() && !EnchantmentHelper.hasAquaAffinity(player)) {
                    f *= 5.0F;
                }
                cap.getEffectsManager().statsHolder.multMiningSpeed(f);
            } else {
                stopEffects(cap, target);
            }
        }
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
        target.removeEffect(MobEffects.WATER_BREATHING);
        target.removeEffect(MobEffects.NIGHT_VISION);
    }
}
