package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.misc.MysticalKnowledgeAbility;
import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;

import java.util.ArrayList;
import java.util.List;

public class WheelDivination extends MysticalKnowledgeAbility {

    @Override
    protected List<Page> getPages(int sequenceLevel) {
        List<Page> pages = new ArrayList<>();
        switch(sequenceLevel){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: pages.addAll(List.of(PageRegistry.COIN_DIVINATION, PageRegistry.ROD_DIVINATION));
        }
        return pages;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "wheel_divination";
    }
}
