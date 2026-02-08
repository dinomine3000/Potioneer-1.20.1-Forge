package net.dinomine.potioneer.block.custom;

import net.dinomine.potioneer.block.entity.PotionCauldronBlockEntity;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class FakeWaterBlock extends Block {
    public FakeWaterBlock(Properties pProperties) {
        super(Properties.copy(Blocks.GLASS)
                .noCollission()
                .strength(-1.0F)
                .noLootTable());
    }
}
