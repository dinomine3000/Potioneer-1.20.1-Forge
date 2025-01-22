package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.client.ClientAdvancementManager;
import net.dinomine.potioneer.beyonder.pathways.*;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.screen.AdvancementScreen;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAbilityInfoSyncSTC;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.PlayerSTCHudStatsSync;
import net.dinomine.potioneer.network.messages.PlayerStatsSyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.network.PacketDistributor;

@AutoRegisterCapability
public class EntityBeyonderManager {
    private float spirituality = 100;
    private float spiritualityCost = 0;
    private int maxSpirituality = 100;
    private int sanity = 100;
    private int acting = 0;

    private BeyonderStats beyonderStats;
    private PlayerAbilitiesManager abilitiesManager;
    private PlayerEffectsManager effectsManager;
    private Beyonder pathway = new Beyonder(10);
    private int syncCD = 20;

    public EntityBeyonderManager(){
        beyonderStats = new BeyonderStats();
        abilitiesManager = new PlayerAbilitiesManager();
        effectsManager = new PlayerEffectsManager();
    }

    public PlayerEffectsManager getEffectsManager(){
        return effectsManager;
    }

    public PlayerAbilitiesManager getAbilitiesManager(){
        return abilitiesManager;
    }

    public BeyonderStats getBeyonderStats(){
        return beyonderStats;
    }

    public int getSequenceLevel(){
        return pathway.getSequence();
    }

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

    public void playerSleep(){
        changeSpirituality(this.maxSpirituality/3f);
        if(sanity > 25) changeSanity(30);
    }

    public float getSpirituality(){
        return this.spirituality;
    }

    public void changeSpirituality(float val){
        setSpirituality(Mth.clamp(getSpirituality()+val, 0, maxSpirituality));
    }

    public void setSpirituality(float spirituality){
        if(spirituality < 0){
            this.spirituality = this.maxSpirituality;
        } else this.spirituality = spirituality;
    }

    public void requestSpiritualityCost(float cost){
        this.spiritualityCost += cost;
    }

    private void applyCost(){
        setSpirituality(Mth.clamp(Math.round((1000*(getSpirituality() - spiritualityCost/20f + maxSpirituality/1200f))) / 1000f,
                0f, this.maxSpirituality));
        this.spiritualityCost = 0;
    }

    public void onTick(LivingEntity entity){
        abilitiesManager.onTick(this, entity);
        effectsManager.onTick(this, entity);
        if(entity instanceof Player player){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerStatsSyncMessage(this.beyonderStats.getMiningSpeed()));
            if(syncCD-- < 0){
                syncCD = 20;
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PlayerSTCHudStatsSync(this.spirituality, this.maxSpirituality, this.sanity, this.getPathwayId(), getAbilitiesManager().enabledDisabled));
            }
        }
    }


    public boolean advance(int id, Player player, boolean sync, boolean advancing){
        System.out.println(getEffectsManager());
        this.abilitiesManager.clear(true, this, player);
        if(id < 0){
            //setDefaultStats(player);
            //getEffectsManager().clearEffects(this, player);
            //getAbilitiesManager().clear(true, this, player);
            this.pathway = new Beyonder(10);
            if(sync) syncSequenceData(player, advancing);
            return true;
        }
        int seq = id%10;


        //setDefaultStats(player);
        //getAbilitiesManager().clear(true, this, player);
        setPathway(id, advancing);
        if(!player.level().isClientSide()){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAbilityInfoSyncSTC(getAbilitiesManager().getPathwayActives().stream().map(Ability::getInfo).toList()));
        }


        //not translated. either make it translatable or delete it for final version
        if(!player.level().isClientSide && advancing){
            player.sendSystemMessage(Component.literal("Successfully advanced to Sequence " + String.valueOf(seq)
                    + " " + Beyonder.getSequenceNameFromId(id, true) + "!"));
        }
        if(sync) syncSequenceData(player, advancing);

        return true;
    }


    public void setPathway(int id, boolean advancing){
        if(id < 0){
            this.pathway = new Beyonder(10);
        } else {
            int pathway = Math.floorDiv(id, 10);
            int seq = id%10;
            switch(pathway){
                case 0:
                    this.pathway = new WheelOfFortunePathway(seq);
                    WheelOfFortunePathway.getAbilities(seq, getAbilitiesManager());
                    break;
                case 1:
                    this.pathway = new TyrantPathway(seq);
                    TyrantPathway.getAbilities(seq, getAbilitiesManager());
                    break;
                case 2:
                    this.pathway = new MysteryPathway(seq);
                    MysteryPathway.getAbilities(seq, getAbilitiesManager());
                    break;
                case 3:
                    this.pathway = new RedPriestPathway(seq);
                    RedPriestPathway.getAbilities(seq, getAbilitiesManager());
                    break;
                case 4:
                    this.pathway = new ParagonPathway(seq);
                    ParagonPathway.getAbilities(seq, getAbilitiesManager());
                    break;
            }
            this.maxSpirituality = this.pathway.getMaxSpirituality(seq);
            if(advancing) setSpirituality(this.maxSpirituality);
        }
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
        this.spirituality = source.getSpirituality();
        advance(source.getPathwayId(), player, true, false);
    }

    public void saveNBTData(CompoundTag nbt){
        nbt.putFloat("spirituality", spirituality);
        nbt.putInt("pathwayId", pathway.getId());
        //this.abilitiesManager.saveNBTData(nbt);
        this.effectsManager.saveNBTData(nbt);
        this.abilitiesManager.saveNBTData(nbt);
    }

    public void loadNBTData(CompoundTag nbt){
        this.spirituality = nbt.getFloat("spirituality");
        setPathway(nbt.getInt("pathwayId"), false);
        this.abilitiesManager.loadNBTData(nbt);
        //TODO make abilities manager actually save and load item abilities.
        //this.abilitiesManager.loadNBTData(nbt);
        this.effectsManager.loadNBTData(nbt);
    }

    public void syncSequenceData(Player player, boolean advancing){
        if(!player.level().isClientSide()){
            System.out.println("syncing from server side");
            //server side to client. messages are sent when client joins world and when he advanced by means controlled by the server
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAdvanceMessage(this.pathway.getId(), advancing));
        } else {
            //client side to server. messages are sent when client advances after succeeding in the minigame
            PacketHandler.INSTANCE.sendToServer(new PlayerAdvanceMessage(this.pathway.getId(), advancing));
        }
    }
}
