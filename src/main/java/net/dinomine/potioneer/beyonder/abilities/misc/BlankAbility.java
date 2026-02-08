package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;

import java.util.function.Function;

public class BlankAbility extends Ability {
    private final Function<Integer, String> descId;
    /**
     * ability that does nothing.
     * @param sequenceLevel
     */
    public BlankAbility(int sequenceLevel, Function<Integer, String> descId) {
        super(sequenceLevel);
        this.descId = descId;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return descId.apply(sequenceLevel);
    }
}
