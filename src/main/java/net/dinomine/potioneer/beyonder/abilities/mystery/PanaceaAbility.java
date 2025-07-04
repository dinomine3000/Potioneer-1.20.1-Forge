package net.dinomine.potioneer.beyonder.abilities.mystery;

import com.mojang.datafixers.util.Pair;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.misc.BeyonderPlagueEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanaceaAbility extends Ability {

    public PanaceaAbility(int sequence){
        this.info = new AbilityInfo(57, 272, "Cure All", 20 + sequence, 100*(9-sequence), 20*10, "panacea");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost() && !target.getActiveEffects().isEmpty()){
            boolean flag = false;

            List<MobEffectInstance> effectsToRemove = new ArrayList<>(target.getActiveEffects());

            for (MobEffectInstance effect : effectsToRemove) {
                if(effect.getEffect().getCategory() == MobEffectCategory.HARMFUL){
                    target.removeEffect(effect.getEffect());
                    flag = true;
                }
            }
            return flag;
        }
        return false;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
    }
}
