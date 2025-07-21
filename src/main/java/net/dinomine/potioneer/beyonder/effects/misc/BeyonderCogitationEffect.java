package net.dinomine.potioneer.beyonder.effects.misc;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class BeyonderCogitationEffect extends BeyonderEffect {
    private List<String> deactivatedAbilities = new ArrayList<>();

    public BeyonderCogitationEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Cogitation Effect";
    }


    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player && deactivatedAbilities.isEmpty()){
            deactivatedAbilities = cap.getAbilitiesManager().disabledALlAbilities(player, "cogitation");
        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 3, true, true));

        if(!target.hasEffect(MobEffects.DARKNESS))
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, -1, 3, true, true));

        if(!target.hasEffect(MobEffects.WEAKNESS))
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, -1, 3, true, true));

        if(!target.hasEffect(MobEffects.GLOWING))
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 3, true, true));
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        target.removeEffect(MobEffects.DARKNESS);
        target.removeEffect(MobEffects.WEAKNESS);
        target.removeEffect(MobEffects.GLOWING);

        if(target instanceof Player player)
            cap.getAbilitiesManager().reactivateAbilities(player, deactivatedAbilities);
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        ListTag deactivated = new ListTag();
        for(String ablID: deactivatedAbilities){
            deactivated.add(StringTag.valueOf(ablID));
        }
        nbt.put("deactivated", deactivated);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);

        deactivatedAbilities = new ArrayList<>();
        ListTag deactivated = nbt.getList("deactivated", ListTag.TAG_STRING);
        for(Tag tag: deactivated){
            if(tag instanceof StringTag ablTag){
                String ablId = ablTag.getAsString();
                deactivatedAbilities.add(ablId);
            }
        }
    }
}
