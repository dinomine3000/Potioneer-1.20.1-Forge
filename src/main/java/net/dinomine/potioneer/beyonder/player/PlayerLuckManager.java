package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.player.luck.LuckRange;
import net.dinomine.potioneer.beyonder.player.luck.luckevents.LuckEvent;
import net.dinomine.potioneer.beyonder.player.luck.luckevents.LuckEvents;
import net.dinomine.potioneer.config.PotioneerGameplayConfig;
import net.dinomine.potioneer.event.LuckChangeEvent;
import net.dinomine.potioneer.event.LuckEventCastEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class PlayerLuckManager {
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

    public PlayerLuckManager(PlayerLuckManager luck1, PlayerLuckManager luck2){
        this.luck = luck1.getLuck() - luck2.getLuck();
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
                if(target instanceof Player){
                    int chance = PotioneerGameplayConfig.LUCK_EVENT_CAST_CHANCE.get();
                    int rangeChance = range.getChance();
                    if(target.getRandom().nextInt(chance) <= rangeChance){
                        castEventNoRefresh(target);
                    }
                }
            }
            //random walk
            luck = range.changeLuck(luck, target.getRandom().nextBoolean() ? 1 : -1, target);
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
                .createInstance(getRandomNumber(PotioneerGameplayConfig.MINIMUM_LUCK_EVENT_TIMER.get()*20, PotioneerGameplayConfig.MAXIMUM_LUCK_EVENT_TIMER.get()*20, luck < 0, target.getRandom()));
        boolean cancelledCheck = MinecraftForge.EVENT_BUS.post(new LuckEventCastEvent.Pre(target, luck, proposedEvent));
        if(cancelledCheck){
            return null;
        }
        else{
            target.sendSystemMessage(Component.translatable("luck.potioneer.event_cast_" + target.getRandom().nextInt(4)));
            MinecraftForge.EVENT_BUS.post(new LuckEventCastEvent.Post(target, luck, proposedEvent));
            return proposedEvent;
        }
    }

    public void forceCastEvent(LivingEntity target, LivingEntityBeyonderCapability cap, boolean instantCast){
        LuckEvent proposedEvent = LuckEvents.getRandomEventFromLuck(luck, target.getRandom())
                .createInstance(getRandomNumber(PotioneerGameplayConfig.MINIMUM_LUCK_EVENT_TIMER.get()*20, PotioneerGameplayConfig.MAXIMUM_LUCK_EVENT_TIMER.get()*20, luck < 0, target.getRandom()));
        target.sendSystemMessage(Component.translatable("luck.potioneer.event_cast_" + target.getRandom().nextInt(4)));
        MinecraftForge.EVENT_BUS.post(new LuckEventCastEvent.Post(target, luck, proposedEvent));
        if(instantCast) proposedEvent.triggerEvent(cap, this, target);
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


    public float checkLuck(float chance){
        return checkLuck(luck, chance);
    }

    /**
     * transforms chance into another value based on the luck value provided.
     * this is all based on a <a href="https://www.desmos.com/calculator/91a36c649f">desmos graph</a>
     * @param luck the luck to be referenced
     * @param chance the chance value, between 0 and 1, to transform
     * @return the new chance value, representative of the luck of the target
     */
    public static float checkLuck(int luck, float chance){
        return LuckMath.checkLuck(luck, chance);
    }

    public static boolean passesLuckCheck(int luck, float chance, RandomSource random){
        if(chance == 0) return false;
        float newChance = checkLuck(luck, chance);
        return random.nextFloat() < newChance;
    }

    public boolean passesLuckCheck(float chance, int luckCostIfSuccess, int luckGainIfFailure, RandomSource random){
        if(passesLuckCheck(luck, chance, random)){
            consumeLuck(null, luckCostIfSuccess, true);
            return true;
        }
        grantLuck(null, luckGainIfFailure, true);
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

    public BlockPos getRandomBlockPos(BlockPos center, int maxDist, boolean incrementY, boolean closer_is_better, RandomSource random){
        int px = getRandomNumber(center.getX() - maxDist, center.getX(), closer_is_better, random);
        if(random.nextBoolean()) px = 2*center.getX() - px;
        int py = getRandomNumber(center.getY() - maxDist, center.getY(), closer_is_better, random);
        if(random.nextBoolean()) py = 2*center.getY() - py;
        int pz = getRandomNumber(center.getZ() - maxDist, center.getZ(), closer_is_better, random);
        if(random.nextBoolean()) pz = 2*center.getZ() - pz;
        return new BlockPos(px, incrementY ? py : center.getY(), pz);
    }

    public void consumeLuck(LivingEntity casterEntity, int consume, boolean natural){
        int luckO = luck;
        luck = Mth.clamp(luck - consume, MINIMUM_LUCK, MAXIMUM_LUCK);
        MinecraftForge.EVENT_BUS.post(new LuckChangeEvent(casterEntity, luckO, luck, natural));
    }
    public void grantLuck(LivingEntity entity, int amm, boolean natural){
        int luckO = luck;
        luck = Mth.clamp(luck + amm, MINIMUM_LUCK, MAXIMUM_LUCK);
        MinecraftForge.EVENT_BUS.post(new LuckChangeEvent(entity, luckO, luck, natural));
    }

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

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public float[] getDataForHud() {
        float[] res = range.getDataForHud();
        res[9] = luck;
        return res;
    }

    public PlayerLuckManager getDiffManager(PlayerLuckManager defenseManager) {
        return new PlayerLuckManager(this, defenseManager);
    }

    private static class LuckMath{
        private static final double[] X1 = {0,  20,  50, 150, 300, 400, 500, 600, 700, 800, 840, 900, 950, 970, 1000};
        private static final double[] Y1 = {0, 100, 180, 220, 250, 270, 300, 340, 380, 440, 500, 600, 700, 800, 1000};

        /**
         * Replicates l1(x) from Desmos.
         * Takes an input x and returns the linearly interpolated adjusted value.
         */
        public static double getAdjustedLuck(double x) {
            // Handle edge cases out of bounds
            if (x <= X1[0]) return Y1[0];
            if (x >= X1[X1.length - 1]) return Y1[Y1.length - 1];

            int index = Arrays.binarySearch(X1, x);
            if (index >= 0) {
                return Y1[index];
            }

            // If x falls between two elements, binarySearch returns: -(insertion_point) - 1
            // We invert this to find the index of the upper bounding element
            int insertionPoint = -(index + 1);

            // Determine the indices of the surrounding points (i and i+1 from your Desmos formula)
            int i = insertionPoint - 1;
            int iPlusOne = insertionPoint;

            double xI = X1[i];
            double xIPlusOne = X1[iPlusOne];
            double yI = Y1[i];
            double yIPlusOne = Y1[iPlusOne];

            // Perform the standard linear interpolation formula: y_i + m * (x - x_i)
            double slope = (yIPlusOne - yI) / (xIPlusOne - xI);
            return yI + slope * (x - xI);
        }

        private static float checkLuck(int luck, float chance){
            if(chance >= 1) return 1;
            if(luck == 0) return chance;
            if(chance < 0) return chance;
            if(PotioneerGameplayConfig.USE_ALTERNATE_LUCK_FUNCTION.get()){
                return (float) Math.pow(chance, (6.8*Math.pow(10, -7)*chance*chance - 0.00162d * chance + 1));
            }
            float newChance;
            float a = 0.2f;
            float d = 20f;
            if(luck > 0){
                //B(x)
                float b2 = a * (float) Math.pow(10, getAdjustedLuck(luck)/100f - 1);
                newChance = (float) (Math.log(b2*chance + 1) / Math.log(b2 + 1));
            } else {
                //L(x)
                float c = luck / (d - luck);
                newChance = (float) (Math.log(c*chance + 1) / Math.log(c+1));
            }
            return newChance;
        }
    }
}
