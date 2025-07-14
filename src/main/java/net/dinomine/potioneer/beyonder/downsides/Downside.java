package net.dinomine.potioneer.beyonder.downsides;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public abstract class Downside extends Ability {
    public Downside(String name, int cost, boolean active, String id, int sequenceLevel){
        this.info = new AbilityInfo(130, 4, name, sequenceLevel, cost, 20, "d_" + id);
        this.isActive = active;
    }

    public void copyCd(int newCd){
        this.info = this.info.copy(newCd);
    }
}
