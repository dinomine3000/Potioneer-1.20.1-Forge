package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.entities.custom.CloneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CloneAbility extends Ability {

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
        System.out.println(player.getGameProfile());
        if(target.level().isClientSide()) return true;
        CloneEntity clone = CloneEntity.clone(player, (ServerLevel) player.level());
        clone.setPos(player.position());
        target.level().addFreshEntity(clone);
        return true;
    }
}
