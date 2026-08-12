package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.TyrantPathway;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.util.ModTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;

public class ArrestSourceEffect extends BeyonderEffect {
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> optAttackerCap, boolean calledOnVictim) {
        if(victim.level().isClientSide() || calledOnVictim || attacker == null) return false;
        boolean aoj = AreaOfJurisdictionAbility.isTargetUnderInfluenceOfEnforcer(victim, attacker);
        ItemStack weapon = attacker.getMainHandItem();
        if(!weapon.is(ModTags.Items.ENFORCER_ARREST_TOOLS)) weapon = attacker.getOffhandItem();
        if(!weapon.is(ModTags.Items.ENFORCER_ARREST_TOOLS)) return false;
        if(attacker instanceof Player playerAttacker){
            if(playerAttacker.getCooldowns().isOnCooldown(weapon.getItem())) return false;
        }

        if(victimCap == null) return false;
        applyArrestToRecipient(attacker, victimCap, victim, getSequenceLevel(), aoj);
        if(attacker instanceof Player playerAttacker){
            playerAttacker.getCooldowns().addCooldown(weapon.getItem(), 10*20);
        }
        optAttackerCap.ifPresent(cap -> {
            cap.requestActiveSpiritualityCost(cost);
            cap.getCharacteristicManager().progressActing(TyrantPathway.ENFORCER_ACTING_ARREST, 17);
            }
        );
        return false;
    }

    public static void applyArrestToRecipient(LivingEntity attacker, BeyonderCapability victimCap, LivingEntity victim, int sequenceLevel, boolean aoj){
        ArrestRecipientEffect eff = (ArrestRecipientEffect) BeyonderEffects.TYRANT_ARREST_RECIPIENT.createInstance(sequenceLevel, 0, aoj ? 7*20 : 3*20, true);
        eff.setEnforcer(attacker.getUUID());
        victimCap.getEffectsManager().addOrRefreshEffect(eff, victimCap, victim);
    }

    public static void applyArrestToRecipient(LivingEntity attacker, LivingEntity victim, int sequenceLevel, boolean aoj){
        if(CapProvider.beyonder(victim).isEmpty()) return;
        applyArrestToRecipient(attacker, CapProvider.beyonder(victim).get(), victim, sequenceLevel, aoj);
    }
}
