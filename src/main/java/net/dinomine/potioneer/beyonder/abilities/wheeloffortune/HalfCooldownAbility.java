package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

public class HalfCooldownAbility extends Ability {
    private int cost = 0;

    @Override
    public void init() {
        cost = 50+20*Math.max(0, 6 - getSequenceLevel());
        defaultMaxCooldown = 60*20;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return sequenceLevel < 6 ? "refresh_cooldown" : "half_cooldown";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return cap.getSpirituality() > cost;
        boolean flag = false;
        if(getSequenceLevel() > 5 || !(target instanceof Player player)){
            flag = refreshAbilityCooldown(cap, target, getSequenceLevel());
        } else {
            List<Player> allies = AbilityFunctionHelper.getAlliesOf((ServerLevel) target.level(), player);
            for(Player ally: allies){
                Optional<BeyonderCapability> optCap = ally.getCapability(CapProvider.BEYONDER_STATS).resolve();
                if(optCap.isEmpty()) continue;
                BeyonderCapability allyCap = optCap.get();
                flag = flag || refreshAbilityCooldown(allyCap, ally, getSequenceLevel());
            }
        }
        if(flag) cap.requestActiveSpiritualityCost(cost);
        return flag;
    }

    private static boolean refreshAbilityCooldown(BeyonderCapability cap, LivingEntity target, int sequenceLevel){
        List<Ability> abilities = cap.getAbilitiesManager().getAllAbilities();
        boolean flag = false;
        for(Ability ability: abilities){
            if(ability.getCooldown() > 0){
                ability.putOnCooldown(sequenceLevel < 6 ? 0 : ability.getCooldown()/2, target);
                flag = true;
            }
        }
        return flag;
    }
}
