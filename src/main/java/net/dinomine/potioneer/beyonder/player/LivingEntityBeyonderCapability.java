package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerSTCStatsSync;
import net.dinomine.potioneer.network.messages.abilityRelevant.AbilitySyncMessage;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerArtifactSyncSTC;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerSyncHotbarMessage;
import net.dinomine.potioneer.network.messages.advancement.PlayerAdvanceMessage;
import net.dinomine.potioneer.util.misc.CharacteristicHelper;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AutoRegisterCapability
public class LivingEntityBeyonderCapability {
    public static final int SANITY_FOR_DAMAGE = 15;
    public static final int SANITY_FOR_DROP = 20;
    public static final int SANITY_MIN_RESPAWN = 40;
    private static int SECONDS_TO_MAX_SPIRITUALITY = PotioneerCommonConfig.SECONDS_TO_MAX_SPIRITUALITY.get();
    public static int MAX_REP_DEFAULT = 2;
    public static int MAX_REP = 9;
    public static int PRAYING_COOLDOWN = 20*60*18;

    private float spirituality = 100;
    private float spiritualityCost = 0;
    private int maxSpirituality = 100;
    private float sanity = 100;
    private final Supplier<Integer> maxSanity;
    //this means that using N times the maximum spirituality equals using 100 points of sanity
    //for instance, using entire spirituality 5 times consumes all sanity for a sequence 9,
    //but for a sequence 1 it might be 2 times instead.
    //https://www.desmos.com/calculator/4idokgotbr
    private final Supplier<Float> spiritualityToSanityScalar = () ->{
        if(getSequenceLevel() > 4){
            return getSequenceLevel()/2f + 0.5f;
        } else {
            return (float) (Math.pow(getSequenceLevel(), 2.6)/90 + 2);
        }
    };
    private int[] reputation = {0, 0, 0, 0, 0};
    private int religion = -1;
    private long timePrayed = -1;

    public void putPrayerCooldown(Level level) {
        timePrayed = level.getGameTime();
    }

    public boolean canPray(Level level) {
        long gameTime = level.getGameTime();
        return timePrayed < 0 || level.getGameTime() - timePrayed > PRAYING_COOLDOWN;
    }

    public int getReligion(){
        return religion;
    }
    public void setReligion(int religion){
        if(religion >= 0 && religion < reputation.length){
            if(this.religion > -1) reputation[this.religion] = Math.min(reputation[this.religion], MAX_REP_DEFAULT);
            this.religion = religion;
        }
    }

    public void setReputation(int pathwayId, int rep){
        if(pathwayId >= 0 && pathwayId < reputation.length)
            reputation[pathwayId] = Mth.clamp(rep, 0, 10);
    }
    public boolean changeReputation(int pathwayId, int rep, Level level){
        if(level != null && timePrayed > 0 && level.getGameTime() - this.timePrayed < PRAYING_COOLDOWN) return false;
        if(pathwayId < 0 || pathwayId >= reputation.length) return false;
        if(getReputation(pathwayId) >= MAX_REP) return false;
        if(getReputation(pathwayId) >= MAX_REP_DEFAULT && pathwayId != religion) return false;
        reputation[pathwayId] += rep;
        return true;
    }

    public boolean changeReputation(int pathwayId, int rep){
        return changeReputation(pathwayId, rep, null);
    }

    public int getReputation(int pathwayId){
        if(pathwayId >= 0 && pathwayId < reputation.length)
            return reputation[pathwayId];
        return 0;
    }

    private int artifactCooldown = 0;

    private ArrayList<Integer> pageList = new ArrayList<>();
    private final BeyonderStats beyonderStats;
    private final PlayerAbilitiesManager abilitiesManager;
    private final PlayerEffectsManager effectsManager;
    private final PlayerLuckManager luckManager;
    private final PlayerCharacteristicManager characteristicManager;
    private ArrayList<ConjurerContainer> conjurerContainers = new ArrayList<>();

    private final LivingEntity entity;


    public LivingEntityBeyonderCapability(LivingEntity entity){
        beyonderStats = new BeyonderStats();
        abilitiesManager = new PlayerAbilitiesManager();
        effectsManager = new PlayerEffectsManager();
        luckManager = new PlayerLuckManager();
        characteristicManager = new PlayerCharacteristicManager();
        if(entity.level().isClientSide)
            maxSanity = () -> 100;
        else
            maxSanity = () -> (int) (characteristicManager.getActingPercentForSanityCalculation()*100d);
        if(entity instanceof Player player) conjurerContainers.add(new ConjurerContainer(player, 9));
        this.entity = entity;
    }

