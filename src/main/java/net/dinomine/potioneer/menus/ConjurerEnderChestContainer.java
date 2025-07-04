package net.dinomine.potioneer.menus;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;

public class ConjurerEnderChestContainer implements Container {
    private final PlayerEnderChestContainer delegate;

    public ConjurerEnderChestContainer(PlayerEnderChestContainer originalContainer){
        super();
        this.delegate = originalContainer;
    }

    public ItemStack getItem(int index) {
        return delegate.getItem(index);
    }

    public ItemStack removeItem(int i, int i1) {
        return delegate.removeItem(i, i1);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return delegate.removeItemNoUpdate(i);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        delegate.setItem(index, stack);
    }

    @Override
    public void setChanged() {
        delegate.setChanged();
    }

    @Override
    public int getContainerSize() {
        return delegate.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void clearContent() {
        delegate.clearContent();
    }
}
