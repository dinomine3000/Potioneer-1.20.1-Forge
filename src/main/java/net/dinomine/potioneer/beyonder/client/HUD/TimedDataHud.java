package net.dinomine.potioneer.beyonder.client.HUD;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public abstract class TimedDataHud extends AbilityDataHud {
    protected float timeActive = 5;
    protected long timestamp = -1;

    public TimedDataHud(float timeActive){this.timeActive = timeActive;}

    @Override
    boolean shouldRender(Minecraft instance, LocalPlayer player) {
        if(timestamp < 0) return false;
        return timeActive > (System.currentTimeMillis() - timestamp)/1000f;
    }

    @Override
    public void trigger() {
        timestamp = System.currentTimeMillis();
    }
}
