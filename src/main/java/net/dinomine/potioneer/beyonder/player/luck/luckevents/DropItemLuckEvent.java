package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DropItemLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        if(target instanceof Player player)
            player.drop(player.getInventory().getItem(player.getRandom().nextInt(27)), true, true);
    }
}
