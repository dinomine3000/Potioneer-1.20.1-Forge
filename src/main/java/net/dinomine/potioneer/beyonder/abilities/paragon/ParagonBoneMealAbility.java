package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.misc.BoneMealAbility;

public class ParagonBoneMealAbility extends BoneMealAbility {

    public ParagonBoneMealAbility() {
        defaultMaxCooldown = 2*20;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "p_bone_meal";
    }
}
