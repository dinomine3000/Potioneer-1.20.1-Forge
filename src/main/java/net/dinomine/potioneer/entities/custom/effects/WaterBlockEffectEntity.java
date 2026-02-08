package net.dinomine.potioneer.entities.custom.effects;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class WaterBlockEffectEntity extends AbstractEffectEntity {
    public WaterBlockEffectEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance < 32;
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > 20*5)
            kill();
    }
}
