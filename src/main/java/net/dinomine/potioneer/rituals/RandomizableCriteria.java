package net.dinomine.potioneer.rituals;

import net.dinomine.potioneer.rituals.criteria.ResponseCriteria;

public interface RandomizableCriteria<T extends ResponseCriteria & RandomizableCriteria<T>> {
    T getRandom();
}
