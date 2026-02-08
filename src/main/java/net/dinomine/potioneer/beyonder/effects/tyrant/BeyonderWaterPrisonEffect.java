package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class BeyonderWaterPrisonEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player && (player.isCreative() || player.isSpectator())) return;
        if(target.isInWater()){
            float mult = Math.max((int)Math.floorDiv(10 - sequenceLevel, 2), 1);
            if(!target.isEyeInFluidType(Fluids.WATER.getFluidType())){
                mult *= 40f;
            }
           AbilityFunctionHelper.pushEntity(target, new Vec3(0, -mult/80, 0));
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
