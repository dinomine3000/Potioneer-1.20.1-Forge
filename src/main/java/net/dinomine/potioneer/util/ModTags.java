package net.dinomine.potioneer.util;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> FIRE_BLOCKS = tag("fire_blocks");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(Potioneer.MOD_ID, name));
        }
    }
    public static class Items {

        public static final TagKey<Item> ELECTRIFICATION_WEAPONS = tag("items/electrification_weapons");
        public static final TagKey<Item> WEAPON_PROFICIENCY = tag("items/weapon_proficiency");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(Potioneer.MOD_ID, name));
        }

    }
}

