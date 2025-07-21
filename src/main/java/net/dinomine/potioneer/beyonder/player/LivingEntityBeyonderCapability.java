package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
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
    private Supplier<Integer> maxSanity;
    //this means that using N times the maximum spirituality equals using 100 points of sanity
    //for instance, using entire spirituality 5 times consumes all sanity for a sequence 9,
    //but for a sequence 1 it might be 2 times instead.
    //https://www.desmos.com/calculator/4idokgotbr
    private Supplier<Float> spiritualityToSanityScalar = () ->{
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
    private final PlayerActingManager actingManager;
    private ArrayList<ConjurerContainer> conjurerContainers = new ArrayList<>();

    private BeyonderPathway pathway = new BeyonderPathway(10);
    private final LivingEntity entity;
    private int syncCD = 40;
    private int effectCd = 40;
    private int characteristicXrayCd = 0;


    public LivingEntityBeyonderCapability(LivingEntity entity){
        beyonderStats = new BeyonderStats();
        abilitiesManager = new PlayerAbilitiesManager();
        effectsManager = new PlayerEffectsManager();
        luckManager = new PlayerLuckManager();
        actingManager = new PlayerActingManager();
        maxSanity = () -> (int) (actingManager.getActingPercentForSanityCalculation(getPathwayId())*100d);
        if(entity instanceof Player player) conjurerContainers.add(new ConjurerContainer(player, 9));
        this.entity = entity;
    }

    public ConjurerContainer getConjurerContainer(int idx){
        return conjurerContainers.isEmpty() ? null : conjurerContainers.get(idx);
    }

    public PlayerActingManager getActingManager(){return actingManager;}

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
        return pathway.getSequence();
    }

    //returns full ID, from 0 to 49 (or -1 if not a beyonder)
    public int getPathwayId(){
        return pathway.getId();
    }

    public boolean isBeyonder(){
        return this.pathway.getId() > -1;
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
        changeSpirituality(this.maxSpirituality/5f);
    }

    public void onFoodEat(ItemStack item, LivingEntity target) {
        if(item.getFoodProperties(target) == null) return;
        changeSpirituality(item.getFoodProperties(target).getNutrition() * getMaxSpirituality()/120f);
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
        this.spiritualityCost += 40*cost;
    }

    public void requestPassiveSpiritualityCost(float cost){
        this.spiritualityCost += cost;
    }

    private void applyCost(){
        if(maxSpirituality <= 0) return;
        float amount = Math.round((1000*( - spiritualityCost/40f + (float) maxSpirituality /SECONDS_TO_MAX_SPIRITUALITY))) / 1000f;
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
            actingManager.tick();
            if(entity instanceof Player player){
                if(syncCD-- == player.getId()%40){
                    if(effectsManager.hasEffect(BeyonderEffects.EFFECT.MISC_COGITATION)){
                        requestActiveSpiritualityCost(-(maxSpirituality/120f));
                    }
                    applyCost();
                    syncCD += 39;
                    abilitiesManager.updateArtifacts(this, player);
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                            new PlayerSTCHudStatsSync(this.spirituality, this.maxSpirituality, (int) this.sanity, this.getPathwayId(), getAbilitiesManager().enabledDisabled, (float) actingManager.getAggregatedActingProgress(getPathwayId())));
                }
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PlayerStatsSyncMessage(getBeyonderStats().getMiningSpeed(),
                                luckManager.getLuck(),
                                luckManager.getMinPassiveLuck(),
                                luckManager.getMaxPassiveLuck()));
            } else if(syncCD-- == entity.getId()%40){
                applyCost();
                syncCD += 39;
            }
            if(effectCd++ > 100){
                effectCd = 0;
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
        } else if (entity instanceof Player player && characteristicXrayCd-- < 0){
            characteristicXrayCd = 120;
            ArrayList<Entity> characteristics = AbilityFunctionHelper.getEntitiesAroundPredicate(player, 16, ent -> ent instanceof CharacteristicEntity);
            for(Entity ent: characteristics){
                if(ent instanceof CharacteristicEntity charact
                        && Math.floorDiv(charact.getSequenceId(), 10) == Math.floorDiv(getPathwayId(), 10)){
                    Vec3 position = charact.position();

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
            }
        }
//        getBeyonderStats().onTick(this, entity);
    }


    public boolean advance(int id, Player player, boolean sync, boolean advancing){
//        System.out.println(getEffectsManager());
        boolean changingPathway = Math.floorDiv(getPathwayId(), 10) != Math.floorDiv(id, 10);
        this.abilitiesManager.clear(true, this, player);
        if(advancing) actingManager.resetPassiveActing(luckManager, player.getRandom(), getPathwayId());
        int seq = id%10;


        //setDefaultStats(player);
        //getAbilitiesManager().clear(true, this, player);
        setPathway(id, advancing);
        if(!player.level().isClientSide()){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAbilityInfoSyncSTC(getAbilitiesManager().getActivesIds(), changingPathway));
        }


        //not translated. either make it translatable or delete it for final version
        if(!player.level().isClientSide && advancing && id > -1){
            player.sendSystemMessage(Component.literal("Successfully advanced to Sequence " + String.valueOf(seq)
                    + " " + BeyonderPathway.getSequenceNameFromId(id, true) + "!"));
        }
        if(sync) syncSequenceData(player, advancing);

        return true;
    }


    public void setPathway(int id, boolean advancing){
        getBeyonderStats().setAttributes(BeyonderPathway.getStatsFor(0));
        if(id < 0){
            this.pathway = new BeyonderPathway(10);
        } else {
            int pathway = Math.floorDiv(id, 10);
            if(id > 59) pathway = 7;
            int seq = id%10;
            switch(pathway){
                case 0:
                    this.pathway = new WheelOfFortunePathway(seq);
                    WheelOfFortunePathway.getAbilities(seq, getAbilitiesManager());
                    getBeyonderStats().setAttributes(WheelOfFortunePathway.getStatsFor(seq));
                    break;
                case 1:
                    this.pathway = new TyrantPathway(seq);
                    TyrantPathway.getAbilities(seq, getAbilitiesManager());
                    getBeyonderStats().setAttributes(TyrantPathway.getStatsFor(seq));
                    break;
                case 2:
                    this.pathway = new MysteryPathway(seq);
                    MysteryPathway.getAbilities(seq, getAbilitiesManager());
                    getBeyonderStats().setAttributes(MysteryPathway.getStatsFor(seq));
                    break;
                case 3:
                    this.pathway = new RedPriestPathway(seq);
                    RedPriestPathway.getAbilities(seq, getAbilitiesManager());
                    getBeyonderStats().setAttributes(RedPriestPathway.getStatsFor(seq));
                    break;
                case 4:
                    this.pathway = new ParagonPathway(seq);
                    ParagonPathway.getAbilities(seq, getAbilitiesManager());
                    getBeyonderStats().setAttributes(ParagonPathway.getStatsFor(seq));
                    break;
                case 5:
                    System.out.println("Advancing as Dev.");
                    this.pathway = new DevPathway(seq);
                    DevPathway.getAbilities(seq, getAbilitiesManager());
                    break;
                default:
                    System.out.println("Invalid pathway Id. Defaulting to beyonderless...");
                    this.pathway = new BeyonderPathway(10);
                    break;
            }
            this.maxSpirituality = this.pathway.getMaxSpirituality(seq);
            //TODO if effect also need an "on acquire" funciton, add it here
            //TODO move this into the "set active abilities" in the ability manager, will require changint the getAbilities
            //in the pathway classes
            //TODO make the effects manager also do the "on Acquire" abilities
            if(advancing) this.abilitiesManager.onAcquireAbilities(this, entity);
            if(advancing) setSpirituality(this.maxSpirituality);
        }
        if(entity instanceof Player player) getBeyonderStats().applyStats(player, true);
        //if hp changes, add a heal to the player here IF ADVANCING!!
    }

    public String getPathwayName(boolean capitalize){
        return BeyonderPathway.getPathwayName(this.pathway.getId(), capitalize);
    }

    public int getPathwayColor(){
        return this.pathway.getColor();
    }

    public String getSequenceName(boolean show){
        return BeyonderPathway.getSequenceNameFromId(this.pathway.getId(), show);
    }

    public void copyFrom(LivingEntityBeyonderCapability source, Player player){
        //TODO have this account for everything
        this.spirituality = source.getSpirituality();
        //advance(source.getPathwayId(), player, true, false);
        setPathway(source.getPathwayId(), false);
        player.setHealth(player.getMaxHealth());
        this.luckManager.copyFrom(source.luckManager);
        this.effectsManager.copyFrom(source.effectsManager, this, player);
        this.abilitiesManager.copyFrom(source.getAbilitiesManager());
        this.conjurerContainers = new ArrayList<>(source.conjurerContainers);
        this.actingManager.copyFrom(source.actingManager);
        this.sanity = Math.min(Math.max(source.sanity, SANITY_MIN_RESPAWN), maxSanity.get());
        //this.abilitiesManager.onAcquireAbilities(this, player);
    }

    public void saveNBTData(CompoundTag nbt){
//        System.out.println("saving nbt data for beyonder capability...");
        nbt.putFloat("spirituality", spirituality);
        nbt.putInt("pathwayId", pathway.getId());
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
        this.actingManager.saveNBTData(nbt);
    }

    public void loadNBTData(CompoundTag nbt){
//        System.out.println("-------------loading capability nbt-------------------");
//        System.out.println("loading nbt data for beyonder capability...");
        this.spirituality = nbt.getFloat("spirituality");
//        System.out.println("Loading pathway id: " + nbt.getInt("pathwayId"));
        this.sanity = nbt.getFloat("sanity");
        setPathway(nbt.getInt("pathwayId"), false);

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
        //enabledDisabled BEFORE onAcquire bc of reach ability
        this.abilitiesManager.loadNBTData(nbt);
        this.actingManager.loadNBTData(nbt);
        //this.abilitiesManager.onAcquireAbilities(this, entity);
        //TODO make abilities manager actually save and load item abilities.
        //this.abilitiesManager.loadNBTData(nbt);
    }

    public void syncSequenceData(Player player, boolean advancing){
        if(!player.level().isClientSide()){
            //System.out.println("syncing from server side");
            //server side to client. messages are sent when client joins world and when he advanced by means controlled by the server
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAdvanceMessage(this.pathway.getId(), advancing));
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerStatsMessageSTC(getBeyonderStats().getIntStats()));
        } else {
            //client side to server. messages are sent when client advances after succeeding in the minigame
            PacketHandler.INSTANCE.sendToServer(new PlayerAdvanceMessage(this.pathway.getId(), advancing));
        }
    }

    public void onPlayerDie(LivingDeathEvent event) {
        boolean dropForLowSanity = sanity < SANITY_FOR_DROP && PotioneerCommonConfig.CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE.get() == PotioneerCommonConfig.CharacteristicDropCriteria.LOW_SANITY;
        boolean doDrop = PotioneerCommonConfig.CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE.get() == PotioneerCommonConfig.CharacteristicDropCriteria.ALWAYS;
        if((doDrop || dropForLowSanity) && event.getEntity() instanceof Player player && isBeyonder()){
            CharacteristicHelper.addCharacteristicToLevel(getPathwayId(), player.level(), player, player.position(), player.getRandom());
            advance((getPathwayId() % 10 != 9) ? getPathwayId() + 1 : -1, player, true, true);
        }
    }
}
