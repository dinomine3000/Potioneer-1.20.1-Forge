package net.dinomine.potioneer.beyonder.effects.tyrant;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;
import java.util.function.Supplier;

public class BeyonderWaterAffinityEffect extends BeyonderEffect {
    private static final UUID attributeId = UUID.fromString("9716a637-f0c7-41fa-9852-918df4567a91");
    private boolean levelUp;

    private boolean nightFlag = false, waterFlag = false;

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        levelUp = sequenceLevel < 8;
        if(target instanceof Player player){
            AbilityFunctionHelper.addAttributeTo(player, attributeId, "swim speed affinity modifier",
                    (int)((sequenceLevel-8.7-8.5)*(sequenceLevel-3.6-8.5)*0.08), AttributeModifier.Operation.MULTIPLY_BASE, ForgeMod.SWIM_SPEED.get());
        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            float f = 1f;
            if(player.isInWater()){
                if(!player.hasEffect(MobEffects.WATER_BREATHING)){
                    this.waterFlag = true;
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, -1, 0, false, false));
                }
                if(!player.hasEffect(MobEffects.NIGHT_VISION)){
                    this.nightFlag = true;
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false));
                }
//                if(!player.hasEffect(MobEffects.CONDUIT_POWER)){
//                    player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, -1, 0, false, false));
//                }
//                if(!player.hasEffect(MobEffects.REGENERATION) && player.getHealth() < player.getMaxHealth()){
//                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, false, false));
//                    cap.requestActiveSpiritualityCost(this.cost/2f);
//                }
                if(player.getFoodData().needsFood() && target.getRandom().nextFloat() <= 0.01){
                    player.getFoodData().eat(1, 2);
                }
                if (sequenceLevel < 9 && target.isInWater() && !target.onGround()) {
                    f *= 5.0F;
                }
                if (player.isUnderWater() && !EnchantmentHelper.hasAquaAffinity(player)) {
                    f *= 5.0F;
                }
                cap.getEffectsManager().statsHolder.multMiningSpeed(f);
            } else {
                if(waterFlag){
                    target.removeEffect(MobEffects.WATER_BREATHING);
                    waterFlag = false;
                }
                if(nightFlag){
                    target.removeEffect(MobEffects.NIGHT_VISION);
                    nightFlag = false;
                }
            }
        }
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("nightFlag", nightFlag);
        nbt.putBoolean("waterFlag", waterFlag);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.nightFlag = nbt.getBoolean("nightFlag");
        this.waterFlag = nbt.getBoolean("waterFlag");
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(waterFlag){
            target.removeEffect(MobEffects.WATER_BREATHING);
            waterFlag = false;
        }
        if(nightFlag){
            target.removeEffect(MobEffects.NIGHT_VISION);
            nightFlag = false;
        }
        if(target instanceof Player player){
            AbilityFunctionHelper.removeAttribute(player, attributeId, "swim speed affinity modifier",
                    (int)((sequenceLevel-8.7-8.5)*(sequenceLevel-3.6-8.5)*0.08), AttributeModifier.Operation.MULTIPLY_BASE, ForgeMod.SWIM_SPEED.get());
        }
    }
}
