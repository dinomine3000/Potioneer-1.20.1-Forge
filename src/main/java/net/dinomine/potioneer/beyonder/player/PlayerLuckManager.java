package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.player.luck.LuckRange;
import net.dinomine.potioneer.beyonder.player.luck.luckevents.LuckEvent;
import net.dinomine.potioneer.beyonder.player.luck.luckevents.LuckEvents;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.event.LuckEventCastEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

import java.util.Random;
import java.util.UUID;

public class PlayerLuckManager {
    //corresponds to 20 minutes irl (ticks once every 10 seconds -> 1200 seconds = 20 mins)
    public static final int MAXIMUM_LUCK = 1000;
    public static final int MINIMUM_LUCK = -1000;

    private LuckEvent currentEvent = null;
    private int luck;
    private LuckRange range;

    public void removeLuckEventModifier(UUID uuid){
        range.removeChanceModifier(uuid);
    }

    public void chanceLuckEventChange(UUID uuid, int diffVal){
        range.changeChance(uuid, diffVal);
    }

    public PlayerLuckManager(){
        this.luck = 0;
        Random random = new Random();
        this.range = new LuckRange(random.nextInt(20, 50), random.nextInt(20, 50));
    }

    public PlayerLuckManager(int luck){
        this.luck = luck;
    }

    public void onTick(LivingEntityBeyonderCapability cap, LivingEntity target){
        //ticks once every 2 seconds
        if(target.level().isClientSide()) return;
        if(currentEvent != null){
            if(currentEvent.timeUp(cap, target)){
                currentEvent = null;
            }
        }
        if(target.tickCount%40 == 0){
//            if(target instanceof Player player)
                //System.out.println("Luck Manager ticking..." + luck);
            if(currentEvent == null) {
                if(target.getRandom().nextInt(10000) <= range.getChance()){
                    castEventNoRefresh(target);
                }
            }
            //random walk
            luck = range.changeLuck(luck, target.getRandom().nextBoolean() ? 1 : -1);
            if(target.tickCount%200 == 0){
                range.tenSecondTick();
            }

        }
    }

    public LuckRange getRange(){
        return range;
    }

    public void changeLuckTemporary(int minDelta, int maxDelta, int posDelta){
        range.changeDecayRange(minDelta, maxDelta, posDelta);
    }

    public void changeLuckRange(UUID uuid, int minDelta, int maxDelta, int posDelta){
        range.changeRange(uuid, minDelta, maxDelta, posDelta);
    }

    public void removeModifier(UUID uuid){
        this.range.removeModifier(uuid);
    }

    private LuckEvent castEvent(LivingEntity target){
        LuckEvent proposedEvent = LuckEvents.getRandomEventFromLuck(luck, target.getRandom())
                .createInstance(getRandomNumber(PotioneerCommonConfig.MINIMUM_LUCK_EVENT_TIMER.get(), PotioneerCommonConfig.MAXIMUM_LUCK_EVENT_TIMER.get(), luck < 0, target.getRandom()));
        boolean cancelledCheck = MinecraftForge.EVENT_BUS.post(new LuckEventCastEvent.Pre(target, luck, proposedEvent));
        if(cancelledCheck){
            return null;
        }
        else{
            MinecraftForge.EVENT_BUS.post(new LuckEventCastEvent.Post(target, luck, proposedEvent));
            target.sendSystemMessage(Component.translatable("potioneer.luck.event_cast_" + target.getRandom().nextInt(4)));
            return proposedEvent;
        }
    }

    /**
     * tries to cast a random event, but if one already exists then it forces it to cast
     * @param target
     * @return
     */
    public boolean castOrHurryEvent(LivingEntity target, LivingEntityBeyonderCapability cap){
        if(currentEvent != null) currentEvent.triggerEvent(cap, this, target);
        return castOrReplaceEvent(target);
    }

    /**
     * tries to cast a random event, but if one already exists then it replaces it.
     * not to be used for situations where this might be called often, as that means most events will be missed (replaced)
     * @param target
     * @return
     */
    public boolean castOrReplaceEvent(LivingEntity target){
        LuckEvent proposedEvent = castEvent(target);
        if(proposedEvent != null) currentEvent = proposedEvent;
        return proposedEvent != null;

    }

    /**
     * tries to cast a random event, but if one already exists it does nothing.
     * @param target
     * @return
     */
    public boolean castEventNoRefresh(LivingEntity target){
        if(currentEvent != null) return false;
        LuckEvent proposedEvent = castEvent(target);
        if(proposedEvent == null) return false;
        currentEvent = proposedEvent;
        return true;
    }

