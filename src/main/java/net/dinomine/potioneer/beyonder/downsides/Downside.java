package net.dinomine.potioneer.beyonder.downsides;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public abstract class Downside extends Ability {

    @Override
    public boolean isDownside() {
        return true;
    }

}
