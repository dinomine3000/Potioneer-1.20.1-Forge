package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.mystery.BlinkAbility;
import net.dinomine.potioneer.beyonder.abilities.mystery.DoorOpeningAbility;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AutoBlinkEffect extends BeyonderEffect {
    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(!calledOnVictim || event.getSource().is(PotioneerDamage.Tags.ABSOLUTE) || attacker == null) return false;

        if(victimCap.getLuckManager().passesLuckCheck(0.2f, 0, 0, victim.getRandom())){
            if(victim.level().isClientSide()) return true;
            BlockPos targetPos = BlinkAbility.breadthFirstSearch(victim.getOnPos().offset(attacker.getRandom().nextInt(-2, 2), 0, attacker.getRandom().nextInt(-2, 2)), 2, victim.level());
            if(BlinkAbility.teleport(victim, (ServerLevel) victim.level(), targetPos, victim.getXRot(), victim.getYRot())) {
                event.setCanceled(true);
                return true;
            }
        }
        return false;
    }
}
