package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class JabEffect extends BeyonderEffect {
    private static final Supplier<Float> JAB_CHANCE = () -> 0.25f;

    private static final List<MobEffect> DEFAULT_BAD_EFFECTS = List.of(
            MobEffects.LEVITATION,
            MobEffects.CONFUSION,
            MobEffects.HUNGER,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.WITHER,
            MobEffects.POISON,
            MobEffects.POISON,
            MobEffects.WEAKNESS,
            MobEffects.WEAKNESS,
            MobEffects.SLOW_FALLING
    );

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        LivingEntity toJab;
        LivingEntity jabber;
        BeyonderCapability jabberCap;
        if(calledOnVictim){
            toJab = attacker;
            jabber = victim;
            jabberCap = victimCap;
        } else {
            toJab = victim;
            jabber = attacker;
            jabberCap = attackerCap.get();
        }
        if(toJab == null) return false;
        if(!jabberCap.getLuckManager().passesLuckCheck(JAB_CHANCE.get(), 0, 0, RandomSource.create())) return false;

        jabber.level().playSound(null, jabber.getOnPos(), ModSounds.JAB.get(), SoundSource.PLAYERS, 2f, (float) jabber.getRandom().triangle(1f, 0.2f));
        List<MobEffect> effects = PotioneerAbilityConfig.getConfiguredMobEffects();
        if(effects.isEmpty()){
            effects = DEFAULT_BAD_EFFECTS;
            System.out.println("[Potioneer] Warning - configured jab effects read as empty. If this was not intended, that might be a problem. Defaulting mob effects...");
        }
        MobEffect eff = effects.get(jabber.getRandom().nextInt(effects.size()));
        toJab.addEffect(new MobEffectInstance(eff, sequenceLevel < 8 ? 20*7 : 20*3, 0, true, true, true));
        jabberCap.requestActiveSpiritualityCost(cost);
        return false;
    }
}
