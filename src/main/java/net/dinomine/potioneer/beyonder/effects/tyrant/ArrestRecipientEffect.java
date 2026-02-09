package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.misc.AbstractSourceRecipientEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.UUID;

public class ArrestRecipientEffect extends AbstractSourceRecipientEffect {
    private static final int spiritualitySap = 50;
    private boolean damaged = false;
    private boolean aoj;

    @Override
    public void refreshTime(LivingEntityBeyonderCapability cap, LivingEntity target, BeyonderEffect effect) {}

    public void setEnforcer(UUID enforcerId){this.sources.put(enforcerId, 10);}

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        List<Player> playerList = getPlayerList(target.level());
        if(playerList.isEmpty()) return;
        Player enforcer = playerList.get(0);

        if(!damaged){
            target.hurt(PotioneerDamage.arrest((ServerLevel) enforcer.level(), enforcer), 5 + Math.max(5*(7-getSequenceLevel()), 0));
            aoj = AreaOfJurisdictionAbility.isTargetUnderInfluenceOfEnforcer(target, enforcer);
            if(aoj){
                enforcer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(enforcerCap -> {
                    enforcerCap.requestActiveSpiritualityCost(-spiritualitySap);
                    cap.requestActiveSpiritualityCost(spiritualitySap);
                });
            }
            target.level().playSound(null, target.getOnPos(), ModSounds.ARREST.get(), SoundSource.PLAYERS, 1, (float) target.getRandom().triangle(1f, 0.2f));
        }
        damaged = true;
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, maxLife-lifetime, 255, false, false, true));
        if(target.isPassenger()){
            target.stopRiding();
        }
        target.setDeltaMovement(0, 0, 0);
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, LivingEntityBeyonderCapability attackerCap, boolean calledOnVictim) {
        if(victim.level().isClientSide() || !calledOnVictim || !aoj) return false;
        List<Player> playerList = getPlayerList(victim.level());
        if(playerList.isEmpty()) return false;
        Player enforcer = playerList.get(0);
        if(enforcer.is(attacker) && aoj){
            event.setAmount(event.getAmount()*2);
            victim.level().playSound(null, victim.getOnPos(), ModSounds.CRIT.get(), SoundSource.PLAYERS);
        }
        return false;
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("damaged", damaged);
        nbt.putBoolean("aoj", aoj);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.damaged = nbt.getBoolean("damaged");
        this.aoj = nbt.getBoolean("aoj");
    }
}
