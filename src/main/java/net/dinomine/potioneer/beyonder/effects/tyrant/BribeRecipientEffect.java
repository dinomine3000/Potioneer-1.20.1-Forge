package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class BribeRecipientEffect extends BeyonderEffect {
    protected String type = "";
    protected UUID ownerId = null;

    public @Nullable Entity getTribunal(ServerLevel level){
        if(ownerId == null || !type.equalsIgnoreCase("disorder")) return null;
        //noinspection SSBasedInspection
        return level.getEntity(ownerId);
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(calledOnVictim || !type.equalsIgnoreCase("weakening")) return false;
        if(!victim.getUUID().equals(ownerId)) return false;
        event.setAmount((float) (event.getAmount() * PotioneerAbilityConfig.BRIBE_DAMAGE_MULTIPLIER.get()));
        return false;
    }

    public boolean isTruce(LivingEntity ent2) {
        return type.equalsIgnoreCase("truce") && ownerId.equals(ent2.getUUID());
    }
}
