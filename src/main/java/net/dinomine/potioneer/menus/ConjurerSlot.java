package net.dinomine.potioneer.menus;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.ConjurerContainer;
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
        ((ConjurerContainer) container).player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent( cap -> {
            cap.requestActiveSpiritualityCost((oldStack.getCount() - getItem().getCount()) * 5);
        });
        oldStack = getItem().copy();
        super.setChanged();
    }
}
