package net.dinomine.potioneer.menus;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class ConjurerContainerMenu extends ChestMenu {

    private Player player;

    public ConjurerContainerMenu(MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, Container pContainer, int pRows) {
        super(pType, pContainerId, pPlayerInventory, pContainer, pRows);

        int rows;
        int cols;
        for(rows = 0; rows < this.containerRows; ++rows) {
            for(cols = 0; cols < 9; ++cols) {
                int idx = 9*rows + cols;
                this.slots.set(idx, new ConjurerSlot(this.slots.get(idx)));
            }
        }
    }


}