    public ConjurerContainer getConjurerContainer(int idx){
        return conjurerContainers.isEmpty() ? null : conjurerContainers.get(idx);
    }

    public PlayerCharacteristicManager getCharacteristicManager(){return characteristicManager;}

    public PlayerEffectsManager getEffectsManager(){
        return effectsManager;
    }

    public PlayerLuckManager getLuckManager(){
        return luckManager;
    }

    public PlayerAbilitiesManager getAbilitiesManager(){
        return abilitiesManager;
    }

    public int getMaxSpirituality(){
        return maxSpirituality;
    }

    public BeyonderStats getBeyonderStats(){
        return beyonderStats;
    }

    //returns only the sequence level
    public int getSequenceLevel(){
        return characteristicManager.getSequenceLevel();
    }

    //returns full ID, from 0 to 49 (or -1 if not a beyonder)
    public int getPathwaySequenceId(){
        return characteristicManager.getPathwaySequenceId();
    }
    public BeyonderPathway getPathway(){
        return characteristicManager.getPathway();
    }

    public boolean isBeyonder(){
        return getPathwaySequenceId() > -1;
    }

    public void setSanity(float san){
        if(san < 0){
            this.sanity = maxSanity.get();
        } else this.sanity = san;
    }

    public int getMaxSanity() {
        return maxSanity.get();
    }

    public void changeSanity(float val){
        setSanity(Mth.clamp(getSanity() + val, 0, maxSanity.get()));
    }

    public float getSanity(){
        return this.sanity;
    }

    public void onPlayerSleep(){
        changeSpirituality(this.maxSpirituality/5f);
    }

    public void onFoodEat(ItemStack item, LivingEntity target) {
        if(item.getFoodProperties(target) == null) return;
        changeSpirituality(item.getFoodProperties(target).getNutrition() * getMaxSpirituality()/160f);
    }

    public float getSpirituality(){
        return Math.max(this.spirituality, 0);
    }

    public float getSpiritualityPercent(){
        return getSpirituality() / getMaxSpirituality();
    }

    public void changeSpirituality(float val){
//        System.out.println(getSpirituality());
        setSpirituality(Mth.clamp(getSpirituality()+val, 0, maxSpirituality));
    }

    public void setMaxSpirituality(int maxSpirituality){
        this.maxSpirituality = maxSpirituality;
    }

    public void setSpirituality(float spirituality){
        if(spirituality < 0){
            this.spirituality = this.maxSpirituality;
        } else this.spirituality = spirituality;
    }

    public void requestActiveSpiritualityCost(float cost){
        this.spiritualityCost += 20*cost;
    }

    public void requestPassiveSpiritualityCost(float cost){
        this.spiritualityCost += cost;
    }

    private void applyCost(){
        if(maxSpirituality <= 0) return;
        float amount = Math.round((1000*( - spiritualityCost/20f + (float) maxSpirituality /SECONDS_TO_MAX_SPIRITUALITY))) / 1000f;
        changeSpirituality(amount);
        this.spiritualityCost = 0;

        if(amount < 0){
            changeSanity(100*amount/(maxSpirituality * spiritualityToSanityScalar.get()));
        } else {
            changeSanity((float) 100 /SECONDS_TO_MAX_SPIRITUALITY);
        }
    }

    public void putCharacteristicArtifactCooldown(int cooldownSeconds){
        artifactCooldown = cooldownSeconds*20;
    }

    public int getArtifactCooldown(){
        return artifactCooldown;
    }

    public void onTick(LivingEntity entity, boolean serverSide){
        abilitiesManager.onTick(this, entity);
        effectsManager.onTick(this, entity);
        if(serverSide){
            if(artifactCooldown > 0) artifactCooldown--;
            luckManager.onTick(this, entity);
            characteristicManager.tick();
            if(entity.tickCount%40 == entity.getId()%40){
                if(entity instanceof Player player){
                    abilitiesManager.updateArtifacts(this, player);
                    PacketHandler.sendMessageSTC(new PlayerSTCStatsSync(this.spirituality, this.maxSpirituality,
                            (int) this.sanity, (float) characteristicManager.getAdjustedActingPercent(getPathwaySequenceId()),
                            beyonderStats.getIntStats()), player);
                }
                applyCost();
                lowStatEffects();
            }
        }

        if(entity instanceof Player player){
            //characteristic xray timeout
            if(player.tickCount%120 == player.getId()%40){
                int radius = 16;
                ArrayList<Entity> characteristics = AbilityFunctionHelper.getEntitiesAroundPredicate(player, radius, ent -> ent instanceof CharacteristicEntity || ent instanceof ItemEntity);
                for(Entity ent: characteristics){
                    if(ent instanceof CharacteristicEntity charact
                            && Math.floorDiv(charact.getSequenceId(), 10) == Math.floorDiv(getPathwaySequenceId(), 10)){
                        characteristicXray(charact.position(), player);
                        continue;
                    }
                    if(ent instanceof ItemEntity itemEnt && isItemOfSamePathway(itemEnt.getItem(), Math.floorDiv(getPathwaySequenceId(), 10))){
                        characteristicXray(itemEnt.position(), player);
                    }
                }
//                System.out.println(abilitiesManager.toString());
            }

        }
    }

