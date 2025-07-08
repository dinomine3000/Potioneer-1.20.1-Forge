package net.dinomine.potioneer.item.custom;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;

public class ConjuredPickaxeItem extends PickaxeItem {
    public ConjuredPickaxeItem(Properties pProperties) {
        super(Tiers.DIAMOND, -2, -2.8f, pProperties);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        if(!player.isCrouching()){
            item.setCount(0);
        }
        return super.onDroppedByPlayer(item, player);
    }
}
