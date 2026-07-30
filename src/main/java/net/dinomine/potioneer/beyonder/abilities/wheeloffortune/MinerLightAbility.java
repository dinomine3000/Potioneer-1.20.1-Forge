package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.misc.LightAbility;
import net.dinomine.potioneer.block.ModBlocks;

public class MinerLightAbility extends LightAbility {
    public MinerLightAbility(int sequence){
        super(sequence, ModBlocks.MINER_LIGHT.get().defaultBlockState());
        withCost(5);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "miner_light" + (sequenceLevel < 5 ? "_2": "_1");
    }
}
