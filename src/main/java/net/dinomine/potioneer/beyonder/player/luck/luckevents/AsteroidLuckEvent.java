package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

public class AsteroidLuckEvent extends LuckEvent {

    public AsteroidLuckEvent() {
        super();
    }

    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        for(int i = luck.getRandomNumber(1, 3, false, target.getRandom()); i > 0; i--){
            boolean miss = luck.passesLuckCheck(0.6f, 20, 10, target.getRandom());
            BlockPos pos = target.getOnPos();
            if(miss){
                pos = AbilityFunctionHelper.getRandomNearbyBlockPos(pos, luck.getRandomNumber(5, 20, true, target.getRandom()), 0, target.getRandom());
            }
            AbilityFunctionHelper.summonAsteroid(pos, target.level());
        }
    }
}
