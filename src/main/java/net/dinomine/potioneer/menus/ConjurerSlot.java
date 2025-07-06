package net.dinomine.potioneer.menus;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.ConjurerContainer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ConjurerSlot extends Slot {
    ItemStack oldStack;

    public ConjurerSlot(Slot slot){
        super(slot.container, slot.index, slot.x, slot.y);
        oldStack = getItem().copy();
    }

    @Override
    public void setChanged() {
        int diff = (oldStack.getCount() - getItem().getCount());
        int wantedCost = diff*5;
        ((ConjurerContainer) container).player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent( cap -> {
            if(wantedCost > 0){
                int calcDebt = Math.max(wantedCost - (int)cap.getSpirituality(), 0);
                ((ConjurerContainer) container).changeDebt(calcDebt);
                //cant use requestCost bc further debt calculations need the updated spirituality amount
                cap.changeSpirituality(-wantedCost);
            } else {
                int debt = ((ConjurerContainer) container).getDebt();
                int spiritualityDifference = 0;
                //debt = 10; wantedCost = -50
                if(debt > -wantedCost){
                    ((ConjurerContainer) container).changeDebt(wantedCost);
                } else {
                    spiritualityDifference = wantedCost + debt;
                    ((ConjurerContainer) container).changeDebt(-debt);
                }
                cap.requestActiveSpiritualityCost(spiritualityDifference);
            }
        });
        oldStack = getItem().copy();
        super.setChanged();
    }
}