    private void lowStatEffects(){
        if (spirituality < maxSpirituality*0.15f){
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1, true, true));
            if(spirituality < maxSpirituality*0.05f){
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 1, true, true));
                changeSanity(-1);
                if(entity.getHealth() > 3){
                    entity.hurt(PotioneerDamage.low_spirituality((ServerLevel) entity.level()), entity.getMaxHealth()*0.1f);
                }
            }
        }
        if(sanity < SANITY_FOR_DAMAGE){
            entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, true, true));
            if(sanity < 1){
                entity.hurt(PotioneerDamage.low_sanity_kill((ServerLevel) entity.level()), 100000);
            } else {
                entity.hurt(PotioneerDamage.low_sanity((ServerLevel) entity.level()), entity.getMaxHealth()*0.1f);
            }
        }
    }

    private static boolean isItemOfSamePathway(ItemStack stack, int exactPathwayId){
        return stack.hasTag() && stack.getTag().contains(MysticalItemHelper.BEYONDER_TAG_ID)
                && Math.floorDiv(stack.getTag().getCompound(MysticalItemHelper.BEYONDER_TAG_ID).getInt("id"), 10) == exactPathwayId;
    }

    private void characteristicXray(Vec3 position, Player player){
        Vec3 pointing = position.subtract(player.getEyePosition()).normalize();
        float i = 0.2f;
        while(i < 1){
            Vec3 iterator = player.getEyePosition().add(pointing.scale(i));
            float speedScale = 0.2f;
            player.level().addAlwaysVisibleParticle(ParticleTypes.END_ROD, false,
                    iterator.x, iterator.y, iterator.z, speedScale*pointing.x, speedScale*pointing.y, speedScale*pointing.z);
            i += 0.2f;
        }
        player.level().addAlwaysVisibleParticle(ParticleTypes.END_ROD, true, position.x, position.y, position.z, 0, 0.1, 0);
    }

    public boolean resetBeyonder(boolean definitive){
        this.abilitiesManager.clearAbilities(this, entity);
        characteristicManager.reset();
        if(definitive) entity.sendSystemMessage(Component.literal("Reset beyonder powers."));
        if(definitive && entity instanceof Player player) syncSequenceData(player);
        return true;
    }

    public void setBeyonderSequence(int id){
        resetBeyonder(false);
        advance(id, false);
    }

    public void advance(int id, boolean fromLoading){
        consumeCharacteristic(id);
        if(!fromLoading){
            //confetti here;
            entity.sendSystemMessage(Component.literal("You successfully advanced, hooray!"));
            spirituality = maxSpirituality;
            if(entity instanceof Player player) syncSequenceData(player);
        }
    }

    private void consumeCharacteristic(int id){
        getBeyonderStats().setAttributes(Pathways.BEYONDERLESS.get().getStatsFor(0));
        characteristicManager.consumeCharacteristic(id);
        abilitiesManager.grantAbilities(characteristicManager.getAbilitiesFromCharacteristics(), getPathwaySequenceId(), this, entity);
        if(entity instanceof Player player) characteristicManager.setAttributes(beyonderStats, player);
        maxSpirituality = characteristicManager.getMaxSpirituality();
        if(entity instanceof Player player) getBeyonderStats().applyStats(player, true);
    }

    public String getPathwayName(boolean capitalize){
        return Pathways.getPathwayBySequenceId(getPathwaySequenceId()).getPathwayName(capitalize);
    }

    public int getPathwayColor(){
        return getPathway().getColor();
    }

    public String getSequenceName(boolean show){
        return getPathway().getSequenceNameFromId(getSequenceLevel(), show);
    }

    public void addPages(List<Integer> pages){
        for(int pageId: pages){
            addPage(pageId);
        }
    }

    public boolean addPage(int pageNumber){
        if(!PageRegistry.pageExists(pageNumber) || pageList.contains(pageNumber)) return false;
        pageList.add(pageNumber);
        if(entity instanceof Player player)
            PacketHandler.sendMessageSTC(new PlayerSTCStatsSync(this.spirituality, this.maxSpirituality,
                (int) this.sanity, (float) characteristicManager.getAdjustedActingPercent(getPathwaySequenceId()),
                beyonderStats.getIntStats(), pageList), player);
        return true;
    }

    public ArrayList<Integer> getPageList(){
        return pageList;
    }

    public void setPageList(List<Integer> list2){
        this.pageList = new ArrayList<>(list2);
    }

    public void clearPages() {
        this.pageList = new ArrayList<>();
        if(entity instanceof Player player)
            PacketHandler.sendMessageSTC(new PlayerSTCStatsSync(this.spirituality, this.maxSpirituality,
                    (int) this.sanity, (float) characteristicManager.getAdjustedActingPercent(getPathwaySequenceId()),
                    beyonderStats.getIntStats(), pageList), player);
    }

    public void copyFrom(LivingEntityBeyonderCapability source, Player player){
        //TODO have this account for everything
        this.spirituality = source.getSpirituality();
        //advance(source.getPathwayId(), player, true, false);
        //setPathway(source.getPathwayId(), false);
        //player.setHealth(player.getMaxHealth());
        this.luckManager.copyFrom(source.luckManager);
        this.effectsManager.copyFrom(source.effectsManager, this, player);
        this.abilitiesManager.copyFrom(source.getAbilitiesManager());
        this.conjurerContainers = new ArrayList<>(source.conjurerContainers);
        this.beyonderStats.copyFrom(source.beyonderStats);
        getBeyonderStats().applyStats(player, true);
        this.characteristicManager.copyFrom(source.characteristicManager);
        maxSpirituality = characteristicManager.getMaxSpirituality();
        this.sanity = Math.min(Math.max(source.sanity, SANITY_MIN_RESPAWN), maxSanity.get());
        pageList = new ArrayList<>(source.pageList);
        syncSequenceData(player);
        this.reputation = source.reputation;
        this.religion = source.religion;
        //this.abilitiesManager.onAcquireAbilities(this, player);
    }

    public void onRespawn() {
        this.sanity = Math.max(this.sanity, SANITY_FOR_DAMAGE);
        this.spirituality = (float) Math.max(this.spirituality, 0.1*maxSpirituality);
    }

    public void saveNBTData(CompoundTag nbt){
//        System.out.println("saving nbt data for beyonder capability...");
        nbt.putFloat("spirituality", spirituality);
        nbt.putFloat("sanity", sanity);
        nbt.putInt("containers_amount", conjurerContainers.size());
        for (int i = 0; i < conjurerContainers.size(); i++) {
            nbt.putInt("container_size_" + i, conjurerContainers.get(i).getContainerSize());
            nbt.put("container_" + i, conjurerContainers.get(i).createTag());
            nbt.putInt("container_debt_" + i, conjurerContainers.get(i).getDebt());
        }
        ListTag pages = new ListTag();
        for(int page: pageList){
            pages.add(IntTag.valueOf(page));
        }
        nbt.put("pages", pages);

        ListTag rep = new ListTag();
        for(int repVal: reputation){
            rep.add(IntTag.valueOf(repVal));
        }
        nbt.put("reputation", rep);
        nbt.putInt("religion", religion);
        nbt.putLong("prayCd", timePrayed);

//        System.out.println("Saving pathway id: " + pathway.getId());
        //this.abilitiesManager.saveNBTData(nbt);
        this.effectsManager.saveNBTData(nbt);
        this.abilitiesManager.saveNBTData(nbt);
        this.luckManager.saveNBTData(nbt);
        this.characteristicManager.saveNBTData(nbt);

    }

    public void loadNBTData(CompoundTag nbt){
//        System.out.println("-------------loading capability nbt-------------------");
//        System.out.println("loading nbt data for beyonder capability...");
        this.spirituality = nbt.getFloat("spirituality");
//        System.out.println("Loading pathway id: " + nbt.getInt("pathwaySequenceId"));
        this.sanity = nbt.getFloat("sanity");
//        setPathway(nbt.getInt("pathwaySequenceId"), false);

        if(entity instanceof Player player && nbt.contains("containers_amount")){
            int containersAmount = nbt.getInt("containers_amount");
            conjurerContainers = new ArrayList<>();
            for (int i = 0; i < containersAmount; i++) {
                int size = nbt.getInt("container_size_" + i);
                ConjurerContainer iterator = new ConjurerContainer(player, size);
                iterator.fromTag(nbt.getList("container_" + i, CompoundTag.TAG_COMPOUND));
                iterator.setDebt(nbt.getInt("container_debt_" + i));
                conjurerContainers.add(iterator);
            }
        }

        ArrayList<Integer> loadedPages = new ArrayList<>();
        ListTag pages = nbt.getList("pages", Tag.TAG_INT);
        for (int i = 0; i < pages.size(); i++) {
            int page = pages.getInt(i);
            loadedPages.add(page);
        }
        this.pageList = new ArrayList<>(loadedPages.stream().filter(PageRegistry::pageExists).toList());

        int[] reputation = new int[5];
        ListTag reputationTag = nbt.getList("reputation", Tag.TAG_INT);
        for (int i = 0; i < Math.min(reputationTag.size(), reputation.length); i++) {
            int repVal = reputationTag.getInt(i);
            reputation[i] = repVal;
        }
        this.reputation = reputation;
        this.religion = nbt.contains("religion") ? nbt.getInt("religion") : -1;
        this.timePrayed = nbt.contains("prayCd") ? nbt.getLong("prayCd") : -1;

        this.luckManager.loadNBTData(nbt);
        this.effectsManager.loadNBTData(nbt, this, entity);
        //characteristics before ablities because characteristics give abilities.
        this.characteristicManager.loadNBTData(nbt, this, entity);
        maxSpirituality = characteristicManager.getMaxSpirituality();
        this.abilitiesManager.loadNBTData(nbt, this, entity);
        //this.abilitiesManager.onAcquireAbilities(this, entity);
        //TODO make abilities manager actually save and load item abilities.
        //this.abilitiesManager.loadNBTData(nbt);
    }

    //server side to client. messages are sent when client joins world and when he advanced by means controlled by the server
    //called on advance, reset beyonder and join world
    public void syncSequenceData(Player player){
        if(!player.level().isClientSide()){
            PacketHandler.sendMessageSTC(new PlayerAdvanceMessage(getCharacteristicManager().getLastConsumedCharacteristics()), player);
            getAbilitiesManager().updateClientAbilityInfo(player, AbilitySyncMessage.SET);
            getAbilitiesManager().updateClientArtifactInfo(player, PlayerArtifactSyncSTC.SET);
            PacketHandler.sendMessageSTC(new PlayerSyncHotbarMessage(getAbilitiesManager().clientHotbar, getAbilitiesManager().quickAbility), player);
            getEffectsManager().syncToClient(player);
            PacketHandler.sendMessageSTC(new PlayerSTCStatsSync(this.spirituality, this.maxSpirituality,
                    (int) this.sanity, (float) characteristicManager.getAdjustedActingPercent(getPathwaySequenceId()),
                    beyonderStats.getIntStats(), pageList), player);
        }
    }

    public void onPlayerDie(LivingDeathEvent event) {
        if(!isBeyonder() || !(event.getEntity() instanceof Player player)) return;
        boolean dropForLowSanity = sanity < SANITY_FOR_DROP && PotioneerCommonConfig.CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE.get() == PotioneerCommonConfig.CharacteristicDropCriteria.LOW_SANITY;
        boolean doDropActingSafeguard = maxSanity.get() < 5;
        boolean doDropAlways = PotioneerCommonConfig.CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE.get() == PotioneerCommonConfig.CharacteristicDropCriteria.ALWAYS;
        boolean switchingPathwaysCheck = PotioneerCommonConfig.ALLOW_CHANGING_PATHWAYS.get() || characteristicManager.hasMoreThanOneCharacteristic();
        boolean dropEverything = PotioneerCommonConfig.DROP_ALL_CHARACTERISTICS.get();
        if((doDropAlways || doDropActingSafeguard || dropForLowSanity)
                && (switchingPathwaysCheck || dropEverything)){
            if(dropEverything){
                CharacteristicHelper.addCharacteristicToLevel(characteristicManager.dropAllCharacteristics(this, entity), player.level(), player, player.position(), player.getRandom());
            } else {
                CharacteristicHelper.addCharacteristicToLevel(characteristicManager.dropLevel(this, entity), player.level(), player, player.position(), player.getRandom());
            }
        }
    }
}
