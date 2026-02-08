package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.WaterPrisonEffectSTC;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;

public class WaterPrisonAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "water_prison";
    }

    public WaterPrisonAbility(int sequence){
        super(sequence);
        defaultMaxCooldown = 20*20;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > cost()){
            double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequenceLevel());
            AllySystemSaveData saveData = AllySystemSaveData.from((ServerLevel) target.level());
            ArrayList<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesAround(target, radius, ent -> !saveData.areEntitiesAllies(ent, target));
            for(LivingEntity entity: hits){
                if(entity.is(target)) continue;
                entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(victimCap ->
                        victimCap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.TYRANT_WATER_PRISON.createInstance(getSequenceLevel(), 0, 20*30, true), victimCap, entity));
            }
            ParticleMaker.summonAOEParticles(target.level(), target.getEyePosition(), (int)(2*radius), radius, ParticleMaker.Preset.AOE_END_ROD);
            target.level().playSound(null, target.getOnPos(), ModSounds.WATER_PRISON.get(), SoundSource.PLAYERS, 1, 1);
            cap.requestActiveSpiritualityCost(cost());
            return true;
        }
        return false;
    }
}
