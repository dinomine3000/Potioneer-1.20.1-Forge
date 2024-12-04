package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.util.ModTags;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public class MetalDetectorItem extends Item {
    public MetalDetectorItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide){
            BlockPos position = pContext.getClickedPos();
            Player player = pContext.getPlayer();
            boolean found = false;
            for (int i = 0; i <= position.getY(); i++) {
                BlockState state = pContext.getLevel().getBlockState(position.below(i));
                if(state.is(ModTags.Blocks.METAL_DETECTOR_VALUABLES)){
                    outputCoords(position.below(i), player, state.getBlock());
                    found = true;
                    break;
                }
            }
            if(!found){
                player.sendSystemMessage(Component.literal("Nothing happens..."));
            }

        }
        pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(), player -> player.broadcastBreakEvent(player.getUsedItemHand()));
        return InteractionResult.SUCCESS;
    }

    private void outputCoords(BlockPos pos, Player p, Block block){
        p.sendSystemMessage(Component.literal("Found " + I18n.get(block.getDescriptionId()) + " at " +
                pos.getY()));
    }
}
