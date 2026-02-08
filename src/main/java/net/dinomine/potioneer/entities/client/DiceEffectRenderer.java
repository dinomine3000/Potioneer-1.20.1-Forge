package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.entities.custom.effects.DiceEffectEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DiceEffectRenderer extends GeoEntityRenderer<DiceEffectEntity> {
    public DiceEffectRenderer(EntityRendererProvider.Context context) {
        super(context, new DiceEffectModel());
    }
}
