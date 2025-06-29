package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.pathways.*;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.network.PacketDistributor;

@AutoRegisterCapability
public class EntityBeyonderManager {
    private float spirituality = 100;
    private float spiritualityCost = 0;
    private int maxSpirituality = 100;
    private int sanity = 100;

    private final BeyonderStats beyonderStats;
    private final PlayerAbilitiesManager abilitiesManager;
    private final PlayerEffectsManager effectsManager;
    private final PlayerLuckManager luckManager;
    private Beyonder pathway = new Beyonder(10);
    private final LivingEntity entity;
    private int syncCD = 20;

    public EntityBeyonderManager(LivingEntity entity){
        beyonderStats = new BeyonderStats();
        abilitiesManager = new PlayerAbilitiesManager();
        effectsManager = new PlayerEffectsManager();
        luckManager = new PlayerLuckManager();
        this.entity = entity;
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
        changeSpirituality(this.maxSpirituality/3f);
        if(sanity > 25) changeSanity(30);
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
                        new PlayerStatsSyncMessage(getBeyonderStats().getMiningSpeed()));
                if(syncCD-- < 0){
                    applyCost();
                    syncCD = 20;
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                            new PlayerSTCHudStatsSync(this.spirituality, this.maxSpirituality, this.sanity, this.getPathwayId(), getAbilitiesManager().enabledDisabled));
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
        if(entity instanceof Player player) getBeyonderStats().applyStats(player);
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
        advance(source.getPathwayId(), player, true, false);
        player.setHealth(player.getMaxHealth());
        this.abilitiesManager.copyFrom(source.getAbilitiesManager());
        this.abilitiesManager.onAcquireAbilities(this, player);
    }

    public void saveNBTData(CompoundTag nbt){
//        System.out.println("saving nbt data for beyonder capability...");
        nbt.putFloat("spirituality", spirituality);
        nbt.putInt("pathwayId", pathway.getId());
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
        setPathway(nbt.getInt("pathwayId"), false);
        this.luckManager.loadNBTData(nbt);
        this.effectsManager.loadNBTData(nbt);
        //enabledDisabled BEFORE onAcquire bc of reach ability
        this.abilitiesManager.loadNBTData(nbt, entity);
        this.abilitiesManager.loadEnabledListFromTag(nbt, this, entity);
        this.abilitiesManager.onAcquireAbilities(this, entity);
        //TODO make abilities manager actually save and load item abilities.
        //this.abilitiesManager.loadNBTData(nbt);
    }

    public void syncSequenceData(Player player, boolean advancing){
        if(!player.level().isClientSide()){
            System.out.println("syncing from server side");
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
}
