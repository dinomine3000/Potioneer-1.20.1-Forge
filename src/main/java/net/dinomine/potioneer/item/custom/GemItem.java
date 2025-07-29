package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.util.misc.ArtifactHelper;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GemItem extends Item {
    public GemItem(Properties pProperties) {
        super(pProperties);
    }

    @OnlyIn(Dist.CLIENT)
    public static class GemItemTint implements ItemColor {

        @Override
        public int getColor(ItemStack itemStack, int i) {
            if(i != 0) return -1;
            if(itemStack.hasTag()){
                if(!itemStack.getTag().getCompound(ArtifactHelper.GEM_TAG_ID).isEmpty()){
                    return itemStack.getTag().getCompound(ArtifactHelper.GEM_TAG_ID).getInt("color");
                }
            }
            return -1;
        }
    }
}
