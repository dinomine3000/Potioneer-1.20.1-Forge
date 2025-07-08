package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.pathways.*;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.*;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.BlockPos;
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

@AutoRegisterCapability
public class EntityBeyonderManager {
    public static final int SANITY_FOR_RECHARGE = 25;
    public static final int SANITY_FOR_DROP = 30;
    public static final int SANITY_WARNING_THRESHOLD = 40;
    public static final int SANITY_MIN_RESPAW = SANITY_WARNING_THRESHOLD;

    private float spirituality = 100;
    private float spiritualityCost = 0;
    private int maxSpirituality = 100;
    private int sanity = 100;

    private final BeyonderStats beyonderStats;
    private final PlayerAbilitiesManager abilitiesManager;
    private final PlayerEffectsManager effectsManager;
    private final PlayerLuckManager luckManager;
    private ArrayList<ConjurerContainer> conjurerContainers = new ArrayList<>();

    private Beyonder pathway = new Beyonder(10);
    private final LivingEntity entity;
    private int syncCD = 20;
    private int effectCd = 40;
    private int characteristicXrayCd = 0;


    public EntityBeyonderManager(LivingEntity entity){
        beyonderStats = new BeyonderStats();
        abilitiesManager = new PlayerAbilitiesManager();
        effectsManager = new PlayerEffectsManager();
        luckManager = new PlayerLuckManager();
        if(entity instanceof Player player) conjurerContainers.add(new ConjurerContainer(player, 9));
        this.entity = entity;
    }

    public ConjurerContainer getConjurerContainer(int idx){
        return conjurerContainers.isEmpty() ? null : conjurerContainers.get(idx);
    }

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

    public void setSanity(int san){
        if(san < 0){
            this.sanity = 100;
        } else this.sanity = san;
    }

    public void changeSanity(int val){
        setSanity(Mth.clamp(getSanity() + val, 0, 100));
    }

    public int getSanity(){
        return this.sanity;
    }

    public void onPlayerSleep(){
        changeSpirituality(this.maxSpirituality/5f);
        if(sanity >= SANITY_FOR_RECHARGE) changeSanity(30);
    }

    public void onFoodEat(ItemStack item, LivingEntity target) {
        if(item.getFoodProperties(target) == null) return;
        changeSpirituality(item.getFoodProperties(target).getNutrition() * getMaxSpirituality()/120f);
        changeSanity(item.getFoodProperties(target).getNutrition()/2);
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
        setSpirituality(Mth.clamp(Math.round((1000*(getSpirituality() - spiritualityCost/20f + maxSpirituality/2400f))) / 1000f,
                0f, this.maxSpirituality));
        this.spiritualityCost = 0;
    }

