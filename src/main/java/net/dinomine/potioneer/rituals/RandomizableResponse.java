package net.dinomine.potioneer.rituals;

import net.dinomine.potioneer.rituals.responses.SpiritResponse;

public interface RandomizableResponse<T extends SpiritResponse & RandomizableResponse<T>> {
    T getRandom();
}
