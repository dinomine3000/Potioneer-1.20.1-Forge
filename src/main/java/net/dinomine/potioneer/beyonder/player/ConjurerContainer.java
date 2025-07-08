package net.dinomine.potioneer.beyonder.player;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ConjurerContainer extends SimpleContainer {
    public Player player;
    private int publicSize;
    private int debt = 0;

    public ConjurerContainer(ConjurerContainer other, int size){
        this(other.player, size);
    }

    public ConjurerContainer(ConjurerContainer other){
        this(other.player, other.publicSize);
    }

    public ConjurerContainer(Player player, int publicSize){
        super(54);
        this.publicSize = publicSize;
        this.player = player;
    }

    public void setDebt(int debt){
        this.debt = debt;
    }

    public void changeDebt(int inc){
        debt += inc;
    }

    public int getDebt(){
        return debt;
    }

    public void allowSize(int size){
        if(size >= 0 && size%9 == 0)
            publicSize = Math.min(size, 54);
    }

    @Override
    public int getContainerSize() {
        return publicSize;
    }

    public ItemStack getItem(int pIndex) {
        return pIndex >= 0 && pIndex < publicSize ? super.getItem(pIndex) : ItemStack.EMPTY;
    }
}
