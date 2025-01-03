package net.dinomine.potioneer.entities.damageSources;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ChryonPierceSource extends DamageSource {
    public ChryonPierceSource(Holder<DamageType> pType, @Nullable Entity pDirectEntity, @Nullable Entity pCausingEntity, @Nullable Vec3 pDamageSourcePosition) {
        super(pType, pDirectEntity, pCausingEntity, pDamageSourcePosition);
    }
}
