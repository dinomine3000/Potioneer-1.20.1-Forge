package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.misc.BoneMealAbility;

public class WheelBoneMealAbility extends BoneMealAbility {

    public WheelBoneMealAbility(int sequence) {
        super(sequence);
        setCost(level -> 10 + 10*(9-level));
        defaultMaxCooldown = 2*20;
//        this.info = new AbilityInfo(5, 200, "Bone Meal", sequence, 10 + 10*(9-sequence), 2*20, "w_bone_meal");
//        this.isActive = true;
    }
}
