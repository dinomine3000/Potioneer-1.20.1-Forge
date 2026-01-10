package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.network.messages.advancement.PlayerAdvanceMessage;
import net.dinomine.potioneer.util.misc.ArtifactHelper;
import net.dinomine.potioneer.util.misc.CharacteristicHelper;
import net.dinomine.potioneer.beyonder.pathways.*;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.function.Supplier;

@AutoRegisterCapability
public class LivingEntityBeyonderCapability {
    public static final int SANITY_FOR_DAMAGE = 15;
    public static final int SANITY_FOR_DROP = 20;
    public static final int SANITY_MIN_RESPAWN = 40;
    private static int SECONDS_TO_MAX_SPIRITUALITY = PotioneerCommonConfig.SECONDS_TO_MAX_SPIRITUALITY.get();

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

    public void changeSanity(float val){
        setSanity(Mth.clamp(getSanity() + val, 0, maxSanity.get()));
    }

    public float getSanity(){
        return this.sanity;
    }

    public void onPlayerSleep(){
        System.out.println("Player sleep method");
        changeSpirituality(this.maxSpirituality/5f);
    }

    public void onFoodEat(ItemStack item, LivingEntity target) {
        if(item.getFoodProperties(target) == null) return;
        changeSpirituality(item.getFoodProperties(target).getNutrition() * getMaxSpirituality()/160f);
    }

    public float getSpirituality(){
        return this.spirituality;
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

    public void onTick(LivingEntity entity, boolean serverSide){
        if(serverSide){
            abilitiesManager.onTick(this, entity);
            effectsManager.onTick(this, entity);
            luckManager.onTick(this, entity);
            characteristicManager.tick();
            if(entity.tickCount%40 == entity.getId()%40){
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
                    }

//                    abilitiesManager.updateArtifacts(this, player);
                    PacketHandler.sendMessageSTC(new PlayerSTCHudStatsSync(this.spirituality, this.maxSpirituality,
                            (int) this.sanity, this.getPathwaySequenceId(),
                            (float) characteristicManager.getAdjustedActingPercent(getPathwaySequenceId())), player);
                }
                applyCost();
                lowStatEffects();
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
                    entity.hurt(entity.damageSources().generic(), entity.getMaxHealth()*0.1f);
                }
            }
        }
        if(sanity < SANITY_FOR_DAMAGE){
            entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, true, true));
            if(sanity < 1){
                entity.hurt(entity.damageSources().genericKill(), 100000);
            } else {
                entity.hurt(entity.damageSources().generic(), entity.getMaxHealth()*0.1f);
            }
        }
    }

    private static boolean isItemOfSamePathway(ItemStack stack, int exactPathwayId){
        return stack.hasTag() && stack.getTag().contains(ArtifactHelper.BEYONDER_TAG_ID)
                && Math.floorDiv(stack.getTag().getCompound(ArtifactHelper.BEYONDER_TAG_ID).getInt("id"), 10) == exactPathwayId;
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
        this.abilitiesManager.clear(this, entity);
        characteristicManager.reset();
        if(definitive) entity.sendSystemMessage(Component.literal("Reset beyonder powers."));

        return true;
    }

    public void setBeyonderSequence(int id){
        resetBeyonder(false);
        consumeCharacteristic(id);
    }

    public void advance(int id){
        consumeCharacteristic(id);

    }

    public void consumeCharacteristic(int id){
        getBeyonderStats().setAttributes(Pathways.BEYONDERLESS.get().getStatsFor(0));
        characteristicManager.consumeCharacteristic(id);
        characteristicManager.setAbilities(id, this, entity);
        characteristicManager.setAttributes(beyonderStats);
        maxSpirituality = characteristicManager.getMaxSpirituality();
        if(entity instanceof Player player) getBeyonderStats().applyStats(player, true);
    }
