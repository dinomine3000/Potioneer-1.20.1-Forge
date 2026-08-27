package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.mystery.BugAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BugDefenseEffect extends BeyonderEffect {
    private boolean redirecting = false;

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void setPriority(int sequenceLevel) {
        this.priority = Priority.VERY_HIGH;
    }

    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(victim.level().isClientSide()) return false;
        if(!calledOnVictim) return false;
        if(redirecting) return false;
        List<BugAbility> abls = victimCap.getAbilitiesManager().getAllAbilities(Abilities.BUG.get().getAblId()).stream().map(abl -> (BugAbility) abl).toList();
        for(BugAbility abl: abls){
            if(abl.isEnabled() && abl.isTargetBugged((ServerLevel) victim.level(), victim)){
                LivingEntity ent = abl.getBug((ServerLevel) victim.level());
                if(ent == null) continue;
                redirecting = true;
                ent.hurt(event.getSource(), event.getAmount());
                redirecting = false;
                return true;
            }
        }
        return false;
    }
}
