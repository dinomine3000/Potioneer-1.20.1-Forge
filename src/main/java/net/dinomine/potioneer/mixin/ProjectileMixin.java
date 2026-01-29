package net.dinomine.potioneer.mixin;

import net.dinomine.potioneer.util.DodgeableProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Projectile.class)
public abstract class ProjectileMixin implements DodgeableProjectile {
    @Unique
    private boolean potioneer$markedForDodge = false;

    @Unique
    public boolean potioneer$hasBeenDodged() {
        return potioneer$markedForDodge;
    }

    @Unique
    public void potioneer$markDodged() {
        potioneer$markedForDodge = true;
    }
}
