package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class ThunderStrikeAbility extends Ability {

    public ThunderStrikeAbility(int sequence){
        this.info = new AbilityInfo(31, 248, "Thunder Strike", 10 + sequence, 70, this.getCooldown(), "thunder_strike");
        this.isActive = true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
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

    private void summonLightning(EntityBeyonderManager cap, Vec3 position, ServerLevel level, boolean thundering){
        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        lightning.setPos(position);
        lightning.setDamage(thundering ? -6 + 5*(10-getSequence()) : 1 + 2*(10-getSequence()));
        level.addFreshEntity(lightning);
        cap.requestActiveSpiritualityCost(info.cost());

    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
    }
}
