package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderLightningTargetEffect extends BeyonderEffect {

    public BeyonderLightningTargetEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Tyrant Lightning Target";
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(!cap.getLuckManager().passesLuckCheck(0.98f, 0, 0, target.getRandom())){
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, target.level());
            lightning.setDamage(Math.max((10-sequenceLevel)*5 - 20, 5));
            lightning.setPos(target.getOnPos().offset(target.getRandom().nextInt(3), 0, target.getRandom().nextInt(3)).getCenter());
            target.level().addFreshEntity(lightning);
        }
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
    }
}