    public void onTick(LivingEntity entity, boolean serverSide){
        if(serverSide){
            abilitiesManager.onTick(this, entity);
            effectsManager.onTick(this, entity);
            luckManager.onTick(this, entity);
            if(entity instanceof Player player){
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PlayerStatsSyncMessage(getBeyonderStats().getMiningSpeed(),
                                luckManager.getLuck(),
                                luckManager.getMinPassiveLuck(),
                                luckManager.getMaxPassiveLuck()));
            }
            if(syncCD-- < 0){
                applyCost();
                syncCD = 20;
                if(entity instanceof Player player)
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PlayerSTCHudStatsSync(this.spirituality, this.maxSpirituality, this.sanity, this.getPathwayId(), getAbilitiesManager().enabledDisabled));
            }
            if(effectCd++ > 100){
                effectCd = 0;
                if (spirituality < maxSpirituality*0.15f){
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1, true, true));
                    entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1, true, true));
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1, true, true));
                    if (sanity < SANITY_WARNING_THRESHOLD){
                        entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, true, true));
                    }
                    if(spirituality < maxSpirituality*0.05f && entity.getHealth() > 3){
                        if(sanity > SANITY_FOR_RECHARGE) changeSanity(-1);
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
                        i += 0.2;
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
//        if(id < 0){
//            //setDefaultStats(player);
//            //getEffectsManager().clearEffects(this, player);
//            //getAbilitiesManager().clear(true, this, player);
//            this.pathway = new Beyonder(10);
//            if(sync) syncSequenceData(player, advancing);
//            return true;
//        }
        int seq = id%10;


        //setDefaultStats(player);
        //getAbilitiesManager().clear(true, this, player);
        setPathway(id, advancing);
        if(!player.level().isClientSide()){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAbilityInfoSyncSTC(getAbilitiesManager().getPathwayActives().stream().map(Ability::getInfo).toList(), changingPathway));
        }


        //not translated. either make it translatable or delete it for final version
        if(!player.level().isClientSide && advancing && id > -1){
            player.sendSystemMessage(Component.literal("Successfully advanced to Sequence " + String.valueOf(seq)
                    + " " + Beyonder.getSequenceNameFromId(id, true) + "!"));
        }
        if(sync) syncSequenceData(player, advancing);

        return true;
    }


    public void setPathway(int id, boolean advancing){
        getBeyonderStats().setAttributes(Beyonder.getStatsFor(0));
        if(id < 0){
            this.pathway = new Beyonder(10);
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
                    this.pathway = new Beyonder(10);
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
        return Beyonder.getPathwayName(this.pathway.getId(), capitalize);
    }

    public int getPathwayColor(){
        return this.pathway.getColor();
    }

    public String getSequenceName(boolean show){
        return Beyonder.getSequenceNameFromId(this.pathway.getId(), show);
    }

    public void copyFrom(EntityBeyonderManager source, Player player){
        //TODO have this account for everything
        this.spirituality = source.getSpirituality();
        //advance(source.getPathwayId(), player, true, false);
        setPathway(source.getPathwayId(), false);
        player.setHealth(player.getMaxHealth());
        this.luckManager.copyFrom(source.luckManager);
        this.abilitiesManager.copyFrom(source.getAbilitiesManager());
        this.conjurerContainers = new ArrayList<>(source.conjurerContainers);
        this.sanity = Math.max(source.sanity, SANITY_MIN_RESPAW);
        //this.abilitiesManager.onAcquireAbilities(this, player);
    }

    public void saveNBTData(CompoundTag nbt){
//        System.out.println("saving nbt data for beyonder capability...");
        nbt.putFloat("spirituality", spirituality);
        nbt.putInt("pathwayId", pathway.getId());
        nbt.putInt("sanity", sanity);
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
    }

    public void loadNBTData(CompoundTag nbt){
//        System.out.println("-------------loading capability nbt-------------------");
//        System.out.println("loading nbt data for beyonder capability...");
        this.spirituality = nbt.getFloat("spirituality");
//        System.out.println("Loading pathway id: " + nbt.getInt("pathwayId"));
        this.sanity = nbt.getInt("sanity");
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
        this.abilitiesManager.loadNBTData(nbt, entity);
        this.abilitiesManager.loadEnabledListFromTag(nbt, this, entity);
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
        if(sanity < SANITY_FOR_DROP && event.getEntity() instanceof Player player && isBeyonder()){
            ItemStack characteristic = new ItemStack(ModItems.CHARACTERISTIC.get());
            CompoundTag root = new CompoundTag();

            CompoundTag charInfo = new CompoundTag();
            charInfo.putInt("id", getPathwayId());
            root.put("beyonder_info", charInfo);
            characteristic.setTag(root);

            MysticismHelper.updateOrApplyMysticismTag(characteristic, 20, player);


            CharacteristicEntity entity = new CharacteristicEntity(ModEntities.CHARACTERISTIC.get(), event.getEntity().level(), characteristic.copy(), getPathwayId());
            entity.setSequenceId(getPathwayId());
            entity.moveTo(event.getEntity().position().offsetRandom(player.getRandom(), 1f).add(0, 1, 0));
            event.getEntity().level().addFreshEntity(entity);

            advance((getPathwayId() % 10 != 9) ? getPathwayId() + 1 : -1, player, true, true);
        }
    }
}
