package net.dinomine.potioneer.beyonder.effects.tyrant;

import com.google.common.collect.Multimap;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ScalesEffect extends BeyonderEffect {
    private static final UUID attributeId = UUID.fromString("301bfe67-b1c7-4add-9ae2-505d1be51ac6");
    private static final UUID armorId = UUID.fromString("302bfe17-c1b7-4add-9be2-505d1be51ac6");

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(sequenceLevel < 8){
            AbilityFunctionHelper.addAttributeTo(target, getKnockbackMod());
        }
        AbilityFunctionHelper.addAttributeTo(target, getArmorMod(getSequenceLevel()));
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        //cap.getEffectsManager().statsHolder.addArmor(4 + (9-getSequenceLevel())*2);
        if(WaterAffinityEffect.isInWater(target, sequenceLevel)){
            if(target.getHealth() < target.getMaxHealth()){
                int amplifier = (int)((10 - getSequenceLevel())/2f);
                if(!target.hasEffect(MobEffects.REGENERATION))
                    target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 25, amplifier, false, true, true));
                cap.requestPassiveSpiritualityCost(cost);
            }
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        AbilityFunctionHelper.removeAttribute(target, getKnockbackMod());
        AbilityFunctionHelper.removeAttribute(target, getArmorMod(sequenceLevel));
    }

    private static Multimap<Attribute, AttributeModifier> getKnockbackMod(){
        return AbilityFunctionHelper.getEntityModifier(Attributes.KNOCKBACK_RESISTANCE, attributeId, AttributeModifier.Operation.ADDITION, "scales_knockback", 1d);
    }

    private static Multimap<Attribute, AttributeModifier> getArmorMod(int level){
        return AbilityFunctionHelper.getEntityModifier(Attributes.ARMOR, armorId, AttributeModifier.Operation.ADDITION, "scales_armor", 4 + (9-level)*2);
    }
}
