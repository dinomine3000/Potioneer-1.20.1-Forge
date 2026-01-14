package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.misc.LightAbility;
import net.dinomine.potioneer.block.ModBlocks;

public class MinerLightAbility extends LightAbility {
    public MinerLightAbility(int sequence){
        super(sequence, ModBlocks.MINER_LIGHT.get().defaultBlockState(), level -> 5 + 2*(9-level));
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "miner_light";
    }
}
