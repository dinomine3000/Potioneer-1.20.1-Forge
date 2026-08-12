package net.dinomine.potioneer.beyonder.downsides.tyrant;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.util.ParticleMaker;
import net.dinomine.potioneer.util.misc.ModNbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public class MistDownside extends Downside {

    public MistDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(target instanceof Player player){
            Iterator<ItemStack> iterator = player.getInventory().items.iterator();
            CompoundTag dataTag = getData();
            ItemStack oldItem;
            if(dataTag.contains("item")){
                oldItem = ItemStack.of(dataTag.getCompound("item"));
            } else oldItem = ItemStack.EMPTY;
            boolean flag = false;
            while(iterator.hasNext()){
                ItemStack stack = iterator.next();
                if(ModNbtUtils.ArtifactInfoTag.isItemArtifact(stack)) continue;
                if(stack.isEmpty()) continue;
                PacketHandler.sendMessageToClientsAround(target, 6, new GeneralAreaEffectMessage(ParticleMaker.Preset.SMALL_MIST, target.getEyePosition().toVector3f(), 1));
                target.level().playSound(null, target.getOnPos(), SoundEvents.BUCKET_FILL, SoundSource.PLAYERS);
                ItemStack copy = stack.copy();
                CompoundTag itemTag = copy.save(new CompoundTag());
                stack.setCount(0);
                dataTag.put("item", itemTag);
                if(!player.addItem(oldItem)) player.drop(oldItem, true, false);
                flag = true;
                break;
            }
            if(flag) setData(dataTag, target);
            return true;
        }
        return false;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(target.tickCount%90 == target.getId()%90) cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.TYRANT_MIST_DOWNSIDE.createInstance(sequenceLevel, 0, 20*5, true), cap, target);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_mist";
    }
}
