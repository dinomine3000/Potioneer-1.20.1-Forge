package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.tyrant.RulePylonAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.events.ServerEventsTyrant;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TyrantChangeItemInHandRuleEffect extends BeyonderEffect {
    private ItemStack stack;
    @Override
    public boolean canBeCleansed() {
        return false;
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        this.stack = target.getMainHandItem();
        super.onAcquire(cap, target);
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(!target.getMainHandItem().equals(stack, false)){
            this.stack = target.getMainHandItem();
            ServerEventsTyrant.ruleBroken(RulePylonAbility.Rule.MAIN_HAND_ITEM, target);
        }
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }
}
