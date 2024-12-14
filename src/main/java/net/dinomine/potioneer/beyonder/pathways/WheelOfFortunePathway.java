package net.dinomine.potioneer.beyonder.pathways;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class WheelOfFortunePathway extends Beyonder{

    public WheelOfFortunePathway(int sequence) {
        super(sequence);
    }

    @Override
    public String getName(int seq){
        return switch (seq) {
            case 9 -> "Miner";
            case 8 -> "Appraiser";
            default -> "";
        };
    }

    @Override
    public int getId() {
        return 10 + sequence;
    }

    @Override
    public float getMiningSpeedMult() {
        return 5;
    }

    @Override
    public void onTick(Player player) {
        super.onTick(player);
        switch(this.sequence){
            case 8:
                onTick8();
            case 9:
                onTick9(player);

        }

    }

    private void onTick8() {
    }

    private void onTick9(Player player) {
        if(!player.hasEffect(MobEffects.NIGHT_VISION)){
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false));
        }
    }
}
