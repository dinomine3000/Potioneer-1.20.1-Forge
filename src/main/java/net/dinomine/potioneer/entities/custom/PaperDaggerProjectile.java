package net.dinomine.potioneer.entities.custom;

import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class PaperDaggerProjectile extends AbstractArrow {

    public PaperDaggerProjectile(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        if(tickCount > 20) discard();
        else super.tick();
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        discard();
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.PAPER);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if(level() instanceof ServerLevel serverLevel){
            pResult.getEntity().invulnerableTime = 0;
            pResult.getEntity().hurt(PotioneerDamage.paper_dagger(serverLevel, getOwner()), 5);
        }
        discard();
    }
}
