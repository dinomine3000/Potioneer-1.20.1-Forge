package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;

public class ArrestSourceEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, Optional<LivingEntityBeyonderCapability> optAttackerCap, boolean calledOnVictim) {
        if(victim.level().isClientSide() || calledOnVictim || attacker == null) return false;
        boolean aoj = AreaOfJurisdictionAbility.isTargetUnderInfluenceOfEnforcer(victim, attacker);
        ItemStack weapon = attacker.getMainHandItem();
        if(!weapon.is(ModTags.Items.ENFORCER_ARREST_TOOLS)) weapon = attacker.getOffhandItem();
        if(!weapon.is(ModTags.Items.ENFORCER_ARREST_TOOLS)) return false;
        if(attacker instanceof Player playerAttacker){
            if(playerAttacker.getCooldowns().isOnCooldown(weapon.getItem())) return false;
        }

        if(victimCap == null) return false;
        ArrestRecipientEffect eff = (ArrestRecipientEffect) BeyonderEffects.TYRANT_ARREST_RECIPIENT.createInstance(getSequenceLevel(), 0, aoj ? 7*20 : 3*20, true);
        eff.setEnforcer(attacker.getUUID());
        victimCap.getEffectsManager().addOrReplaceEffect(eff, victimCap, victim);
        if(attacker instanceof Player playerAttacker){
            playerAttacker.getCooldowns().addCooldown(weapon.getItem(), 10*20);
        }
        optAttackerCap.ifPresent(cap -> cap.requestActiveSpiritualityCost(cost));
        return false;
    }
}
