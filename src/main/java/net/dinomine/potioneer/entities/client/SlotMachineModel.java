package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.DiceEffectEntity;
import net.dinomine.potioneer.entities.custom.SlotMachineEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SlotMachineModel extends GeoModel<SlotMachineEntity> {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/gamble.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/gamble.png");
    private final ResourceLocation animation = new ResourceLocation(Potioneer.MOD_ID, "animations/gamble.animation.json");
    @Override
    public ResourceLocation getModelResource(SlotMachineEntity diceEffectEntity) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(SlotMachineEntity diceEffectEntity) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SlotMachineEntity diceEffectEntity) {
        return animation;
    }
}
