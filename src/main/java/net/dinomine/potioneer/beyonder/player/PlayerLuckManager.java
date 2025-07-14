package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.misc.ArtifactHelper;
import net.dinomine.potioneer.beyonder.player.luck.LuckRange;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.AsteroidEntity;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.Random;

public class PlayerLuckManager {
    //corresponds to 20 minutes irl (ticks once every 10 seconds -> 1200 seconds = 20 mins)
    private static final int LV1_THRESHOLD = 100;
    private static final int LV2_THRESHOLD = 100;
    private static final int LV3_THRESHOLD = 300;
    public static final int MAXIMUM_LUCK = 1000;
    public static final int MINIMUM_LUCK = -1000;

    private boolean eventGoingOn = false;
    private int luckEventCountdown;
    private int luckEventCd;
    private int luck;
    private int tick = 0;
    private LuckRange range;
    public int luckEventChance = 1;

    public PlayerLuckManager(){
        this.luck = 0;
        Random random = new Random();
        this.range = new LuckRange(random.nextInt(20, 50), random.nextInt(20, 50));
        luckEventCountdown = 1;
    }

    public void onTick(EntityBeyonderManager cap, LivingEntity target){
        //ticks once every 2 seconds
        if(target.level().isClientSide()) return;
        if(tick++ > 40){
            tick = 0;
            if(eventGoingOn){
                luckEventCountdown--;

                if(luckEventCountdown < 1){
                    triggerEvent(cap, target);
                    eventGoingOn = false;
                }

            } else {
                if(target.getRandom().nextInt(10000) <= luckEventChance){
                    castEvent(target);
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
        luckEventCountdown = target.getRandom().nextInt(20);
        target.sendSystemMessage(Component.translatable("potioneer.luck.event_cast_" + target.getRandom().nextInt(4)));
        //luckEventCountdown = target.getRandom().nextInt(1);
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
        int event = target.getRandom().nextInt(7);
        luckEventCd = target.getRandom().nextInt(24);
        if(luck > LV3_THRESHOLD || luck < -LV3_THRESHOLD){
            target.sendSystemMessage(Component.translatable("potioneer.luck.trigger_3"));
            consumeLuck(100);
            if(luck < 0){
                triggerVeryUnluckyEvent(cap, target, event);
            } else {
                triggerVeryLuckyEvent(cap, target, event);
            }
        } else if (luck > LV2_THRESHOLD || luck < -LV2_THRESHOLD){
            target.sendSystemMessage(Component.translatable("potioneer.luck.trigger_2"));
            consumeLuck(70);
            if(luck < 0){
                triggerMildlyUnluckyEvent(cap, target, event);
            } else {
                triggerMildlyLuckyEvent(cap, target, event);
            }
        } else {
            target.sendSystemMessage(Component.translatable("potioneer.luck.trigger_1"));
            consumeLuck(40);
            if(luck < 0){
                triggerMehUnluckyEvent(cap, target, event);
            } else {
                triggerMehLuckyEvent(cap, target, event);
            }

        }
    }

    private void triggerVeryUnluckyEvent(EntityBeyonderManager cap, LivingEntity target, int event){
        switch(event){
            case 0:
                target.getMainHandItem().shrink(target.getRandom().nextInt(8) + 2);
                break;
            case 1:
                target.addEffect(new MobEffectInstance(ModEffects.PLAGUE_EFFECT.get(), 600, 1));
                target.addEffect(new MobEffectInstance(ModEffects.WATER_PRISON.get(), 600, 5));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 5));
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600, 5));
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 5));
                target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 5));
                break;
            case 2:
                for(int i = target.getRandom().nextInt(5) + 1; i > 0; i--){
                    summonAsteroid(target.getOnPos(), target.level());
                }
        }
    }

    private void triggerMildlyUnluckyEvent(EntityBeyonderManager cap, LivingEntity target, int event){
        switch(event){
            case 0:
                if(target instanceof Player player){
                    player.getArmorSlots().forEach(armorPiece -> {
                        if(!passesLuckCheck(0.5f, 20, 10, target.getRandom()))
                            armorPiece.enchant(Enchantments.BINDING_CURSE, 1);
                    });
                }
            case 1:
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 240,
                        passesLuckCheck(0.4f, 20, 20, target.getRandom()) ? 1 : 5,
                        true, true));
                break;
            case 2:
                cap.getEffectsManager().addEffectNoCheck(
                        BeyonderEffects.byId(BeyonderEffects.EFFECT.TYRANT_LIGHTNING_TARGET, 0, 0, 10*20, true),
                        cap, target);
        }
    }

    private void triggerMehUnluckyEvent(EntityBeyonderManager cap, LivingEntity target, int event){
        switch(event){
            case 0:
                if(target instanceof Player player){
                    for(int i = target.getRandom().nextInt(5); i > 0; i--){
                        if(!passesLuckCheck(0.3f, 10, 10, target.getRandom()))
                            player.drop(player.getInventory().getItem(player.getRandom().nextInt(27)), true, true);
                    }
                    break;
                }
            case 1:
                target.getArmorSlots().forEach(armorPiece -> {
                    if(!passesLuckCheck(0.4f, 20, 10, target.getRandom())){
                        armorPiece.setDamageValue(Mth.clamp(armorPiece.getMaxDamage() - target.getRandom().nextInt(100), 0, armorPiece.getMaxDamage()));
                    }
                });
                break;
            case 2:
                if(target instanceof Player player){
                    player.giveExperienceLevels(-target.getRandom().nextInt(5) + 2);
                }
        }
    }

    private void triggerVeryLuckyEvent(EntityBeyonderManager cap, LivingEntity target, int event){
        switch(event){
            case 0:
                target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600,
                        passesLuckCheck(0.4f, 20, 20, target.getRandom()) ? 5 : 2,
                        true, true));
                target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600,
                        passesLuckCheck(0.4f, 20, 20, target.getRandom()) ? 5 : 2,
                        true, true));
                target.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600,
                        passesLuckCheck(0.4f, 20, 20, target.getRandom()) ? 5 : 2,
                        true, true));
                target.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600,
                        passesLuckCheck(0.4f, 20, 20, target.getRandom()) ? 5 : 2,
                        true, true));
                break;
            case 1:
                //digest potion
            case 2:

                break;
        }
    }

    private void triggerMildlyLuckyEvent(EntityBeyonderManager cap, LivingEntity target, int event){
        switch(event){
            case 0:
                if(target instanceof Player player){
                    ItemStack res = new ItemStack(ModItems.RING.get());
                    ArtifactHelper.makeSealedArtifact(res,
                            target.getRandom().nextInt(4)*10 + 10 + target.getRandom().nextInt(3) + 6,
                            target.getRandom());
                    if(!player.addItem(res)){
                        player.drop(res, false);
                    }
                    break;
                }
            case 1:
                cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_FORTUNE, 0, 0, 240, true), cap, target);
                cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_MINING, 5, 0, 240, true), cap, target);
                target.sendSystemMessage(Component.translatable("potioneer.luck.fortune_event"));
                break;
            case 2:
                
        }
    }

    private void triggerMehLuckyEvent(EntityBeyonderManager cap, LivingEntity target, int event){
        switch(event){
            case 0:
                if(target instanceof Player player){
                    player.addItem(
                            new ItemStack(Items.DIAMOND).copyWithCount(target.getRandom().nextInt(5))
                    );
                }
                break;
            case 1:
                if(target instanceof Player){
                    cap.getEffectsManager().addEffectNoCheck(BeyonderEffects.byId(BeyonderEffects.EFFECT.MISC_HUNGER_REGEN,
                            0, 0, target.getRandom().nextInt(5)*20, true), cap, target);
                    break;
                }
            case 2:
                target.addEffect(new MobEffectInstance(MobEffects.HEAL, 100, 0, false, false));
                break;
        }
    }

    private void summonAsteroid(BlockPos pos, Level level){
        AsteroidEntity ent = new AsteroidEntity(ModEntities.ASTEROID.get(), level);
        ent.setToHit(pos, level.random);
        level.addFreshEntity(ent);
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
