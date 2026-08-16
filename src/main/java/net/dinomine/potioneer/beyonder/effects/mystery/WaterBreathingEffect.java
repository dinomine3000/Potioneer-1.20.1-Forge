package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class WaterBreathingEffect extends BeyonderEffect {
    private boolean waterFlag = false;

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {}

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            if(isInWater(player, sequenceLevel)){
                if(!player.hasEffect(MobEffects.WATER_BREATHING)){
                    this.waterFlag = true;
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, maxLife - lifetime, 0, false, false));
                }
            } else {
                if(waterFlag){
                    target.removeEffect(MobEffects.WATER_BREATHING);
                    waterFlag = false;
                }
            }
        }
    }

    public static boolean isInWater(LivingEntity player, int level){
        return player.isInWater() || (player.level().isRaining() && level < 7);
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("waterFlag", waterFlag);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.waterFlag = nbt.getBoolean("waterFlag");
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        if(waterFlag){
            target.removeEffect(MobEffects.WATER_BREATHING);
            waterFlag = false;
        }
    }
}
