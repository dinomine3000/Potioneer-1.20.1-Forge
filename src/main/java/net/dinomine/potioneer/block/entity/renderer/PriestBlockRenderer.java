package net.dinomine.potioneer.block.entity.renderer;

import net.dinomine.potioneer.block.entity.PriestLightBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PriestBlockRenderer extends GeoBlockRenderer<PriestLightBlockEntity> {
    public PriestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new PriestBlockModel());
    }
}
