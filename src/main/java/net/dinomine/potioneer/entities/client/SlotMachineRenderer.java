package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.entities.custom.effects.SlotMachineEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class SlotMachineRenderer extends AbstractGeoEffectEntityRenderer<SlotMachineEntity> {
    public SlotMachineRenderer(EntityRendererProvider.Context context) {
        super(context, new SlotMachineModel());
    }
}
