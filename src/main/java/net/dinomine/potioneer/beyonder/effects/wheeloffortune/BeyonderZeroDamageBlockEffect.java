package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import io.netty.channel.epoll.EpollEventArray;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class BeyonderZeroDamageBlockEffect extends BeyonderEffect {
    private ItemStack item = ItemStack.EMPTY;
    public BeyonderZeroDamageBlockEffect withItem(ItemStack stack){
        this.item = stack;
        return this;
    }
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            player.addItem(item);
            if(player.level().isClientSide())
                target.level().playSound(player, target.getOnPos(), ModSounds.LUCK.get(), SoundSource.PLAYERS, 1, 1);
            if(player.level().isClientSide())
                target.level().playSound(player, target.getOnPos(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1, 1);
        }
        endEffectWhenPossible();
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
