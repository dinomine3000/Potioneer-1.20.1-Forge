package net.dinomine.potioneer.beyonder.effects.misc;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;


public class BeyonderPlagueEffect extends BeyonderEffect {
    private static final double PLAGUE_RANGE = 5;
    private int lives = 1;
    private int spreadCd;
    private UUID casterId = null;


    public void setCasterId(UUID id){
        this.casterId = id;
    }


    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.hasEffect(ModEffects.PLAGUE_EFFECT.get())){
            target.addEffect(new MobEffectInstance(ModEffects.PLAGUE_EFFECT.get(), -1, lives--, true, true));
            if(lives < -1){
                endEffectWhenPossible();
                return;
            }
        }
        cap.requestPassiveSpiritualityCost(1);
        if(spreadCd++ > 400){
            spreadCd = 0;
            if(casterId != null){
                Player caster = target.level().getPlayerByUUID(casterId);
                if(caster != null)
                    caster.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(casterCap -> {
                        if(Math.floorDiv(casterCap.getPathwaySequenceId(), 10) == 2)
                            casterCap.requestActiveSpiritualityCost(-cost);
                    });
            }
            List<Entity> spreadTargets = target.level().getEntities(target, target.getBoundingBox().inflate(PLAGUE_RANGE));
            for(Entity ent: spreadTargets){
                if(ent instanceof LivingEntity entity){
                    if(casterId != null && ent instanceof Player testPlayer && testPlayer.getUUID().compareTo(casterId) == 0) continue;
                    entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(newCap -> {
//                        if(!newCap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MISC_PLAGUE)){
//                            BeyonderPlagueEffect eff = new BeyonderPlagueEffect(sequenceLevel, cost, -1, true, BeyonderEffects.EFFECT.MISC_PLAGUE);
//                            eff.setCasterId(casterId);
//                            newCap.getEffectsManager().addOrReplaceEffect(eff, newCap, entity);
//                        }
                    });
                }
            }
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.hasEffect(ModEffects.PLAGUE_EFFECT.get())){
            target.removeEffect(ModEffects.PLAGUE_EFFECT.get());
        }
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        if(nbt.contains("casterId")) casterId = nbt.getUUID("casterId");
        lives = nbt.getInt("livesRemaining");
        spreadCd = nbt.getInt("spreadCd");
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        if(casterId != null) nbt.putUUID("casterId", casterId);
        nbt.putInt("livesRemaining", lives);
        nbt.putInt("spreadCd", spreadCd);
    }
}
