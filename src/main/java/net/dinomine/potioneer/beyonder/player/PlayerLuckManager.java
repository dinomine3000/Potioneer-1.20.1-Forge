package net.dinomine.potioneer.beyonder.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class PlayerLuckManager {
    private static final int FORTUNATE_EVENT_THRESHOLD = 100;
    private static final int UNFORTUNATE_EVENT_THRESHOLD = -100;

    private boolean eventGoingOn = false;
    private int luckEventCountdown;
    private int luck;
    private int tick = 0;

    public PlayerLuckManager(){
        this.luck = 0;
        luckEventCountdown = -1;
    }

    public void onTick(EntityBeyonderManager cap, LivingEntity target){
        if(tick++ > 400){
            tick = 0;
            if(eventGoingOn){
                luckEventCountdown--;

                if(luckEventCountdown == 0){
                    eventGoingOn = false;
                }

            } else {

            }
//            if(!hasEvent()){
//                if(luck > FORTUNATE_EVENT_THRESHOLD){
//                    createFortunateEvent(luck);
//                }
//                else if (luck < UNFORTUNATE_EVENT_THRESHOLD){
//                    createMisfortunateEvent(luck);
//                }
//            }
            if(luck < 50){
                luck++;
//                System.out.println("Luck updated to: " + luck);
            }
        }
    }

    public float checkLuck(float chance){
        float newChance;
        if(luck == 0) newChance = chance;
        else if(luck > 0){
            float a = 3.6f * (float) Math.pow(10, luck/100f - 1);
            newChance = (float) (Math.log(a*chance + 1) / Math.log(a + 1));
        } else {
            float c = chance / (20 - chance);
            newChance = (float) (Math.log(c*chance) / Math.log(c+1));
        }
        return newChance;
    }

    public void consumeLuck(int consume){
        luck -= consume;
    }

    public void saveNBTData(CompoundTag nbt){
        CompoundTag luck = new CompoundTag();
        luck.putInt("luck", this.luck);
        nbt.put("luck_status", luck);
    }

    public void loadNBTData(CompoundTag nbt){
        CompoundTag tag = nbt.getCompound("luck_status");
        this.luck = tag.getInt("luck");
    }
}
