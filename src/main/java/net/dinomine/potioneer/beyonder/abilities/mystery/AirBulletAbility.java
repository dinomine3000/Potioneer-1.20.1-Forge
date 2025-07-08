package net.dinomine.potioneer.beyonder.abilities.mystery;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AirBulletAbility extends Ability {

    public AirBulletAbility(int sequence){
        this.info = new AbilityInfo(57, 56, "Air Bullet", 20 + sequence, 100*(8-sequence), 5*20, "air_bullet");
        this.isActive = true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getSpirituality() < getInfo().cost()) return false;
        Level level = target.level();
        if(level.isClientSide()){
            double dist = (9 - getSequence())*8 + 5;
            float temp = 0.7f;
            Vec3 lookAngle = target.getLookAngle();
            while(temp < dist){
                Vec3 itVector = target.getEyePosition().add(lookAngle.scale(temp));
                level.addParticle(ParticleTypes.POOF, itVector.x, itVector.y, itVector.z,0, -0.02f, 0);
                temp += 0.4f;
            }
        }
        else {
            cap.requestActiveSpiritualityCost(info.cost());
            int radius = (9 - getSequence())*8 + 5;
            ArrayList<Entity> hits = AbilityFunctionHelper.getLivingEntitiesLooking(target, radius);
            hits.forEach(ent -> {
                int pow = (9-getSequence());
                if(ent != target) ent.hurt(level.damageSources().indirectMagic(target, null),
                        (float) (0.384f*Math.pow(target.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue()*pow, 2) + 2.461f*pow + 3.938f));
            });
            level.playSound(null, target.getOnPos().above(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1, 1);
        }
        return true;
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
