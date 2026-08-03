package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.util.misc.ModTags;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AbstractLiquidContainer extends Item {
    int maxLevel;
    String name;

    public AbstractLiquidContainer(Properties pProperties, int maxLevel) {
        super(pProperties);
        this.maxLevel = maxLevel;
    }

    @Override
    public Component getName(ItemStack pStack) {
        if(!ModTags.hasTag(ModTags.TAGS.POTION, pStack)) return super.getName(pStack);
        String name = ModTags.PotionInfoTag.getPotionName(ModTags.getTagFromItem(ModTags.TAGS.POTION, pStack));
        String key = this.getDescriptionId() + "." + name;
        Component comp;
        if(I18n.exists(key))
            comp = Component.translatable(key);
        else
            comp = Component.translatable(this.getDescriptionId() + ".generalized", capitalizeFirstLetters(name.replace("_", " ")));
        return comp;
    }
    static String capitalizeFirstLetters(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        return Arrays.stream(input.split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
    @OnlyIn(Dist.CLIENT)
    public static class LiquidContainerTint implements ItemColor {

        @Override
        public int getColor(ItemStack itemStack, int i) {
            if(i != 1) return -1;
            if(!ModTags.hasTag(ModTags.TAGS.POTION, itemStack))
                return 0x00000000;
            return ModTags.PotionInfoTag.getPotionColor(ModTags.getTagFromItem(ModTags.TAGS.POTION, itemStack));
        }
    }
}
