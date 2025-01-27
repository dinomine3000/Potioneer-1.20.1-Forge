package net.dinomine.potioneer.beyonder.player;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class BeyonderStats {
    private float miningSpeedMult = 1;
    private float damageBonus = 1;
    private boolean mayFly = false;
    private AABB boundingBox;

    public void onTick(EntityBeyonderManager cap, LivingEntity target){
        if(target instanceof Player player && boundingBox != null){
            player.setBoundingBox(getBoundingBox());
        }
    }

    public void setBoundingBox(AABB box){
        this.boundingBox = box;
    }

    public AABB getBoundingBox(){
        return this.boundingBox;
    }

    public float getDamageBonus() {
        return damageBonus;
    }

    public void multDamageBonus(float amount) {
        this.damageBonus *= amount;
    }

    BeyonderStats(){
        resetStats();
    }

    public void enableFlight(){
        mayFly = true;
    }

    public boolean canFly(){
        return mayFly;
    }

    public void setMiningSpeed(float mult){
        this.miningSpeedMult = mult;
    }

    public void getMiningSpeed(PlayerEvent.BreakSpeed event){
        event.setNewSpeed(event.getOriginalSpeed()*miningSpeedMult);
    }

    public float getMiningSpeed(){
        return miningSpeedMult;
    }

    public void multMiningSpeed(float mult){this.miningSpeedMult *= mult;}

    public void resetStats(){
        miningSpeedMult = 1;
        mayFly = false;
        damageBonus = 1;
        boundingBox = null;
    }

    public void setStats(BeyonderStats oldStore){
        this.miningSpeedMult = oldStore.miningSpeedMult;
        this.damageBonus = oldStore.damageBonus;
        this.mayFly = oldStore.mayFly;
        this.boundingBox = oldStore.getBoundingBox();
    }
}
