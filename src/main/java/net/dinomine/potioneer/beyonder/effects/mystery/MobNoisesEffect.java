package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class MobNoisesEffect extends BeyonderEffect {
    private static final List<SoundEvent> POSSIBLE_SOUNDS = List.of(
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.WARDEN_AMBIENT,
            SoundEvents.WARDEN_EMERGE,
            SoundEvents.CREEPER_HURT,
            SoundEvents.SKELETON_SHOOT,
            SoundEvents.SKELETON_STEP,
            SoundEvents.SKELETON_AMBIENT,
            SoundEvents.ZOMBIE_VILLAGER_AMBIENT,
            SoundEvents.ZOMBIE_AMBIENT,
            SoundEvents.PHANTOM_AMBIENT,
            SoundEvents.PHANTOM_FLAP,
            SoundEvents.PHANTOM_SWOOP,
            SoundEvents.SPIDER_STEP,
            SoundEvents.SPIDER_STEP,
            SoundEvents.ENDERMAN_AMBIENT,
            SoundEvents.ENDERMAN_SCREAM,
            SoundEvents.ENDERMAN_STARE,
            SoundEvents.ENDERMAN_HURT,
            SoundEvents.BAT_AMBIENT,
            SoundEvents.BAT_AMBIENT,
            SoundEvents.BAT_AMBIENT,
            SoundEvents.GHAST_AMBIENT,
            SoundEvents.GHAST_SCREAM,
            SoundEvents.STONE_STEP,
            SoundEvents.STONE_STEP,
            SoundEvents.STONE_STEP,
            SoundEvents.STONE_STEP,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON
    );
    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(target.getRandom().nextInt(100) != 5) return;
        target.playSound(POSSIBLE_SOUNDS.get(target.getRandom().nextInt(POSSIBLE_SOUNDS.size())));
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }
}
