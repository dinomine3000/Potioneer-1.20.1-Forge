package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.misc.LightAbility;
import net.dinomine.potioneer.block.ModBlocks;

public class MinerLightAbility extends LightAbility {
    public MinerLightAbility(){
        super(ModBlocks.MINER_LIGHT.get().defaultBlockState());
    }

    @Override
    public void init() {
        cost = 5;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "miner_light" + (sequenceLevel < 5 ? "_2": "_1");
    }
}
