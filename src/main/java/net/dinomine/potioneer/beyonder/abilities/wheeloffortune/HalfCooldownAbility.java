package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

public class HalfCooldownAbility extends Ability {

    public HalfCooldownAbility(int sequence){
        super(sequence);
        defaultMaxCooldown = 60*20;
        setCost(level -> 50+20*Math.max(0, 6 - level));
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return sequenceLevel < 6 ? "refresh_cooldown" : "half_cooldown";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return cap.getSpirituality() > cost();
        boolean flag = false;
        if(sequenceLevel > 5 || !(target instanceof Player player)){
            flag = refreshAbilityCooldown(cap, target, sequenceLevel);
        } else {
            List<Player> allies = AbilityFunctionHelper.getAlliesOf((ServerLevel) target.level(), player);
            for(Player ally: allies){
                Optional<BeyonderCapability> optCap = ally.getCapability(CapProvider.BEYONDER_STATS).resolve();
                if(optCap.isEmpty()) continue;
                BeyonderCapability allyCap = optCap.get();
                flag = flag || refreshAbilityCooldown(allyCap, ally, sequenceLevel);
            }
        }
        if(flag) cap.requestActiveSpiritualityCost(cost());
        return flag;
    }

    private static boolean refreshAbilityCooldown(BeyonderCapability cap, LivingEntity target, int sequenceLevel){
        List<AbilityKey> keys = cap.getAbilitiesManager().getAbilityKeys();
        boolean flag = false;
        for(AbilityKey key: keys){
            if(cap.getAbilitiesManager().getAbility(key).getCooldown() > 0){
                cap.getAbilitiesManager().putAbilityOnCooldown(key, sequenceLevel < 6 ? 0 : cap.getAbilitiesManager().getAbility(key).getCooldown()/2, target);
                flag = true;
            }
        }
        return flag;
    }
}
