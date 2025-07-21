package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.custom.RitualInk;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public class InkBottleItem extends Item {
    public InkBottleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(RitualInk.canBePlacedOn(
                pContext.getLevel(),
                pContext.getClickedPos().relative(pContext.getClickedFace()).below(),
                pContext.getLevel().getBlockState(pContext.getClickedPos().relative(pContext.getClickedFace()).below())))
        {
            pContext.getLevel().setBlock(pContext.getClickedPos().relative(pContext.getClickedFace()),
                    ModBlocks.RITUAL_INK.get().getStateForPlacement(new BlockPlaceContext(pContext)),
                    Block.UPDATE_ALL_IMMEDIATE);
            pContext.getLevel().playSound(pContext.getPlayer(), pContext.getClickedPos(), SoundEvents.STONE_PLACE, SoundSource.PLAYERS);

            if(pContext.getPlayer() !=null)
                pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(), (p_41300_) -> {
                    p_41300_.broadcastBreakEvent(pContext.getHand());
                });
            return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }
}
