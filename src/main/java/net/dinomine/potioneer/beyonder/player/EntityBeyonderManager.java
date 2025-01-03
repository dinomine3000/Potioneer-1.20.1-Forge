package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.client.ClientAdvancementManager;
import net.dinomine.potioneer.beyonder.pathways.*;
import net.dinomine.potioneer.beyonder.screen.AdvancementScreen;
import net.dinomine.potioneer.network.PacketHandler;
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
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
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
        if(entity instanceof Player player){
            abilitiesManager.onTick(this, player);
            effectsManager.onTick(this, player);
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerStatsSyncMessage(this.beyonderStats.getMiningSpeed()));
            if(syncCD-- < 0){
                syncCD = 20;
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PlayerSTCHudStatsSync(this.spirituality, this.maxSpirituality, this.sanity, this.getPathwayId()));
            }
        } else {
            abilitiesManager.onTick(this, entity);
            effectsManager.onTick(this, entity);
        }
    }


    public boolean advance(int id, Player player, boolean sync, boolean advancing){
        this.abilitiesManager.clear(true, this, player);
        if(id < 0){
            //setDefaultStats(player);
            this.pathway = new Beyonder(10);
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
                    this.abilitiesManager.setPathwayPassives(WheelOfFortunePathway.getPassiveAbilities(seq));
                    break;
                case 1:
                    this.pathway = new TyrantPathway(seq);
                    this.abilitiesManager.setPathwayPassives(TyrantPathway.getPassiveAbilities(seq));
                    break;
                case 2:
                    this.pathway = new MysteryPathway(seq);
                    this.abilitiesManager.setPathwayPassives(MysteryPathway.getPassiveAbilities(seq));
                    break;
                case 3:
                    this.pathway = new RedPriestPathway(seq);
                    this.abilitiesManager.setPathwayPassives(RedPriestPathway.getPassiveAbilities(seq));
                    break;
                case 4:
                    this.pathway = new ParagonPathway(seq);
                    this.abilitiesManager.setPathwayPassives(ParagonPathway.getPassiveAbilities(seq));
                    break;
            }
            this.maxSpirituality = this.pathway.getMaxSpirituality(seq);
            if(advancing) setSpirituality(this.maxSpirituality);
        }
    }

    public void attemptAdvancement(int newSeq){
        //difference between the new sequence and current sequence
        //plus one more difficulty for every 25% sanity lost
        //plus 1 for each group of 9-7, 6-4 and 3-1 sequence levels
        //plus 1 or 2 for undigested potions
        ClientAdvancementManager.setDifficulty((Math.max(this.getSequenceLevel() - newSeq%10, 1) //adds the difference in levels. from 1 to 10
                + Math.round(4f-sanity/25f) //from 0 to 4 more points
                + 3-Math.floorDiv(newSeq%10, 3))); //adds from 0 to 3 points of difficulty
//        ClientAdvancementManager.difficulty = 10;     //Debug
        ClientAdvancementManager.targetSequence = newSeq;
        Minecraft.getInstance().setScreen(new AdvancementScreen());
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

    public void copyFrom(EntityBeyonderManager source, Player player){
        this.spirituality = source.getSpirituality();
        advance(source.getPathwayId(), player, true, false);
    }

    public void saveNBTData(CompoundTag nbt){
        nbt.putFloat("spirituality", spirituality);
        nbt.putInt("pathwayId", pathway.getId());
        //this.abilitiesManager.saveNBTData(nbt);
        this.effectsManager.saveNBTData(nbt);
    }

    public void loadNBTData(CompoundTag nbt){
        this.spirituality = nbt.getFloat("spirituality");
        setPathway(nbt.getInt("pathwayId"), false);
        //TODO make abilities manager actually save and load item abilities.
        //this.abilitiesManager.loadNBTData(nbt);
        this.effectsManager.loadNBTData(nbt);
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
