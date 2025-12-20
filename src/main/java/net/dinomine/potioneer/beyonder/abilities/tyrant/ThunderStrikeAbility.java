package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class ThunderStrikeAbility extends Ability {
    private static final float actingProgress = 0.001f;

    public ThunderStrikeAbility(int sequence){
        this.info = new AbilityInfo(31, 248, "Thunder Strike", 10 + sequence, 50, this.getMaxCooldown(), "thunder_strike");
        this.isActive = true;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < getInfo().cost()) return false;
        if(target.level().isClientSide()) return true;
        ServerLevel level = (ServerLevel) target.level();
        boolean thundering = level.isThundering();
        int radius = thundering ? 128 : 32;
        ArrayList<Entity> hits = AbilityFunctionHelper.getLivingEntitiesLooking(target, radius);
        for(Entity ent: hits){
            if(ent instanceof LivingEntity entity && entity != target){
                summonLightning(cap, entity.position(), level, thundering);
                return true;
            }
        }
        HitResult hit = target.pick(radius, 0, false);
        if(hit instanceof BlockHitResult blockHit && !level.getBlockState(blockHit.getBlockPos()).is(Blocks.AIR)){
            summonLightning(cap, blockHit.getLocation(), level, thundering);
            return true;
        }
        return false;
    }

    private void summonLightning(LivingEntityBeyonderCapability cap, Vec3 position, ServerLevel level, boolean thundering){
        cap.getCharacteristicManager().progressActing(actingProgress, 17);
        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        lightning.setPos(position);
        int damage;
        if(thundering){
            damage = (int) (-8 + 5*(10-getSequence()) + 4*cap.getCharacteristicManager().getAggregatedActingProgress(10 + getSequence()));
        } else {
            damage = -2 + 2*(10-getSequence()) + (int)(3 * cap.getCharacteristicManager().getAggregatedActingProgress(10 + getSequence()));
        }
        lightning.setDamage(damage);
        level.addFreshEntity(lightning);
        cap.requestActiveSpiritualityCost(thundering ? info.cost() / 2f : info.cost());

    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }
}
