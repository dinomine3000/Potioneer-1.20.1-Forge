package net.dinomine.potioneer.block.entity.renderer;

import net.dinomine.potioneer.block.entity.MinerLightBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MinerBlockRenderer extends GeoBlockRenderer<MinerLightBlockEntity> {
    public MinerBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new MinerBlockModel());
    }
}
