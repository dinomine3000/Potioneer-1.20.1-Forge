package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.misc.LightAbility;
import net.dinomine.potioneer.block.ModBlocks;

public class MinerLightAbility extends LightAbility {
    public MinerLightAbility(int sequence){
        super(sequence, ModBlocks.MINER_LIGHT.get().defaultBlockState());
        this.info = new AbilityInfo(5, 56, "Miner Light", sequence, 5 + 2*(9-sequence), this.getCooldown(), "miner_light");
    }
}
