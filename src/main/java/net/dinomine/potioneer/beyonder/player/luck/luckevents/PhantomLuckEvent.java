package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Phantom;

public class PhantomLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        int num = luck.getRandomNumber(1, 12, false, target.getRandom());
        for(int i = 0; i < num; i++){
            BlockPos pos = luck.getRandomBlockPos(target.getOnPos(), 6, true, false, target.getRandom());
            Mob mob = new Phantom(EntityType.PHANTOM, target.level());
            mob.setTarget(target);
            mob.setPos(pos.getCenter());
            mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 1, true, true, true));
            target.level().addFreshEntity(mob);
        }
    }
}
