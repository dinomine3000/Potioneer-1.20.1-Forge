package net.dinomine.potioneer.beyonder.downsides;

import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class MobNoisesDownside extends Downside{
    private static final List<SoundEvent> POSSIBLE_SOUNDS = List.of(
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
            SoundEvents.ZOMBIE_ATTACK_IRON_DOOR,
            SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR,
            SoundEvents.PHANTOM_AMBIENT,
            SoundEvents.PHANTOM_FLAP,
            SoundEvents.PHANTOM_SWOOP,
            SoundEvents.SPIDER_AMBIENT,
            SoundEvents.SPIDER_STEP,
            SoundEvents.SPIDER_STEP,
            SoundEvents.PIG_AMBIENT,
            SoundEvents.COW_AMBIENT,
            SoundEvents.SHEEP_AMBIENT,
            SoundEvents.HORSE_AMBIENT,
            SoundEvents.ENDERMAN_AMBIENT,
            SoundEvents.ENDERMAN_SCREAM,
            SoundEvents.ENDERMAN_STARE,
            SoundEvents.ENDERMAN_HURT,
            SoundEvents.BAT_AMBIENT,
            SoundEvents.BAT_AMBIENT,
            SoundEvents.BAT_AMBIENT,
            SoundEvents.BLAZE_AMBIENT,
            SoundEvents.GHAST_AMBIENT,
            SoundEvents.GHAST_SCREAM,
            SoundEvents.LEVER_CLICK,
            SoundEvents.GRASS_STEP,
            SoundEvents.GRASS_STEP,
            SoundEvents.GRASS_STEP,
            SoundEvents.GRASS_STEP,
            SoundEvents.STONE_STEP,
            SoundEvents.STONE_STEP,
            SoundEvents.STONE_STEP,
            SoundEvents.STONE_STEP,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON
    );
    public MobNoisesDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.getRandom().nextInt(300) != 5) return;
        target.playSound(POSSIBLE_SOUNDS.get(target.getRandom().nextInt(POSSIBLE_SOUNDS.size())));
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_noises";
    }
}
