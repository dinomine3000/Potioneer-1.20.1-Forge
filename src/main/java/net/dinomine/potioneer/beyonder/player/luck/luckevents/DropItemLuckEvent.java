package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DropItemLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(BeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        if(!(target instanceof Player player)) return;
        int attempts = luck.getRandomNumber(3, 10, false, target.getRandom());
        for(int i = 0; i < attempts; i++){
            AbilityFunctionHelper.dropItem(player, player.getInventory().getItem(player.getRandom().nextInt(27)), false, false);
        }
    }
}
