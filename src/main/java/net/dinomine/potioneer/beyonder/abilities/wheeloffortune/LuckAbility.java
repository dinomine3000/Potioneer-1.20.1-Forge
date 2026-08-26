package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.LuckEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public class LuckAbility extends PassiveAbility {
    private static final int cost = 150;
    public LuckAbility(){
//        this.info = new AbilityInfo(5, 128, "Luck Boost", sequence, 30 + 10*(9-sequence), 20*60, "luck_boost");
        super(BeyonderEffects.WHEEL_LUCK_EFFECT, ignored -> "luck");
    }

    @Override
    public void init() {
        enabledOnAcquire();
        defaultMaxCooldown = 20*250;
    }

    @Override
    protected BeyonderEffect createEffectInstance() {
        LuckEffect eff = (LuckEffect) BeyonderEffects.WHEEL_LUCK_EFFECT.createInstance(getSequenceLevel(), 0, -1, true);
        return eff.withCrit();
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= cost) return true;

        if(cap.getSpirituality() >= cost){
            RandomSource random = target.getRandom();
            target.level().playSound(null, target.getOnPos(), ModSounds.LUCK.get(), SoundSource.PLAYERS, 2, (float) target.getRandom().triangle(1f, 0.3f));
            cap.getLuckManager().changeLuckTemporary(-random.nextInt(0, 50), -random.nextInt(0, 50), random.nextInt(0, 50));
            cap.getLuckManager().grantLuck(target, 20, false);
            target.sendSystemMessage(Component.translatable("ability.potioneer.decaying_luck"));
            cap.requestActiveSpiritualityCost(cost);
            return true;
        }
        return false;
    }
}
