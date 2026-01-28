package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.misc.MysticalKnowledgeAbility;
import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;

import java.util.ArrayList;
import java.util.List;

public class WheelKnowledgeAbility extends MysticalKnowledgeAbility {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public WheelKnowledgeAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected List<Page> getPages(int sequenceLevel) {
        List<Page> result = new ArrayList<>();
        switch(sequenceLevel){
            case 7:
                result.addAll(List.of(PageRegistry.CHARM_PATIENCE, PageRegistry.CHARM_GAMBLE, PageRegistry.CHARM_SPEED, PageRegistry.CHARM_COOLDOWN));
            case 8:
                result.addAll(List.of(PageRegistry.CHARM_INSTANT_LUCK, PageRegistry.CHARM_TEMP_LUCK, PageRegistry.RITUALS_CANDLES, PageRegistry.RITUALS_INK, PageRegistry.RITUALS_DAGGER, PageRegistry.RITUALS_OFFERINGS, PageRegistry.CHARMS_CRAFTING));
            case 9:
                break;
        }
        return result;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "wheel_knowledge";
    }
}
