package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.DivinationRodEntity;
import net.dinomine.potioneer.entities.custom.PlaceableItemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public abstract class DivinationRodItem extends SwordItem {

    public DivinationRodItem(Properties pProperties, int dmg, float spd, Tier tier) {
        super(tier, dmg, spd, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        pContext.getLevel().playSound(pContext.getPlayer(), pContext.getClickedPos(), SoundEvents.METAL_PLACE, SoundSource.PLAYERS);
        if(pContext.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        DivinationRodEntity entity = new DivinationRodEntity(ModEntities.DIVINATION_ROD.get(), pContext.getLevel(), pContext.getItemInHand().copy());
        Vec3 pos = pContext.getClickedPos().relative(pContext.getClickedFace()).getCenter().add(0, -0.5f, 0);

        entity.moveTo(pos.x, pos.y, pos.z, pContext.getRotation(), 0);
        pContext.getLevel().addFreshEntity(entity);
        if(!pContext.getPlayer().isCreative()){
            pContext.getPlayer().setItemInHand(pContext.getHand(), ItemStack.EMPTY);
        }
        return InteractionResult.SUCCESS;
    }
}
