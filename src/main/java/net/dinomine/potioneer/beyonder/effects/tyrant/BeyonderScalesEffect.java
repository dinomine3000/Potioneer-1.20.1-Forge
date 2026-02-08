package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class BeyonderScalesEffect extends BeyonderEffect {
    private static final UUID attributeId = UUID.fromString("301bfe67-b1c7-4add-9ae2-505d1be51ac6");

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            AbilityFunctionHelper.addAttributeTo(player, attributeId, "scales armor", 4 + (9-getSequenceLevel())*2, AttributeModifier.Operation.ADDITION, Attributes.ARMOR);
        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(target.isInWater()){
            if(target.getHealth() < target.getMaxHealth()){
                int amplifier = (int)((10 - getSequenceLevel())/2f);
                target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 22, amplifier, false, true, true));
                cap.requestPassiveSpiritualityCost(cost);
            }
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            AbilityFunctionHelper.removeAttribute(player, attributeId, "scales armor", 4 + (9-getSequenceLevel())*2, AttributeModifier.Operation.ADDITION, Attributes.ARMOR);
        }
    }
}