//        this.maxSpirituality = characteristicManager.getMaxSpirituality();
//        if(advancing){
//            this.abilitiesManager.onAcquireAbilities(this, entity);
//            setSpirituality(this.maxSpirituality);
//        }
//        boolean changingPathway = Math.floorDiv(getPathwayId(), 10) != Math.floorDiv(id, 10);
//        this.abilitiesManager.clear(true, this, player);
//        if(advancing) characteristicManager.resetPassiveActing(luckManager, player.getRandom(), getPathwayId());
//        int seq = id%10;
//
//
//        setPathway(id, advancing);
//        if(!player.level().isClientSide()){
//            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
//                    new PlayerAbilityInfoSyncSTC(getAbilitiesManager().getActivesIds(), changingPathway));
//        }
//
//        //not translated. either make it translatable or delete it for final version
//        if(!player.level().isClientSide && advancing && id > -1){
//            player.sendSystemMessage(Component.literal("Successfully advanced to Sequence " + String.valueOf(seq)
//                    + " " + BeyonderPathway.getSequenceNameFromId(id, true) + "!"));
//        }
//        if(sync) syncSequenceData(player, advancing);
//    public void setPathway(int id, boolean advancing){
//        if(id < 0){
//            this.pathway = new BeyonderPathway(10);
//        } else {
//            int pathway = Math.floorDiv(id, 10);
//            if(id > 59) pathway = 7;
//            int seq = id%10;
//            switch(pathway){
//                case 0:
//                    this.pathway = new WheelOfFortunePathway(seq);
//                    WheelOfFortunePathway.getAbilities(seq, getAbilitiesManager());
//                    getBeyonderStats().setAttributes(WheelOfFortunePathway.getStatsFor(seq));
//                    break;
//                case 1:
//                    this.pathway = new TyrantPathway(seq);
//                    TyrantPathway.getAbilities(seq, getAbilitiesManager());
//                    getBeyonderStats().setAttributes(TyrantPathway.getStatsFor(seq));
//                    break;
//                case 2:
//                    this.pathway = new MysteryPathway(seq);
//                    MysteryPathway.getAbilities(seq, getAbilitiesManager());
//                    getBeyonderStats().setAttributes(MysteryPathway.getStatsFor(seq));
//                    break;
//                case 3:
//                    this.pathway = new RedPriestPathway(seq);
//                    RedPriestPathway.getAbilities(seq, getAbilitiesManager());
//                    getBeyonderStats().setAttributes(RedPriestPathway.getStatsFor(seq));
//                    break;
//                case 4:
//                    this.pathway = new ParagonPathway(seq);
//                    ParagonPathway.getAbilities(seq, getAbilitiesManager());
//                    getBeyonderStats().setAttributes(ParagonPathway.getStatsFor(seq));
//                    break;
//                case 5:
//                    System.out.println("Advancing as Dev.");
//                    this.pathway = new DevPathway(seq);
//                    DevPathway.getAbilities(seq, getAbilitiesManager());
//                    break;
//                default:
//                    System.out.println("Invalid pathway Id. Defaulting to beyonderless...");
//                    this.pathway = new BeyonderPathway(10);
//                    break;
//            }
//            this.maxSpirituality = this.pathway.getMaxSpirituality(seq);
//            //TODO if effect also need an "on acquire" funciton, add it here
//            //TODO move this into the "set active abilities" in the ability manager, will require changint the getAbilities
//            //in the pathway classes
//            //TODO make the effects manager also do the "on Acquire" abilities
//            if(advancing) this.abilitiesManager.onAcquireAbilities(this, entity);
//            if(advancing) setSpirituality(this.maxSpirituality);
//        }
//        if(entity instanceof Player player) getBeyonderStats().applyStats(player, true);
//        //if hp changes, add a heal to the player here IF ADVANCING!!
//    }

    public String getPathwayName(boolean capitalize){
        return Pathways.getPathwayById(getPathwaySequenceId()).getPathwayName(capitalize);
    }

    public int getPathwayColor(){
        return getPathway().getColor();
    }

    public String getSequenceName(boolean show){
        return getPathway().getSequenceNameFromId(getSequenceLevel(), show);
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
        this.sanity = Math.min(Math.max(source.sanity, SANITY_MIN_RESPAWN), maxSanity.get());
        //this.abilitiesManager.onAcquireAbilities(this, player);
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
//        System.out.println("Loading pathway id: " + nbt.getInt("pathwayId"));
        this.sanity = nbt.getFloat("sanity");
//        setPathway(nbt.getInt("pathwayId"), false);

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

        this.luckManager.loadNBTData(nbt);
        this.effectsManager.loadNBTData(nbt, this, entity);
        //characteristics before ablities because characteristics give abilities.
        this.characteristicManager.loadNBTData(nbt, this, entity);
        this.abilitiesManager.loadNBTData(nbt, this, entity);
        //this.abilitiesManager.onAcquireAbilities(this, entity);
        //TODO make abilities manager actually save and load item abilities.
        //this.abilitiesManager.loadNBTData(nbt);
    }

    public void syncSequenceData(Player player){
        if(!player.level().isClientSide()){
            //System.out.println("syncing from server side");
            //server side to client. messages are sent when client joins world and when he advanced by means controlled by the server
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAdvanceMessage(this.getPathwaySequenceId()));
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAttributesSyncMessageSTC(getBeyonderStats().getIntStats()));
        }
    }

    public void onPlayerDie(LivingDeathEvent event) {
        boolean dropForLowSanity = sanity < SANITY_FOR_DROP && PotioneerCommonConfig.CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE.get() == PotioneerCommonConfig.CharacteristicDropCriteria.LOW_SANITY;
        boolean doDrop = PotioneerCommonConfig.CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE.get() == PotioneerCommonConfig.CharacteristicDropCriteria.ALWAYS;
        boolean switchingPathwaysCheck = PotioneerCommonConfig.ALLOW_CHANGING_PATHWAYS.get() || (isBeyonder() && getSequenceLevel() < 9);
        boolean dropEverything = PotioneerCommonConfig.DROP_ALL_CHARACTERISTICS.get();
        if((doDrop || dropForLowSanity)
                && (switchingPathwaysCheck || dropEverything)
                && event.getEntity() instanceof Player player && isBeyonder()){
            if(dropEverything){
                CharacteristicHelper.addCharacteristicToLevel(characteristicManager.dropAllCharacteristics(), player.level(), player, player.position(), player.getRandom());
            } else {
                CharacteristicHelper.addCharacteristicToLevel(characteristicManager.dropLevel(), player.level(), player, player.position(), player.getRandom());
            }
        }
    }
}
