package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class FateEffect extends BeyonderEffect {
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(!cap.getLuckManager().castEventNoRefresh(target)){
            target.sendSystemMessage(Component.translatable("ability.potioneer.fate_cast_fail"));
            return;
        }
        cap.requestActiveSpiritualityCost(cost);
        endEffectWhenPossible();
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }
}
