package net.dinomine.potioneer.beyonder.effects.misc;

import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class BeyonderCogitationEffect extends BeyonderEffect {
    private List<AbilityKey> deactivatedAbilities = new ArrayList<>();
    private boolean slownlessCheck = false;
    private boolean darknessCheck = false;
    private boolean weaknessCheck = false;
    private boolean glowingCheck = false;

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        this.name = "Cogitation Effect";
//        if(target instanceof Player player && deactivatedAbilities.isEmpty()){
//            deactivatedAbilities = cap.getAbilitiesManager().disabledAllAbilities(player, "cogitation");
//        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
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
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(slownlessCheck) target.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if(darknessCheck) target.removeEffect(MobEffects.DARKNESS);
        if(weaknessCheck) target.removeEffect(MobEffects.WEAKNESS);
        if(glowingCheck) target.removeEffect(MobEffects.GLOWING);

        cap.getAbilitiesManager().unrevokeAll(cap, target);
    }
//
//    @Override
//    public void toNbt(CompoundTag nbt) {
//        super.toNbt(nbt);
//        ListTag deactivated = new ListTag();
//        for(String ablID: deactivatedAbilities){
//            deactivated.add(StringTag.valueOf(ablID));
//        }
//        nbt.put("deactivated", deactivated);
//    }
//
//    @Override
//    public void loadNBTData(CompoundTag nbt) {
//        super.loadNBTData(nbt);
//
//        deactivatedAbilities = new ArrayList<>();
//        ListTag deactivated = nbt.getList("deactivated", ListTag.TAG_STRING);
//        for(Tag tag: deactivated){
//            if(tag instanceof StringTag ablTag){
//                String ablId = ablTag.getAsString();
//                deactivatedAbilities.add(ablId);
//            }
//        }
//    }
}
