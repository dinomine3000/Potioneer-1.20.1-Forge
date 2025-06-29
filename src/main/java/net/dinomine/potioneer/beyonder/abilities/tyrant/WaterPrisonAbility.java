package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.OpenDivinationScreenSTC;
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
import net.minecraft.world.phys.*;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.function.Predicate;

public class WaterPrisonAbility extends Ability {

    public WaterPrisonAbility(int sequence){
        this.info = new AbilityInfo(31, 152, "Cast Water Prison", sequence, 40, 20*10, "water_prison");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost()){
            ServerLevel level = (ServerLevel) target.level();
            Vec3 pos = target.position();
            double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequence());
            AABB box = new AABB(
                    pos.x-radius, pos.y-radius, pos.z-radius,
                    pos.x+radius, pos.y+radius, pos.z+radius
            );
            ArrayList<Entity> hits = new ArrayList<>(level.getEntities((Entity) null, box, entity -> entity instanceof LivingEntity));

            for(Entity ent: hits){
                LivingEntity entity = (LivingEntity) ent;
                if(entity != target) entity.addEffect(new MobEffectInstance(ModEffects.WATER_PRISON.get(), 20*30, Math.floorDiv(10 - getSequence(), 2), false, false));
                if(entity instanceof ServerPlayer player){
                    System.out.println("Radius: " + radius);
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new WaterPrisonEffectSTC(pos.x, pos.y, pos.z, radius));
                }
            }

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
