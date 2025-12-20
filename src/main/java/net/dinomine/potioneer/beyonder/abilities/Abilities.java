package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.abilities.tyrant.WaterAffinityAbility;

import java.util.HashMap;
import java.util.function.Function;

public class Abilities {
    private static final HashMap<String, AbilityFactory> ABILITIES = new HashMap<>();

    public static final AbilityFactory WATER_AFFINITY = registerAbility("water_affinity", WaterAffinityAbility::new);

    private static AbilityFactory registerAbility(String ablId, Function<Integer, Ability> constructor){
        AbilityFactory factory = new AbilityFactory(constructor, ablId);
        ABILITIES.put(ablId, factory);
        return factory;
    }

    public static AbilityFactory getAbilityById(String abl_id){
        return ABILITIES.get(abl_id);
    }

    public static Ability getAbilityById(String abl_id, int sequenceLevel){
        return ABILITIES.get(abl_id).create(sequenceLevel);
    }

    public static class AbilityFactory{
        private Function<Integer, Ability> ablConstructor;
        private String ablId;
        public AbilityFactory(Function<Integer, Ability> ablConstructor, String id){
            this.ablConstructor = ablConstructor;
            this.ablId = id;
        }

        public Ability create(int sequenceLevel){
            Ability abl = ablConstructor.apply(sequenceLevel);
            abl.setId(ablId);
            return abl;
        }
    }
}
