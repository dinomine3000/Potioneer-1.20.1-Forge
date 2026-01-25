package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderInstantLuckEffect extends BeyonderEffect {
    private boolean acquired = false;
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(acquired || target.level().isClientSide()) return;
        cap.getLuckManager().grantLuck(50 + 25*(8-sequenceLevel));
        target.sendSystemMessage(Component.translatable("message.potioneer.luck_boost_grant"));
        target.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1, 1);
        acquired = true;
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        cap.getLuckManager().consumeLuck(50 + 25*(8-sequenceLevel) + 10);
        target.sendSystemMessage(Component.literal("All is not without its price. Your luck has been taken back."));
        if (cap.getLuckManager().getLuck() < 0){
            target.sendSystemMessage(Component.literal("Unlucky..."));
            cap.getLuckManager().instantlyCastEvent(target);
        }
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("was_acquired", acquired);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.acquired = nbt.getBoolean("was_acquired");
    }
}
