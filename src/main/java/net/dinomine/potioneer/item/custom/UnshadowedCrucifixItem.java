package net.dinomine.potioneer.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UnshadowedCrucifixItem extends Item {
    public static final String CRUCIFIX_TAG_ID = "crucifix_data";
    public enum CrucifixState {
        ENABLED(0), ENABLING(1), DISABLED(2);
        public final int id;
        CrucifixState(int id) { this.id = id; }
    }

    public UnshadowedCrucifixItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pLevel.isClientSide()) return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));
        CompoundTag tag = pPlayer.getItemInHand(pUsedHand).getOrCreateTag();
        if(tag.contains(CRUCIFIX_TAG_ID)){
            CompoundTag crucifixData = tag.getCompound(CRUCIFIX_TAG_ID);
            if(crucifixData.getInt("state")  == CrucifixState.ENABLED.id){
                crucifixData.putInt("state", CrucifixState.DISABLED.id);
                pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1, 1.3f - pPlayer.getRandom().nextFloat()*0.6f);
            }
            else if(crucifixData.getInt("state")  == CrucifixState.DISABLED.id){
                crucifixData.putInt("state", CrucifixState.ENABLED.id);
                pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1, 1.3f - pPlayer.getRandom().nextFloat()*0.6f);
            }
        } else {
            CompoundTag newTag = new CompoundTag();
            newTag.putInt("state", CrucifixState.ENABLED.id);
            tag.put(CRUCIFIX_TAG_ID, newTag);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
