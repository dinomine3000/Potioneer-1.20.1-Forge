package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class BlankAbility extends Ability {

    @Override
    public void init() {

    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "blank";
    }
}
