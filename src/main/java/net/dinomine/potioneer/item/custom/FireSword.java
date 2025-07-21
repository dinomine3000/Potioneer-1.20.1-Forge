package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

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
        Optional<LivingEntityBeyonderCapability> cap = pContext.getPlayer().getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        if (hitResult instanceof BlockHitResult blockHitResult
                && !pContext.getLevel().getBlockState(blockHitResult.getBlockPos()).is(Blocks.FIRE)
                && cap.get().getSpirituality() > 5){
            Player player = pContext.getPlayer();
            ItemStack item = pContext.getItemInHand();
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            UseOnContext newContext = new UseOnContext(pContext.getLevel(), player, pContext.getHand(), ItemStack.EMPTY, blockHit);
            InteractionResult res = Items.FLINT_AND_STEEL.useOn(newContext);
            if(res != InteractionResult.FAIL){

                pContext.getItemInHand().hurtAndBreak(5, player, (p_41300_) -> {
                    p_41300_.broadcastBreakEvent(pContext.getHand());
                });

                cap.get().requestActiveSpiritualityCost(5f);
                MysticismHelper.updateOrApplyMysticismTag(item, 5f, player);
            }
            return res;
        }
        return super.useOn(pContext);
    }
}
