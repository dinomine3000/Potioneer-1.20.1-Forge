package net.dinomine.potioneer.rituals.spirits;

import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.dinomine.potioneer.rituals.criteria.AlwaysCriteria;
import net.dinomine.potioneer.rituals.criteria.NeverCriteria;
import net.dinomine.potioneer.rituals.responses.NothingResponse;
import net.dinomine.potioneer.rituals.responses.PlayerResponse;

import java.util.ArrayList;

public class PlayerRitualSpirit extends EvilSpirit{
    public PlayerRitualSpirit(){
        super(new RitualResponseLogic(new NeverCriteria(), new AlwaysCriteria(), new NothingResponse(),
                new PlayerResponse()), new ArrayList<>());
    }
}
