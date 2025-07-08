package net.dinomine.potioneer.recipe;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class PotionCauldronContainer extends SimpleContainer {
    public boolean isOnFire() {
        return onFire;
    }

    @Override
    public String toString() {
        return "PotionCauldronContainer{" +
                "onFire=" + onFire +
                ", waterLevel=" + waterLevel +
                ", inventory=" + super.toString() +
                '}';
    }

    public void setOnFire(boolean onFire) {
        this.onFire = onFire;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    private boolean onFire;
    private int waterLevel;

    public PotionCauldronContainer(boolean onFire, int waterLevel) {
        super(9);
        this.onFire = onFire;
        this.waterLevel = waterLevel;
    }

    public PotionCauldronContainer(boolean onFire, int waterLevel, ItemStack... pItems) {
        super(pItems);
        if(pItems.length != 9) throw new IllegalArgumentException("A potion cauldron should have exactly 9 items");
        this.onFire = onFire;
        this.waterLevel = waterLevel;
    }

}
