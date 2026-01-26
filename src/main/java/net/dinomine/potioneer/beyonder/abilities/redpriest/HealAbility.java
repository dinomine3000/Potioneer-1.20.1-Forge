package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
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

public class HealAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "heal";
    }

    public HealAbility(int sequence){
//        this.info = new AbilityInfo(83, 152, "Healing", 30 + sequence, 20, 20*10, "heal");
        super(sequence);
        setCost(ignored -> 20);
        defaultMaxCooldown = 20*10;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        ArrayList<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesLooking(target, target.getAttributeValue(ForgeMod.ENTITY_REACH.get()) + 0.5f);
        hits.sort((a, b) -> (int) (a.position().distanceTo(target.position()) - b.position().distanceTo(target.position())));
        boolean flag = false;
        for(LivingEntity ent: hits){
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
            flag = true;
            ent.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 5*20, 2, true, true));

            for(int i = 0; i < 5; i++){
                target.level().addParticle(ParticleTypes.HEART,
                        pos.x + 0.5f - target.getRandom().nextFloat(), pos.y + target.getRandom().nextFloat(), pos.z + 0.5 - target.getRandom().nextFloat(),
                        0, 0.2f, 0);
            }
        }
        if(flag){
            cap.requestActiveSpiritualityCost(cost());
            return true;
        }
        return flag;
    }

}
