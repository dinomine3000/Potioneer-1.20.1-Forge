package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.OpenDivinationScreenSTC;
import net.dinomine.potioneer.network.messages.abilityRelevant.WaterPrisonEffectSTC;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PacketDistributor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.Predicate;

public class WaterPrisonAbility extends Ability {

    public WaterPrisonAbility(int sequence){
        this.info = new AbilityInfo(31, 152, "Cast Water Prison", 10 + sequence, 20 + 20*(9 - sequence), 20*20, "water_prison");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost()){
            double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequence());
            ArrayList<Entity> hits = AbilityFunctionHelper.getLivingEntitiesAround(target, radius);
            for(Entity ent: hits){
                LivingEntity entity = (LivingEntity) ent;
                if(entity != target) entity.addEffect(new MobEffectInstance(ModEffects.WATER_PRISON.get(), 20*30, Math.floorDiv(10 - getSequence(), 2), false, false));
                if(entity instanceof ServerPlayer player){
                    Vec3 pos = target.position();
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new WaterPrisonEffectSTC(pos.x, pos.y, pos.z, radius));
                }
            }
            target.level().playSound(null, target.getOnPos(), ModSounds.WATER_PRISON.get(), SoundSource.PLAYERS, 1, 1);
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
