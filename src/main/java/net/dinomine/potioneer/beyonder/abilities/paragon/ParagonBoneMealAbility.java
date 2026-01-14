package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.misc.BoneMealAbility;

public class ParagonBoneMealAbility extends BoneMealAbility {

    public ParagonBoneMealAbility(int sequence) {
        super(sequence);
//        this.info = new AbilityInfo(109, 128, "Bone Meal", 40 + sequence, 2*(10-sequence), 2*20, "p_bone_meal");
//        this.isActive = true;
        setCost(level -> 2*(10-level));
        defaultMaxCooldown = 2*20;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "p_bone_meal";
    }
}
