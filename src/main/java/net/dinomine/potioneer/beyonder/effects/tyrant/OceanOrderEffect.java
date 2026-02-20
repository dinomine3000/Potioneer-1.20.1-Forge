package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ModTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;

public class OceanOrderEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, Optional<LivingEntityBeyonderCapability> optAttackerCap, boolean calledOnVictim) {
        if(victim.level().isClientSide()) return false;
        if(calledOnVictim){
            victim.level().getEntities(victim, new AABB(victim.getOnPos().offset(-32, -32, -32), victim.getOnPos().offset(32, 32, 32)), ent -> ent.getType().is(ModTags.Entities.OCEAN_ORDER_MOBS)).forEach(oceanMob -> {
                if(!(oceanMob instanceof LivingEntity livingEntity)) return;
                livingEntity.setLastHurtByMob(attacker);
                if(attacker instanceof Player playerAttacker) livingEntity.setLastHurtByPlayer(playerAttacker);
            });
        }
        else {
            attacker.level().getEntities(attacker, new AABB(victim.getOnPos().offset(-32, -32, -32), victim.getOnPos().offset(32, 32, 32)), ent -> ent.getType().is(ModTags.Entities.OCEAN_ORDER_MOBS)).forEach(oceanMob -> {
                if(!(oceanMob instanceof LivingEntity livingEntity)) return;
                livingEntity.setLastHurtByMob(victim);
                if(victim instanceof Player playerVictim) livingEntity.setLastHurtByPlayer(playerVictim);
            });
        }
        return false;
    }
}
