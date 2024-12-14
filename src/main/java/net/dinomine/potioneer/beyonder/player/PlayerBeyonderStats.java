package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.pathways.Beyonder;
import net.dinomine.potioneer.beyonder.pathways.TyrantPathway;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.sound.midi.SysexMessage;

@AutoRegisterCapability
public class PlayerBeyonderStats {
    private int spirituality;
    private float itemMiningSpeedMult = 1;
    private Beyonder pathway = new Beyonder(10);

    public int getSequence(){
        return pathway.getSequence();
    }

    public void getMiningSpeed(PlayerEvent.BreakSpeed event){
        //fix client syncing issues
        //only works on client side but client side cant get the values
        event.setNewSpeed(event.getOriginalSpeed()*itemMiningSpeedMult*pathway.getMiningSpeedMult());
    }

    public int getPathwayId(){
        return pathway.getId();
    }

    public boolean isBeyonder(){
        return this.pathway.getId() > 0;
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
        pathway.onTick(player);
    }



    public boolean advance(int id, Player player){
        if(id < 0){
            this.pathway = new Beyonder(10);
            player.removeAllEffects();
            return true;
        }
        int seq = id%10;

        player.removeAllEffects();
        setPathway(id);
        if(player.level().isClientSide){
            player.sendSystemMessage(Component.literal("Successfully advanced to Sequence " + String.valueOf(seq)
                    + " " + this.pathway.getName(seq) + "!"));
        }

        return true;
    }

    public void setPathway(int id){
        int pathway = Math.floorDiv(id, 10);
        int seq = id%10;
        switch(pathway){
            case 0:
                this.pathway = new TyrantPathway(seq);
                break;
            case 1:
                this.pathway = new WheelOfFortunePathway(seq);
                break;
        }
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
                    new PlayerAdvanceMessage(this.pathway.getId(), true));
        }
    }
}
