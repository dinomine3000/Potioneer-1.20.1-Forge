package net.dinomine.potioneer.beyonder.player;

import net.minecraftforge.event.entity.player.PlayerEvent;

public class BeyonderStats {
    private float miningSpeedMult = 1;
    private boolean mayFly = false;

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
    }

    public void setStats(BeyonderStats oldStore){
        this.miningSpeedMult = oldStore.miningSpeedMult;
        this.mayFly = oldStore.mayFly;
    }
}
