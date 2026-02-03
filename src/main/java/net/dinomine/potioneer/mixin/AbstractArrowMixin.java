package net.dinomine.potioneer.mixin;

import net.dinomine.potioneer.util.MarkedProjectile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements MarkedProjectile {
    @Shadow public boolean inGround;


    @Unique
    private boolean potioneer$marked = false;
//    @Unique
//    private LivingEntity potioneer$honingTarget = null;

    @Unique
    public boolean potioneer$marked() {
        return potioneer$marked;
    }

//    @Unique
//    public AbstractArrow potioneer$getMe() {
//        return (AbstractArrow) ((Object) this);
//    }
//
//    @Unique
//    public void potioneer$setHoningTarget(LivingEntity target) {
//        potioneer$honingTarget = target;
//    }

    @Unique
    public void potioneer$setMarked() {
        potioneer$marked = true;
    }


//    @Inject(method = "tick", at = @At("HEAD"))
//    public void tick(CallbackInfo ci) {
//        if(!this.inGround && potioneer$honingTarget != null){
//            AbstractArrow me = potioneer$getMe();
//            LivingEntity tar = potioneer$honingTarget;
//            Vec3 diff = new Vec3(tar.getX() - me.getX())
//        }
//    }
}
