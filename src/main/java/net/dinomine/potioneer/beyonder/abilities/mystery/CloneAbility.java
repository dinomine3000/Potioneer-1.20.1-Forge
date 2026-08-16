package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.mystery.IllusionEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.entities.custom.CloneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class CloneAbility extends Ability {
    public static final Supplier<Integer> MAX_CLONE_DIST = () -> 16;

    public CloneAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "clone";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(!(target instanceof Player player)) return false;
        IllusionEffect existingEffect = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.MYSTERY_ILLUSION.getEffectId(), target);
        if(existingEffect != null){
            existingEffect.invalidateEffect(target);
            return true;
        }
        if(target.level().isClientSide()) return true;
        CloneEntity clone = CloneEntity.clone(player, (ServerLevel) player.level());
        clone.setCloneHealth(target.getMaxHealth(), target.getHealth());
        clone.type = CloneEntity.Type.INHABIT;
        clone.setPos(player.position());
        target.level().addFreshEntity(clone);
        //overrideTargets(target, clone);

        IllusionEffect eff = (IllusionEffect) BeyonderEffects.MYSTERY_ILLUSION.createInstance(sequenceLevel, 0, -1, true);
        eff.setClone(clone);
        cap.getEffectsManager().addOrReplaceEffect(eff, cap, target);

        return true;
    }

    private void overrideTargets(LivingEntity caster, CloneEntity clone){
        AbilityFunctionHelper.getLivingEntitiesAround(caster, 32).forEach(ent -> {
            if(ent instanceof Monster monster){
                LivingEntity monsterTarget = monster.getTarget();
                if(monsterTarget != null && monsterTarget.is(caster)) monster.setTarget(clone);
            }
        });
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        if(!(target instanceof Player player)) return false;
        if(target.level().isClientSide()) return true;

        CloneEntity clone = CloneEntity.clone(player, (ServerLevel) player.level());
        clone.setPos(player.position());
        clone.setCloneHealth(1f, 1f);
        clone.type = CloneEntity.Type.INVISIBLE;
        target.level().addFreshEntity(clone);

        overrideTargets(target, clone);

        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.MYSTERY_INVISIBLE.createInstance(sequenceLevel, 0, 20*15, true), cap, target);
        return true;
    }
}
