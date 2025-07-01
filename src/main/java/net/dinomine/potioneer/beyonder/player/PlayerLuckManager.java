package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.player.luck.LuckRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

public class PlayerLuckManager {
    //corresponds to 20 minutes irl (ticks once every 10 seconds -> 1200 seconds = 20 mins)
    private static final int FORTUNATE_EVENT_THRESHOLD = 100;
    private static final int UNFORTUNATE_EVENT_THRESHOLD = -100;
    public static final int MAXIMUM_LUCK = 1000;
    public static final int MINIMUM_LUCK = -1000;

    private boolean eventGoingOn = false;
    private int luckEventCountdown;
    private int luck;
    private int tick = 0;
    private LuckRange range;

    public PlayerLuckManager(){
        this.luck = 0;
        Random random = new Random();
        this.range = new LuckRange(random.nextInt(20, 50), random.nextInt(20, 50));
        luckEventCountdown = 1;
    }

    public void onTick(EntityBeyonderManager cap, LivingEntity target){
        //ticks once every 10 seconds
        if(tick++ > 400){
            tick = 0;
            if(eventGoingOn){
                luckEventCountdown--;

                if(luckEventCountdown < 1){
                    triggerEvent(cap, target);
                    eventGoingOn = false;
                }

            } else {
                if(luck > FORTUNATE_EVENT_THRESHOLD || luck < UNFORTUNATE_EVENT_THRESHOLD){
                    castEvent(target);
                    target.sendSystemMessage(Component.literal("The gears of fate turn to you"));
                }
            }
            //random walk
            luck = range.changeLuck(luck, target.getRandom().nextBoolean() ? 1 : -1);
            range.tenSecondTick();

        }
    }

    public LuckRange getRange(){
        return range;
    }

    public void changeLuckTemporary(int minDelta, int maxDelta, int posDelta){
        range.changeDecayRange(minDelta, maxDelta, posDelta);
    }

    public void changeLuckRange(int minDelta, int maxDelta, int posDelta){
        range.changeRange(minDelta, maxDelta, posDelta);
    }

    private void castEvent(LivingEntity target){
        eventGoingOn = true;
//      luckEventCountdown = target.getRandom().nextInt(10*6);
        luckEventCountdown = target.getRandom().nextInt(1);
    }

    public void instantlyCastEvent(LivingEntity target){
        castEvent(target);
        luckEventCountdown = 0;
    }

    public int getMaxPassiveLuck(){
        return range.getMaxLuck();
    }

    public int getMinPassiveLuck(){
        return range.getMinLuck();
    }

    public int getLuck(){
        return this.luck;
    }

    private void triggerEvent(EntityBeyonderManager cap, LivingEntity target){
        int event = target.getRandom().nextInt(3);
        if(luck > 0){
            target.sendSystemMessage(Component.literal("Fate bestows a blessing upon thee."));
            consumeLuck(40);
            switch(event){
                case 0:
                    if(target instanceof Player player){
                        player.addItem(
                                new ItemStack(Items.DIAMOND).copyWithCount(target.getRandom().nextInt(5))
                        );
                    }
                    break;
                case 1:
                    target.addEffect(new MobEffectInstance(MobEffects.HEAL, 100, 0, false, false));
                    break;
                case 2:
                    if(target instanceof Player player){
                        player.giveExperienceLevels(target.getRandom().nextInt(3));
                    }
            }
        } else {
            grantLuck(15);
            target.sendSystemMessage(Component.literal("Fate curses thee."));
            switch(event){
                case 0:
                    if(target instanceof Player player){
                        ItemStack stack = target.getMainHandItem();
                        ItemStack copy = stack.copy();
                        stack.setCount(0);
                        player.drop(copy, false);
                    }
                    break;
                case 1:
                    target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 2, false, false));
                    break;
                case 2:
                    if(target instanceof Player player){
                        player.hurt(player.damageSources().generic(), player.getRandom().nextFloat()*5);
                    }
            }
        }
    }

    public float checkLuck(float chance){
        float newChance;
        float b = 3.6f;
        float d = 20f;
        if(luck == 0) newChance = chance;
        else if(luck > 0){
            float a = b * (float) Math.pow(10, luck/100f - 1);
            newChance = (float) (Math.log(a*chance + 1) / Math.log(a + 1));
        } else {
            float c = luck / (d - luck);
            newChance = (float) (Math.log(c*chance + 1) / Math.log(c+1));
        }
        return newChance;
    }

    public boolean passesLuckCheck(float chance, int luckCostIfSuccess, int luckGainIfFailure, RandomSource random){
        float newChance = checkLuck(chance);
        if(random.nextFloat() < newChance){
            consumeLuck(luckCostIfSuccess);
            return true;
        }
        grantLuck(luckGainIfFailure);
        return false;
    }

    public void consumeLuck(int consume){
        luck = Mth.clamp(luck - consume, MINIMUM_LUCK, MAXIMUM_LUCK);
    }
    public void grantLuck(int amm){ luck = Mth.clamp(luck + amm, MINIMUM_LUCK, MAXIMUM_LUCK);}

    public void saveNBTData(CompoundTag nbt){
        CompoundTag luck = new CompoundTag();
        luck.putInt("luck", this.luck);
        luck.putInt("luck_countdown", this.luckEventCountdown);
        luck.put("range_data", range.saveNBTData(new CompoundTag()));
        luck.putBoolean("event_on", this.eventGoingOn);
        nbt.put("luck_status", luck);
    }

    public void loadNBTData(CompoundTag nbt){
        CompoundTag tag = nbt.getCompound("luck_status");
        this.luck = tag.getInt("luck");
        if(tag.contains("range_data"))
            this.range.loadNBTData(tag.getCompound("range_data"));
        this.luckEventCountdown = tag.getInt("luck_countdown");
        this.eventGoingOn = tag.getBoolean("event_on");
    }

    public void copyFrom(PlayerLuckManager luckManager) {
        this.range = luckManager.range;
        this.luck = luckManager.luck;
    }
}
