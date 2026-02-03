package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;

public class LightningStrikeLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, target.level());
        lightning.setPos(luck.getRandomBlockPos(target.getOnPos(), 6, false, false, target.getRandom()).getCenter());
        lightning.setDamage(luck.getRandomNumber(5, 20, false, target.getRandom()));
        target.level().addFreshEntity(lightning);
    }
}
