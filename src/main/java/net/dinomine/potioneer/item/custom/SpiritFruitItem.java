package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpiritFruitItem extends Item {

    public SpiritFruitItem(Properties pProperties) {
        super(pProperties);
    }

    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        ItemStack itemstack = super.finishUsingItem(pStack, pLevel, pEntityLiving);
        if(!pLevel.isClientSide()){
            pEntityLiving.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                int maxSpir = cap.getMaxSpirituality();
                cap.requestActiveSpiritualityCost(-1*maxSpir*0.1f);
                PlayerLuckManager lck = cap.getLuckManager();
                RandomSource rnd = pEntityLiving.getRandom();
                if(!lck.passesLuckCheck(0.3f, 1, 0, rnd)){
                    pEntityLiving.addEffect(new MobEffectInstance(MobEffects.GLOWING, rnd.nextInt(10)*20, rnd.nextInt(3)));
                }
                if(!lck.passesLuckCheck(0.8f, 1, 0, rnd)){
                    pEntityLiving.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, rnd.nextInt(10)*20, rnd.nextInt(3)));
                }
                if(!lck.passesLuckCheck(0.8f, 1, 0, rnd)){
                    pEntityLiving.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, rnd.nextInt(10)*20, rnd.nextInt(3)));
                }
            });
        }
        return itemstack;
    }
}
