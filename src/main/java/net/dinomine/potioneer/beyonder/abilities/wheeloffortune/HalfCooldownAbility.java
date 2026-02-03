package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.OpenScreenMessage;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class HalfCooldownAbility extends Ability {

    public HalfCooldownAbility(int sequence){
        super(sequence);
        defaultMaxCooldown = 60*20;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return sequenceLevel < 6 ? "refresh_cooldown" : "half_cooldown";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return cap.getSpirituality() > cost();
        boolean flag = false;
        if(sequenceLevel > 5 || !(target instanceof Player player)){
            flag = refreshAbilityCooldown(cap, target, sequenceLevel);
        } else {
            List<UUID> allies = AllySystemSaveData.from((ServerLevel) target.level()).getAlliesOf(player.getUUID());
            for(UUID id: allies){
                Player ally = target.level().getPlayerByUUID(id);
                LivingEntityBeyonderCapability allyCap = ally.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
                refreshAbilityCooldown(allyCap, ally, sequenceLevel);
            }
        }
        if(flag) cap.requestActiveSpiritualityCost(cost());
        return flag;
    }

    private static boolean refreshAbilityCooldown(LivingEntityBeyonderCapability cap, LivingEntity target, int sequenceLevel){
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
