package net.dinomine.potioneer.datagen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Potioneer.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.Blocks.FIRE_BLOCKS).add(Blocks.LAVA, Blocks.MAGMA_BLOCK);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.POTION_CAULDRON.get(),
                ModBlocks.SAPPHIRE_ORE.get(),
                ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get(),
                ModBlocks.SAPPHIRE_BLOCK.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SAPPHIRE_ORE.get(),
                        ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get(),
                        ModBlocks.SAPPHIRE_BLOCK.get());

        this.tag(Tags.Blocks.ORES)
                .add(ModBlocks.SAPPHIRE_ORE.get(),
                        ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get());
    }
}
