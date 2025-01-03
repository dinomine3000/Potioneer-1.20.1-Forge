package net.dinomine.potioneer.datagen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_,
                               CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, Potioneer.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.Items.POTION_INGREDIENTS).add(ModItems.SAPPHIRE.get())
                .add(Items.DIAMOND)
                .add(Items.COAL)
                .add(Items.REDSTONE)
                .add(Items.GLOWSTONE_DUST)
                .add(Items.IRON_INGOT)
                .add(Items.GOLD_INGOT)
                .add(Items.COPPER_INGOT)
                .add(ModItems.PECAN_LEAF.get())
                .add(ModItems.PECAN_SHELL.get())
                .add(ModItems.SOLSEER.get())
                .add(ModItems.WANDERING_CACTUS_PRICK.get());
    }
}
