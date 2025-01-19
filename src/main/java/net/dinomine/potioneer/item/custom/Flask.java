package net.dinomine.potioneer.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;


public class Flask extends AbstractLiquidContainer {
    public Flask(Properties pProperties) {
        super(pProperties.stacksTo(16),2);
    }


}
