package net.dinomine.potioneer.recipe;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RitualContainer extends SimpleContainer {
    public int getPathwayId() {
        return pathwayId;
    }

    public int getReputationLevel() {
        return reputationLevel;
    }

    public float getReputationPercent(){
        return Mth.clamp(reputationLevel/ LivingEntityBeyonderCapability.MAX_REP, 0, 1);
    }

    public int getDesiredLevel(){return desiredLevel;}

    private int desiredLevel = 0;
    private final int pathwayId;
    private final int reputationLevel;

    public RitualContainer(int pathwayId, int reputationLevel, ItemStack... items){
        super(items);
        this.pathwayId = pathwayId;
        this.reputationLevel = reputationLevel;
    }

    public RitualContainer withDesiredLevel(int level){
        this.desiredLevel = level;
        return this;
    }

    public RitualContainer(int pathwayId, ItemStack... items){
        this(pathwayId, 0, items);
    }

    public RitualContainer(int pathwayId, int reputationLevel, List<ItemStack> items){
        this(pathwayId, reputationLevel, items.toArray(new ItemStack[0]));
    }

    public RitualContainer(int pathwayId, List<ItemStack> items){
        this(pathwayId, 0, items);
    }
}
