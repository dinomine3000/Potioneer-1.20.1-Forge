package net.dinomine.potioneer.beyonder.effects.redpriest;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BeyonderFireAuraEffect extends BeyonderEffect {
    private int tick = 0;

    public BeyonderFireAuraEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Red Priest Fire Aura";
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(tick++ > 20){
            tick = 0;
            BlockPos pos = target.getOnPos();
            int radius = (9 - sequenceLevel)*2 + 2;
            List<Entity> entities = target.level().getEntities(target, new AABB(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius)), entity -> entity instanceof Mob mob && (mob.getTarget() != null) && mob.getTarget().is(target));
            entities.forEach(entity -> {
                if(entity instanceof LivingEntity victim) victim.setSecondsOnFire(2*(9-sequenceLevel));
            });
            if(entities.isEmpty()) return;
            cap.requestActiveSpiritualityCost(cost);
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }
}
