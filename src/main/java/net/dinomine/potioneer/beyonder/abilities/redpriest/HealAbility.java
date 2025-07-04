package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.Optional;

public class HealAbility extends Ability {

    public HealAbility(int sequence){
        this.info = new AbilityInfo(83, 152, "Healing", 30 + sequence, 20, 20*10, "heal");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        ArrayList<Entity> hits = AbilityFunctionHelper.getLivingEntitiesLooking(target, target.getAttributeValue(ForgeMod.ENTITY_REACH.get()) + 0.5f);
        hits.sort((a, b) -> (int) (a.position().distanceTo(target.position()) - b.position().distanceTo(target.position())));
        boolean flag = false;
        for(Entity ent: hits){
            Vec3 pos = ent.position();
            if(ent instanceof ZombieVillager zombie){
                flag = true;
                zombie.startConverting(target instanceof Player player ? player.getUUID() : null, 20*15);
                for(int i = 0; i < 5; i++){
                    target.level().addParticle(ParticleTypes.END_ROD,
                            pos.x + 0.5f - target.getRandom().nextFloat(), pos.y + target.getRandom().nextFloat(), pos.z + 0.5f - target.getRandom().nextFloat(),
                            (0.5f-target.getRandom().nextFloat())/2f, 0.2f, (0.5f-target.getRandom().nextFloat())/2f);
                }
            }
            else if(ent instanceof LivingEntity livingEntity){
                flag = true;
                livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2*20, 2, true, true));

                for(int i = 0; i < 5; i++){
                    target.level().addParticle(ParticleTypes.HEART,
                            pos.x + 0.5f - target.getRandom().nextFloat(), pos.y + target.getRandom().nextFloat(), pos.z + 0.5 - target.getRandom().nextFloat(),
                            0, 0.2f, 0);
                }
            }
        }
        if(flag){
            cap.requestActiveSpiritualityCost(info.cost());
        }
        return flag;
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
