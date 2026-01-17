package net.dinomine.potioneer.beyonder.downsides;

import net.dinomine.potioneer.beyonder.abilities.Ability;

public abstract class Downside extends Ability {
    public Downside(int sequenceLevel){
        super(sequenceLevel);
//        this.info = new AbilityInfo(130, 4, name, sequenceLevel, cost, 20, "d_" + id);
//        this.isActive = active;
    }

    @Override
    public boolean isDownside() {
        return true;
    }
}
