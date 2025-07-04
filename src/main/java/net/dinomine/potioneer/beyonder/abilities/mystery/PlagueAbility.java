package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.misc.BeyonderPlagueEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.entity.WaterTrapBlockEntity;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.WaterPrisonEffectSTC;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;

import static net.dinomine.potioneer.block.custom.MinerLightSourceBlock.WATERLOGGED;

public class PlagueAbility extends Ability {

    public PlagueAbility(int sequence){
        this.info = new AbilityInfo(57, 248, "Cast Plague", 20 + sequence, 60*(9-sequence), 20*10, "cast_plague");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost()){
            ServerLevel level = (ServerLevel) target.level();
            Vec3 pos = target.position();
            double radius = 13 - getSequence();
            AABB box = new AABB(
                    pos.x-radius, pos.y-radius, pos.z-radius,
                    pos.x+radius, pos.y+radius, pos.z+radius
            );
            ArrayList<Entity> hits = new ArrayList<>(level.getEntities((Entity) null, box, entity -> entity instanceof LivingEntity));

            for(Entity ent: hits){
                LivingEntity entity = (LivingEntity) ent;
                if(entity != target) {
                    //cost given here is granted to the caster every 10 seconds per entity with the plague effect
                    BeyonderPlagueEffect eff = new BeyonderPlagueEffect(getSequence(), 5, -1, true, BeyonderEffects.EFFECT.MISC_PLAGUE);
                    if(target instanceof Player player) eff.setCasterId(player.getUUID());
                    entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(entCap -> {
                        if(!entCap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MISC_PLAGUE)){
                            entCap.getEffectsManager().addEffect(eff, entCap, entity);
                        }
                    });
                }
            }
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MISC_PLAGUE, getSequence(), cap, target);
            cap.requestActiveSpiritualityCost(info.cost());
            return true;
        }
        return false;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
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
