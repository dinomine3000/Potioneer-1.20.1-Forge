package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.pathways.Beyonder;
import net.dinomine.potioneer.beyonder.pathways.TyrantPathway;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.PlayerSTCHudStatsSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.function.Consumer;

@AutoRegisterCapability
public class PlayerBeyonderStats {
    private int spirituality = 100;
    private int maxSpirituality = 100;
    private int sanity = 100;
    private int acting = 0;

    private float miningSpeedMult = 1;
    public boolean mayFly = false;

    private ArrayList<Consumer<Player>> passiveAbilities = new ArrayList<>();
    private Beyonder pathway = new Beyonder(10);
    private int syncCD = 20;

    public int getSequenceLevel(){
        return pathway.getSequence();
    }

    public void multMiningSpeed(float mult){
        this.miningSpeedMult *= mult;
    }

    public void getMiningSpeed(PlayerEvent.BreakSpeed event){
        event.setNewSpeed(event.getOriginalSpeed()*miningSpeedMult);
    }

    public int getPathwayId(){
        return pathway.getId();
    }

    public boolean isBeyonder(){
        return this.pathway.getId() > -1;
    }

    public int getSpirituality(){
        return this.spirituality;
    }

    public void setSpirituality(int spirituality){
        this.spirituality = spirituality;
    }

    public void decreaseSpirituality(int sub){
        this.spirituality -= sub;
    }

    public void increaseSpirituality(int add){
        this.spirituality += add;
    }

    public void onTick(Player player){
        setDefaultStats(player);
        //TODO default state for flying and whatnot should be set on the artifacts themselves
        //also make a TRUE set default function for advancing
        //could also try to have the abilities define dummy values and set the true variables to the final dummy values
        passiveAbilities.forEach(a -> {
            a.accept(player);
        });
        player.getAbilities().mayfly = this.mayFly;
        if(!this.mayFly) player.getAbilities().flying = false;
        if(!player.level().isClientSide()){
            if(syncCD-- < 1){
                syncCD = 20;
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PlayerSTCHudStatsSync(this.spirituality, this.maxSpirituality, this.sanity, this.getPathwayId()));
            }
        }
    }


    //used to start the players anew every tick
    private void setDefaultStats(Player player){
        /*if(!player.isSpectator() && !player.isCreative()){
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
        }*/
        this.miningSpeedMult = 1;
        this.mayFly = player.isCreative() || player.isSpectator();
        if(!player.level().isClientSide()){
        }
    }


    public boolean advance(int id, Player player, boolean sync){
        if(id < 0){
            //setDefaultStats(player);
            this.pathway = new Beyonder(10);
            this.passiveAbilities = new ArrayList<>();
            if(sync) syncSequenceData(player);
            return true;
        }
        int seq = id%10;


        //setDefaultStats(player);
        setPathway(id);

        //not translated. either make it translatable or delete it for final version
        if(player.level().isClientSide){
            player.sendSystemMessage(Component.literal("Successfully advanced to Sequence " + String.valueOf(seq)
                    + " " + this.pathway.getSequenceName(seq, true) + "!"));
        }
        if(sync) syncSequenceData(player);

        return true;
    }


    public void setPathway(int id){
        if(id < 0){
            this.pathway = new Beyonder(10);
        } else {
            int pathway = Math.floorDiv(id, 10);
            int seq = id%10;
            switch(pathway){
                case 0:
                    this.pathway = new TyrantPathway(seq);
                    this.passiveAbilities = TyrantPathway.getPassiveAbilities(seq);
                    break;
                case 1:
                    this.pathway = new WheelOfFortunePathway(seq);
                    this.passiveAbilities = WheelOfFortunePathway.getPassiveAbilities(seq);
                    break;
            }
        }
    }

    public String getPathwayName(boolean capitalize){
        return capitalize ? this.pathway.getPathwayName() : this.pathway.getPathwayName().toLowerCase();
    }

    public int getPathwayColor(){
        return this.pathway.getColor();
    }

    public String getSequenceName(boolean show){
        return this.pathway.getSequenceName(show);
    }

    public void copyFrom(PlayerBeyonderStats source){

        this.spirituality = source.getSpirituality();
        setPathway(source.getPathwayId());
    }

    public void saveNBTData(CompoundTag nbt){
        nbt.putInt("spirituality", spirituality);
        nbt.putInt("pathwayId", pathway.getId());
    }

    public void loadNBTData(CompoundTag nbt){
        this.spirituality = nbt.getInt("spirituality");
        setPathway(nbt.getInt("pathwayId"));
    }

    public void syncSequenceData(Player player){
        if(!player.level().isClientSide()){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAdvanceMessage(this.pathway.getId()));
        } else {
            PacketHandler.INSTANCE.sendToServer(new PlayerAdvanceMessage(this.pathway.getId()));
        }
    }
}
