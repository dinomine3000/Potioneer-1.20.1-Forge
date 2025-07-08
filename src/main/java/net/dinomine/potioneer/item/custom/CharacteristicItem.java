package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CharacteristicItem extends Item {
    public CharacteristicItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(pContext.getPlayer() == null) return InteractionResult.FAIL;
        pContext.getLevel().playSound(pContext.getPlayer(), pContext.getClickedPos(), SoundEvents.STONE_PLACE, SoundSource.PLAYERS);
        if(pContext.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        CharacteristicEntity entity = new CharacteristicEntity(ModEntities.CHARACTERISTIC.get(), pContext.getLevel(), pContext.getItemInHand().copy(), -1);
        Vec3 pos = pContext.getClickedPos().relative(pContext.getClickedFace()).getCenter().add(0, -0.5f, 0);

        if(pContext.getItemInHand().hasTag() && pContext.getItemInHand().getTag().contains("beyonder_info")){
            entity.setSequenceId(pContext.getItemInHand().getTag().getCompound("beyonder_info").getInt("id"));
        } else {
            entity.setSequenceId(-1);
        }

        entity.moveTo(pos.x, pos.y, pos.z, pContext.getRotation(), 0);
        pContext.getLevel().addFreshEntity(entity);
        if(!pContext.getPlayer().isCreative()){
            pContext.getPlayer().setItemInHand(pContext.getHand(), ItemStack.EMPTY);
        }
        return InteractionResult.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    public static class CharacteristicTint implements ItemColor {

        @Override
        public int getColor(ItemStack itemStack, int i) {
            int seq = -1;
            if(i != 1) seq = -1;
            if(itemStack.hasTag()){
                if(!itemStack.getTag().getCompound("beyonder_info").isEmpty()){
                    seq = itemStack.getTag().getCompound("beyonder_info").getInt("id");
                }
            }
            return Beyonder.getSequenceColorFromId(seq);
        }
    }
}
