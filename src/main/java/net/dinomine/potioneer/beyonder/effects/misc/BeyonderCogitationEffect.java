package net.dinomine.potioneer.beyonder.effects.misc;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.DisabledAbilitiesManager;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderCogitationEffect extends BeyonderEffect {
    private boolean slownlessCheck = false;
    private boolean darknessCheck = false;
    private boolean weaknessCheck = false;
    private boolean glowingCheck = false;
    public ResourceLocation ablId = null;

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
//        this.name = "Cogitation Effect";
//        if(target instanceof Player player && deactivatedAbilities.isEmpty()){
//            deactivatedAbilities = cap.getAbilitiesManager().disabledAllAbilities(player, "cogitation");
//        }
        if(ablId == null) throw new RuntimeException("[Potioneer] tried to add a cogitation effect with no reference to its casting ability");
        cap.getAbilitiesManager().getDisabledAbilitiesManager().disableAbility("cogitation", DisabledAbilitiesManager.DisabledAbilityProxy.all(-1, ablId), cap, target);
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        cap.requestPassiveSpiritualityCost(-(cap.getMaxSpirituality()/60f));
        if(!target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)){
            slownlessCheck = true;
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 3, true, true));
        }

        if(!target.hasEffect(MobEffects.DARKNESS)){
            darknessCheck = true;
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, -1, 3, true, true));
        }

        if(!target.hasEffect(MobEffects.WEAKNESS)){
            weaknessCheck = true;
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, -1, 3, true, true));
        }

        if(!target.hasEffect(MobEffects.GLOWING)){
            glowingCheck = true;
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 3, true, true));
        }
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        if(slownlessCheck) target.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if(darknessCheck) target.removeEffect(MobEffects.DARKNESS);
        if(weaknessCheck) target.removeEffect(MobEffects.WEAKNESS);
        if(glowingCheck) target.removeEffect(MobEffects.GLOWING);
        cap.getAbilitiesManager().getDisabledAbilitiesManager().enableAbility("cogitation", cap, target);
    }
//
    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("slowlness", slownlessCheck);
        nbt.putBoolean("darkness", darknessCheck);
        nbt.putBoolean("weakness", weaknessCheck);
        nbt.putBoolean("glowing", glowingCheck);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        darknessCheck = nbt.getBoolean("darkness");
        slownlessCheck = nbt.getBoolean("slowlness");
        weaknessCheck = nbt.getBoolean("weakness");
        glowingCheck = nbt.getBoolean("glowing");
    }
}
