package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.entities.custom.CloneEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class FakeDeathEffect extends BeyonderEffect {

    @Override
    protected void setPriority(int sequenceLevel) {
        priority = Priority.VERY_HIGH;
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(!(victim instanceof Player player) ||!calledOnVictim || event.getSource().is(PotioneerDamage.Tags.ABSOLUTE) || victim.level().isClientSide()) return false;
        CloneEntity clone = CloneEntity.clone(player, (ServerLevel) victim.level());
        clone.setPos(player.position());
        clone.type = CloneEntity.Type.DEATH;
        clone.setCloneHealth(player.getHealth(), player.getMaxHealth());
        player.level().addFreshEntity(clone);
        clone.hurt(event.getSource(), clone.getMaxHealth()*10);
        victimCap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.MYSTERY_INVISIBLE.createInstance(sequenceLevel, 0, 20*15, false), victimCap, victim);

        clone.getCombatTracker().recordDamage(event.getSource(), 1);
        Component deathMessage = clone.getCombatTracker().getDeathMessage();
        for (Player players : victim.level().players()) players.sendSystemMessage(deathMessage);
        endEffectWhenPossible();
        return true;
    }
}
