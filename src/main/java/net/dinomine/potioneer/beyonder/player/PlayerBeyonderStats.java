package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.pathways.*;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.PlayerSTCHudStatsSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AutoRegisterCapability
public class PlayerBeyonderStats {
    private float spirituality = 100;
    private float spiritualityCost = 0;
    private float spiritualityPerSecond = 0;
    private int maxSpirituality = 100;
    private int sanity = 100;
    private int acting = 0;

    private float miningSpeedMult = 1;
    public boolean mayFly = false;

    private ArrayList<BiConsumer<Player, PlayerBeyonderStats>> passiveAbilities = new ArrayList<>();
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

    public void onTick(Player player){
        setDefaultStats(player);
        //TODO default state for flying and whatnot should be set on the artifacts themselves
        //also make a TRUE set default function for advancing
        //could also try to have the abilities define dummy values and set the true variables to the final dummy values

        /*player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            passiveAbilities.forEach(a -> {
                a.accept(player, this);
            });
        });*/

        passiveAbilities.forEach(a -> {
            a.accept(player, this);
        });

        player.getAbilities().mayfly = this.mayFly;
        if(!this.mayFly) player.getAbilities().flying = false;


        if(!player.level().isClientSide()){
            if(syncCD-- < 0){
                syncCD = 20;
                applyCost();
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


    public boolean advance(int id, Player player, boolean sync, boolean advancing){
        if(id < 0){
            //setDefaultStats(player);
            this.pathway = new Beyonder(10);
            this.passiveAbilities = new ArrayList<>();
            if(sync) syncSequenceData(player, advancing);
            return true;
        }
        int seq = id%10;


        //setDefaultStats(player);
        setPathway(id, advancing);

        //not translated. either make it translatable or delete it for final version
        if(player.level().isClientSide && advancing){
            player.sendSystemMessage(Component.literal("Successfully advanced to Sequence " + String.valueOf(seq)
                    + " " + this.pathway.getSequenceName(seq, true) + "!"));
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
                    this.passiveAbilities = WheelOfFortunePathway.getPassiveAbilities(seq);
                    break;
                case 1:
                    this.pathway = new TyrantPathway(seq);
                    this.passiveAbilities = TyrantPathway.getPassiveAbilities(seq);
                    break;
                case 2:
                    this.pathway = new MysteryPathway(seq);
                    this.passiveAbilities = MysteryPathway.getPassiveAbilities(seq);
                    break;
                case 3:
                    this.pathway = new RedPriestPathway(seq);
                    this.passiveAbilities = RedPriestPathway.getPassiveAbilities(seq);
                    break;
                case 4:
                    this.pathway = new ParagonPathway(seq);
                    this.passiveAbilities = ParagonPathway.getPassiveAbilities(seq);
                    break;
            }
            this.maxSpirituality = this.pathway.getMaxSpirituality(seq);
            if(advancing) setSpirituality(this.maxSpirituality);
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
        setPathway(source.getPathwayId(), false);
    }

    public void saveNBTData(CompoundTag nbt){
        nbt.putFloat("spirituality", spirituality);
        nbt.putInt("pathwayId", pathway.getId());
    }

    public void loadNBTData(CompoundTag nbt){
        this.spirituality = nbt.getFloat("spirituality");
        setPathway(nbt.getInt("pathwayId"), false);
    }

    public void syncSequenceData(Player player, boolean advancing){
        if(!player.level().isClientSide()){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAdvanceMessage(this.pathway.getId(), advancing));
        } else {
            PacketHandler.INSTANCE.sendToServer(new PlayerAdvanceMessage(this.pathway.getId(), advancing));
        }
    }
}
