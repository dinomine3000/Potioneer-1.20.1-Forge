package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderStats;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class FireSword extends SwordItem {
    public FireSword(Properties pProperties) {
        super(Tiers.IRON, 3, 1.2f, pProperties.durability(256));
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if(!(pAttacker instanceof Player player)) return super.hurtEnemy(pStack, pTarget, pAttacker);
        MysticismHelper.updateOrApplyMysticismTag(pStack, 1f, player);
        pTarget.setSecondsOnFire(2);
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        HitResult hitResult = pContext.getPlayer().pick(5.0D, 0.0F, false);
        if (hitResult.getType() == HitResult.Type.BLOCK){
            Player player = pContext.getPlayer();
            ItemStack item = pContext.getItemInHand();
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            UseOnContext newContext = new UseOnContext(pContext.getLevel(), player, pContext.getHand(), ItemStack.EMPTY, blockHit);
            Items.FLINT_AND_STEEL.useOn(newContext);
            pContext.getItemInHand().setDamageValue(item.getDamageValue() + 5);

            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.requestActiveSpiritualityCost(5f);
            });
            MysticismHelper.updateOrApplyMysticismTag(item, 5f, player);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }
}
