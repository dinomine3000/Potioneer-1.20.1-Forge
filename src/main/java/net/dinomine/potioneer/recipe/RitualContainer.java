package net.dinomine.potioneer.recipe;

import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class RitualContainer extends SimpleContainer {
    public int getPathwayId() {
        return pathwayId;
    }

    public int getReputationLevel() {
        return reputationLevel;
    }

    public float getReputationPercent(){
        return Mth.clamp(reputationLevel/10f, 0, 1);
    }

    private int pathwayId;
    private int reputationLevel;

    public RitualContainer(int pathwayId, int reputationLevel, ItemStack... pItems){
        super(pItems);
        this.pathwayId = pathwayId;
        this.reputationLevel = reputationLevel;
    }

    public RitualContainer(int pathwayId, ItemStack... items){
        this(pathwayId, 0, items);
    }
}