    public void instantlyCastEvent(LivingEntity target){
        if(castEventNoRefresh(target)){
            this.currentEvent.forceCast();
        }
    }

    public LuckEvent getCurrentEvent() {
        return this.currentEvent;
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

//
//    private void triggerVeryUnluckyEvent(LivingEntityBeyonderCapability cap, LivingEntity target, int event){
//        switch(event){
//            case 0:
//                target.getMainHandItem().shrink(target.getRandom().nextInt(target.getMainHandItem().getMaxStackSize()) + 1);
//                break;
//            case 1:
//                target.addEffect(new MobEffectInstance(ModEffects.PLAGUE_EFFECT.get(), 600, 1));
//                target.addEffect(new MobEffectInstance(ModEffects.WATER_PRISON.get(), 600, 5));
//                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 5));
//                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600, 5));
//                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 5));
//                target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 5));
//                break;
//            case 2:
//                break;
//        }
//    }
//
//    private void triggerMildlyUnluckyEvent(LivingEntityBeyonderCapability cap, LivingEntity target, int event){
//        switch(event){
//            case 0:
//                if(target instanceof Player player){
//                    player.getArmorSlots().forEach(armorPiece -> {
//                        if(armorPiece != null && !armorPiece.isEmpty()
//                                && !passesLuckCheck(0.5f, 20, 10, target.getRandom()))
//                            armorPiece.enchant(Enchantments.BINDING_CURSE, 1);
//                    });
//                }
//            case 1:
//                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 240,
//                        passesLuckCheck(0.4f, 20, 20, target.getRandom()) ? 1 : 5,
//                        true, true));
//                break;
//            case 2:
//                cap.getEffectsManager().addEffectNoCheck(
//                        BeyonderEffects.byId(BeyonderEffects.TYRANT_LIGHTNING_TARGET.getEffectId(), 5, 0, 10*20, true),
//                        cap, target);
//        }
//    }
//
//    private void triggerMehUnluckyEvent(LivingEntityBeyonderCapability cap, LivingEntity target, int event){
//        switch(event){
//            case 0:
//                if(target instanceof Player player){
//                    for(int i = target.getRandom().nextInt(5); i > 0; i--){
//                        if(!passesLuckCheck(0.3f, 10, 10, target.getRandom()))
//                            player.drop(player.getInventory().getItem(player.getRandom().nextInt(27)), true, true);
//                    }
//                    break;
//                }
//            case 1:
//                target.getArmorSlots().forEach(armorPiece -> {
//                    if(armorPiece.isDamageableItem() && !passesLuckCheck(0.4f, 20, 10, target.getRandom())){
//                        armorPiece.setDamageValue(Mth.clamp(armorPiece.getDamageValue() + target.getRandom().nextInt(armorPiece.getMaxDamage() - armorPiece.getDamageValue()), 0, armorPiece.getMaxDamage()));
//                    }
//                });
//                break;
//            case 2:
//                //TODO: EMPTY
//        }
//    }
//
//    private void triggerVeryLuckyEvent(LivingEntityBeyonderCapability cap, LivingEntity target, int event){
//        switch(event){
//            case 0:
//                target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600,
//                        passesLuckCheck(0.4f, 20, 20, target.getRandom()) ? 5 : 2,
//                        true, true));
//                target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600,
//                        passesLuckCheck(0.4f, 20, 20, target.getRandom()) ? 5 : 2,
//                        true, true));
//                target.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600,
//                        passesLuckCheck(0.4f, 20, 20, target.getRandom()) ? 5 : 2,
//                        true, true));
//                target.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600,
//                        passesLuckCheck(0.4f, 20, 20, target.getRandom()) ? 5 : 2,
//                        true, true));
//                break;
//            case 1:
//                //digest potion
//            case 2:
//
//                break;
//        }
//    }
//
//    private void triggerMildlyLuckyEvent(LivingEntityBeyonderCapability cap, LivingEntity target, int event){
//        switch(event){
//            case 0:
//                if(target instanceof Player player){
//                    ItemStack res = new ItemStack(ModItems.RING.get());
//                    MysticalItemHelper.generateSealedArtifact(res,
//                            target.getRandom().nextInt(4)*10 + 10 + target.getRandom().nextInt(3) + 6,
//                            target.getRandom());
//                    if(!player.addItem(res)){
//                        player.drop(res, false);
//                    }
//                    break;
//                }
//            case 1:
//                cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.WHEEL_FORTUNE.getEffectId(), 0, 0, 240, true), cap, target);
//                cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.WHEEL_MINING.getEffectId(), 5, 0, 240, true), cap, target);
//                target.sendSystemMessage(Component.translatable("potioneer.luck.fortune_event"));
//                break;
//            case 2:
//
//        }
//    }
//
//    private void triggerMehLuckyEvent(LivingEntityBeyonderCapability cap, LivingEntity target, int event){
//        switch(event){
//            case 0:
//                if(target instanceof Player player){
//                    player.addItem(
//                            new ItemStack(Items.DIAMOND).copyWithCount(target.getRandom().nextInt(5))
//                    );
//                }
//                break;
//            case 1:
//                if(target instanceof Player){
//                    cap.getEffectsManager().addEffectNoCheck(BeyonderEffects.byId(BeyonderEffects.HUNGER_REGEN.getEffectId(),
//                            0, 0, target.getRandom().nextInt(5)*20, true), cap, target);
//                    break;
//                }
//            case 2:
//                target.addEffect(new MobEffectInstance(MobEffects.HEAL, 100, 0, false, false));
//                break;
//        }
//    }

    public float checkLuck(float change){
        return checkLuck(luck, change);
    }

    /**
     * transforms chance into another value based on the luck value provided.
     * this is all based on a <a href="https://www.desmos.com/calculator/91a36c649f">desmos graph</a>
     * @param luck the luck to be referenced
     * @param chance the chance value, between 0 and 1, to transform
     * @return the new chance value, representative of the luck of the target
     */
    public static float checkLuck(int luck, float chance){
        if(chance >= 1) return 1;
        if(luck == 0) return chance;
        if(chance < 0) return chance;
        if(PotioneerCommonConfig.USE_ALTERNATE_LUCK_FUNCTION.get()){
            return (float) Math.pow(chance, (6.8*Math.pow(10, -7)*chance*chance - 0.00162d * chance + 1));
        }
        float newChance;
        float b = 3.6f;
        float d = 20f;
        if(luck > 0){
            //B(x)
            float a = b * (float) Math.pow(10, luck/100f - 1);
            newChance = (float) (Math.log(a*chance + 1) / Math.log(a + 1));
        } else {
            //L(x)
            float c = luck / (d - luck);
            newChance = (float) (Math.log(c*chance + 1) / Math.log(c+1));
        }
        return newChance;
    }

    public static boolean passesLuckCheck(int luck, float chance, RandomSource random){
        float newChance = checkLuck(luck, chance);
        return random.nextFloat() < newChance;
    }

    public boolean passesLuckCheck(float chance, int luckCostIfSuccess, int luckGainIfFailure, RandomSource random){
        if(passesLuckCheck(luck, chance, random)){
            consumeLuck(luckCostIfSuccess);
            return true;
        }
        grantLuck(luckGainIfFailure);
        return false;
    }

    public float nextFloat(RandomSource random){
        return checkLuck(random.nextFloat());
    }

    /**
     * gets a random integer based on the targets luck
     * @param min minimum value, inclusive
     * @param max maximum value, exclusive
     * @param bigger_is_better whether bigger is better. if true, lucky people will get bigger number and vice versa
     * @param random the random source
     * @return an int between [min, max[
     */
    public int getRandomNumber(int min, int max, boolean bigger_is_better, RandomSource random) {
        float rnd = nextFloat(random);
        if(!bigger_is_better) rnd = 1 - rnd;
        return (int) (Math.floor(min + (max - min)*rnd));
    }

    public void consumeLuck(int consume){
        luck = Mth.clamp(luck - consume, MINIMUM_LUCK, MAXIMUM_LUCK);
    }
    public void grantLuck(int amm){ luck = Mth.clamp(luck + amm, MINIMUM_LUCK, MAXIMUM_LUCK);}

    public void saveNBTData(CompoundTag nbt){
        CompoundTag luck = new CompoundTag();
        luck.putInt("luck", this.luck);
        luck.put("range_data", range.saveNBTData(new CompoundTag()));
        if(currentEvent != null){
            luck.putString("eventId", currentEvent.getId());
            currentEvent.saveNbt(luck);
        }
        nbt.put("luck_status", luck);
    }

    public void loadNBTData(CompoundTag nbt){
        CompoundTag tag = nbt.getCompound("luck_status");
        this.luck = tag.getInt("luck");
        if(tag.contains("range_data"))
            this.range.loadNBTData(tag.getCompound("range_data"));
        if(tag.contains("eventId"))
            this.currentEvent = LuckEvents.getEventById(tag.getString("eventId")).createInstance(1000).loadNbt(tag);
        else this.currentEvent = null;
    }

    public void copyFrom(PlayerLuckManager luckManager) {
        this.range = luckManager.range.copyOnDeath();
        this.luck = luckManager.luck;
    }
}
